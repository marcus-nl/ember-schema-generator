package nl.marcus.embermg;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.guava.api.Assertions.assertThat;

import java.util.Collection;
import java.util.List;

import nl.marcus.embermg.jackson.ExplicitPropertiesFilter;
import nl.marcus.embermg.jackson.ExplicitPropertiesMixin;
import nl.marcus.embermg.zoo.Animal;
import nl.marcus.embermg.zoo.Elephant;
import nl.marcus.embermg.zoo.Zoo;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.google.common.collect.Ordering;

public class EmberSchemaGeneratorTest {

	private EmberSchemaGenerator generator;

	@Before
	public void init() {
		generator = new EmberSchemaGenerator(createObjectMapper());
	}
	
	@Test
	public void emptySchema() {
		EmberSchema schema = generator.getEmberSchema();
		assertThat(schema.getEmberClasses()).isEmpty();
	}
	
	@Test
	public void singleClass() {
		generator.addClass(Elephant.class);

		EmberSchema schema = generator.getEmberSchema();
		assertThat(schema.getEmberClasses()).hasSize(1);
		
		EmberClass elephant = schema.getEmberClasses().get(0);
		assertThat(elephant.getJavaClass()).isEqualTo(Elephant.class);
		assertThat(elephant.getName()).isEqualTo("Elephas"); // see Elephant's @JsonTypeName annotation.
		assertThat(elephant.getSuperType()).isAbsent();      // Elephant does have a super class, but it is not included in the generator.
		assertThat(elephant.getSuperTypeName()).isNull();
		
		// since the super class is not included, all properties are considered the its own.
		List<EmberProperty> properties = sort(elephant.getOwnProperties());
		assertThat(properties).hasSize(3);
		assertThat(properties)
			.extracting("name")
			.containsExactly("name", "trunkLength", "type");
		assertThat(properties)
			.extracting("typeRef")
			.containsExactly(EmberTypeRef.STRING, EmberTypeRef.NUMBER, EmberTypeRef.STRING);
	}
	
	@Test
	public void associatedClass() {
		generator.addClass(Zoo.class);
		
		EmberSchema schema = generator.getEmberSchema();
		assertThat(schema.getEmberClasses())
			.hasSize(2)
			.extracting("javaClass")
			.contains(Zoo.class, Animal.class);
	}
	
	@Test
	public void hierarchy_noSubsKnown() {
		generator.addHierarchy(NoSubsKnown_Base.class);
		
		EmberSchema schema = generator.getEmberSchema();
		assertThat(schema.getEmberClasses())
			.hasSize(1)
			.extracting("javaClass")
			.contains(NoSubsKnown_Base.class);
	}
	
	@Test
	public void hierarchy_subsAnnotated() {
		generator.addHierarchy(SubsAnnotated_Base.class);
		
		EmberSchema schema = generator.getEmberSchema();
		assertThat(schema.getEmberClasses())
			.hasSize(3)
			.extracting("javaClass")
			.contains(
				SubsAnnotated_Base.class, 
				SubsAnnotated_Sub1.class, 
				SubsAnnotated_Sub2.class
			);
	}
	
	@Test
	public void hierarchy_subsRegistered() {
		generator.addHierarchy(SubsRegistered_Base.class);
		
		EmberSchema schema = generator.getEmberSchema();
		assertThat(schema.getEmberClasses())
			.hasSize(3)
			.extracting("javaClass")
			.contains(
				SubsRegistered_Base.class, 
				SubsRegistered_Sub1.class, 
				SubsRegistered_Sub2.class
			)
			.doesNotContain(SubsRegistered_Sub3.class);
	}
	
	// sort by name since there is no predefined order
	private List<EmberProperty> sort(Collection<EmberProperty> properties) {
		return EmberPropertyOrdering.BY_NAME.sortedCopy(properties);
	}

	private ObjectMapper createObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(Feature.AUTO_CLOSE_TARGET, false);
		
		objectMapper.setFilters(
				new SimpleFilterProvider()
					.addFilter("explicitProperties", new ExplicitPropertiesFilter())
			);
		
		objectMapper.addMixInAnnotations(Object.class, ExplicitPropertiesMixin.class);
		
		objectMapper.registerSubtypes(new Class[] { SubsRegistered_Sub1.class, SubsRegistered_Sub2.class });

		return objectMapper;
	}
}

abstract class EmberPropertyOrdering extends Ordering<EmberProperty> {
	public static EmberPropertyOrdering BY_NAME = new EmberPropertyOrdering() {
		@Override
		public int compare(EmberProperty left, EmberProperty right) {
			return left.getName().compareTo(right.getName());
		}
	};
}

class NoSubsKnown_Base {}
class NoSubsKnown_Sub1 extends NoSubsKnown_Base {}
class NoSubsKnown_Sub2 extends NoSubsKnown_Base {}

@JsonSubTypes({ 
	@Type(value = SubsAnnotated_Sub1.class, name = "Sub1"), 
	@Type(value = SubsAnnotated_Sub2.class, name = "Sub2") 
})
class SubsAnnotated_Base {}
class SubsAnnotated_Sub1 extends SubsAnnotated_Base {}
class SubsAnnotated_Sub2 extends SubsAnnotated_Base {}

class SubsRegistered_Base {}
class SubsRegistered_Sub1 extends SubsRegistered_Base {}
class SubsRegistered_Sub2 extends SubsRegistered_Base {}
class SubsRegistered_Sub3 extends SubsRegistered_Base {} // not registered
