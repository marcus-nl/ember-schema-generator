package nl.marcus.embermg;

import nl.marcus.embermg.EmberModelCollector;
import nl.marcus.embermg.EmberModelWriter;
import nl.marcus.embermg.jackson.ExplicitPropertiesFilter;
import nl.marcus.embermg.jackson.ExplicitPropertiesMixin;
import nl.marcus.embermg.zoo.Animal;
import nl.marcus.embermg.zoo.Zoo;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

public class EmberModelGeneratorTest {

	@Test
	public void jacksonCollector() {
		ObjectMapper objectMapper = createObjectMapper();
		EmberModelCollector collector = new EmberModelCollector(objectMapper);
		
		collector.addClass(Zoo.class);
		collector.addHierarchy(Animal.class);

		try (EmberModelWriter writer = new EmberModelWriter()) {
			collector.write(writer);
		}
	}

	private ObjectMapper createObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		
		objectMapper.setFilters(
				new SimpleFilterProvider()
					.addFilter("explicitProperties", new ExplicitPropertiesFilter())
			);
		
		objectMapper.addMixInAnnotations(Object.class, ExplicitPropertiesMixin.class);

		return objectMapper;
	}
}
