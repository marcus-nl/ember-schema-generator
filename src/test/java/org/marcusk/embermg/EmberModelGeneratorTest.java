package org.marcusk.embermg;

import org.junit.Test;
import org.marcusk.embermg.zoo.Animal;
import org.marcusk.embermg.zoo.Zoo;

import com.fasterxml.jackson.databind.ObjectMapper;

public class EmberModelGeneratorTest {

	@Test
	public void jacksonCollector() {
		ObjectMapper objectMapper = new ObjectMapper();
		EmberTypeRegistry typeRegistry = new EmberTypeRegistry();
		EmberModelCollector collector = new EmberModelCollector(objectMapper, typeRegistry);
		
		collector.processClass(Zoo.class);
		collector.processHierarchy(Animal.class);

		try (EmberModelWriter writer = new EmberModelWriter()) {
			typeRegistry.emit(writer);
		}
	}
}
