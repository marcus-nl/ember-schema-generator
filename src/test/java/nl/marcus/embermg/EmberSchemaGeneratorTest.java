package nl.marcus.embermg;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.guava.api.Assertions.assertThat;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import nl.marcus.embermg.jackson.ExplicitPropertiesFilter;
import nl.marcus.embermg.jackson.ExplicitPropertiesMixin;
import nl.marcus.embermg.zoo.Animal;
import nl.marcus.embermg.zoo.Elephant;
import nl.marcus.embermg.zoo.Lion;
import nl.marcus.embermg.zoo.Zoo;

import org.assertj.core.api.Condition;
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

	@Test
	public void emptySchema() {
		EmberSchema schema = generator.getEmberSchema();
		assertThat(schema.getEmberClasses()).isEmpty();
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
	
	@Test
	public void singleClass() {
		generator.addClass(Elephant.class);

		EmberSchema schema = generator.getEmberSchema();
		assertThat(schema.getEmberClasses()).hasSize(1);
		
		EmberClass elephant = schema.getEmberClasses().get(0);
		assertThat(elephant)
			.is(emberClass(
				"Elephas",		// see Elephant's @JsonTypeName annotation.
				Elephant.class,
				null			// Elephant *does* have a super class, but it is not included in the schema.
			))
			// since the super class is not included, all properties are considered its own.
			.has(sortedProperties(
				property("name"),
				property("trunkLength"),
				property("type")
			));
	}

	@Test
	public void zoo() {
		generator.addClass(Zoo.class);
		generator.addHierarchy(Animal.class);
		
		EmberSchema schema = generator.getEmberSchema();
		List<EmberClass> classes = sortClasses(schema.getEmberClasses());
		assertThat(schema.getEmberClasses()).hasSize(4);
		
		Iterator<EmberClass> it = classes.iterator();
		EmberClass animal = it.next();
		EmberClass elephant = it.next();
		EmberClass lion = it.next();
		EmberClass zoo = it.next();
		
		assertThat(zoo)
			.is(emberClass(
				"Zoo", 
				Zoo.class,
				null
			))
			.has(sortedProperties(
				property("animals"),
				property("city"),
				property("name"),
				property("star")
			));
		
		assertThat(animal)
			.is(emberClass(
				"Animal",
				Animal.class,
				null
			))
			.has(sortedProperties(
				property("name"),
				property("type")
			));

		assertThat(elephant)
			.is(emberClass(
				"Elephas",
				Elephant.class,
				Animal.class
			))
			.has(sortedProperties(
				property("trunkLength")
			));
		
		assertThat(lion)
			.is(emberClass(
				"Lion",
				Lion.class,
				Animal.class
			))
			.has(sortedProperties(
				property("hasManes")
			));
	}
	
	// === helpers === //
	
	private EmberClassCondition emberClass(final String name, final Class<?> type, final Class<?> superType) {
		return new EmberClassCondition() {
			@Override
			public boolean matches(EmberClass cls) {
				assertThat(cls.getName()).isEqualTo(name);
				assertThat(cls.getJavaClass()).isEqualTo(type);

				if (superType == null) {
					assertThat(cls.getSuperType()).isAbsent();
					assertThat(cls.getSuperTypeName()).isNull();
				}
				else {
					assertThat(cls.getSuperType()).isPresent();
					assertThat(cls.getSuperType().get().getJavaClass()).isEqualTo(superType);
				}
				
				return true;
			}
		};
	}
	
	private EmberClassCondition sortedProperties(final EmberPropertyCondition... props) {
		return new EmberClassCondition() {
			@Override
			public boolean matches(EmberClass cls) {
				List<EmberProperty> properties = sortProperties(cls.getOwnProperties());
				assertThat(properties).as("Properties of " + cls.getName()).hasSize(props.length);
				
				for (int i = 0; i < props.length; i++) {
					assertThat(properties.get(i)).as("Property " + i).is(props[i]);
				}
				
				return true;
			}
		};
	}
	
	private EmberPropertyCondition property(final String name) {
		return new EmberPropertyCondition() {
			@Override
			public boolean matches(EmberProperty property) {
				assertThat(property.getName()).isEqualTo(name);
				return true;
			}
		};
	}
	
	// sort classes by name since there is no predefined order
	private List<EmberClass> sortClasses(Collection<EmberClass> properties) {
		return EmberClassOrdering.BY_NAME.sortedCopy(properties);
	}

	// sort properties by name since there is no predefined order
	private List<EmberProperty> sortProperties(Collection<EmberProperty> properties) {
		return EmberPropertyOrdering.BY_NAME.sortedCopy(properties);
	}
}

abstract class EmberClassOrdering extends Ordering<EmberClass> {
	public static EmberClassOrdering BY_NAME = new EmberClassOrdering() {
		@Override
		public int compare(EmberClass left, EmberClass right) {
			return left.getName().compareTo(right.getName());
		}
	};
}

abstract class EmberPropertyOrdering extends Ordering<EmberProperty> {
	public static EmberPropertyOrdering BY_NAME = new EmberPropertyOrdering() {
		@Override
		public int compare(EmberProperty left, EmberProperty right) {
			return left.getName().compareTo(right.getName());
		}
	};
}

abstract class EmberClassCondition extends Condition<EmberClass> {}

abstract class EmberPropertyCondition extends Condition<EmberProperty> {}

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
