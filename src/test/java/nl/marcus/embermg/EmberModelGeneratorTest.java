package nl.marcus.embermg;

import java.io.IOException;

import nl.marcus.embermg.jackson.ExplicitPropertiesFilter;
import nl.marcus.embermg.jackson.ExplicitPropertiesMixin;
import nl.marcus.embermg.zoo.Animal;
import nl.marcus.embermg.zoo.Zoo;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

public class EmberModelGeneratorTest {

	@Test
	public void jacksonCollector() throws IOException {
		ObjectMapper objectMapper = createObjectMapper();
		EmberModelCollector collector = new EmberModelCollector(objectMapper);
		
		collector.addClass(Zoo.class);
		collector.addHierarchy(Animal.class);

		JsonGenerator jgen = objectMapper.getFactory()
				.createGenerator(System.out)
				.useDefaultPrettyPrinter();
		
		jgen.writeObject(collector.getEmberClasses());
		
		jgen.close();
	}

	private ObjectMapper createObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(Feature.AUTO_CLOSE_TARGET, false);
		
		objectMapper.setFilters(
				new SimpleFilterProvider()
					.addFilter("explicitProperties", new ExplicitPropertiesFilter())
			);
		
		objectMapper.addMixInAnnotations(Object.class, ExplicitPropertiesMixin.class);

		return objectMapper;
	}
}
