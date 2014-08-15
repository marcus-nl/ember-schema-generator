package org.marcusk.embermg;

import org.junit.Test;
import org.marcusk.embermg.jackson.ExplicitPropertiesMixin;
import org.marcusk.embermg.jackson.ExplicitPropertiesFilter;
import org.marcusk.embermg.zoo.Animal;
import org.marcusk.embermg.zoo.Zoo;

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
