package nl.marcus.embermg;

import static org.assertj.core.api.Assertions.assertThat;
import nl.marcus.embermg.zoo.Animal;
import nl.marcus.embermg.zoo.Lion;

import org.junit.Before;
import org.junit.Test;

public class EmberTypeRegistryTest {

	private EmberTypeRegistry typeRegistry;

	@Before
	public void init() {
		typeRegistry = new EmberTypeRegistry();

		typeRegistry.register(
				Animal.class, 
				new EmberClass(Animal.class, "T_Animal")
			);
	}

	@Test
	public void getEmberClasses() {
		assertThat(typeRegistry.getEmberClasses())
			.hasSize(1)
			.extracting("name")
			.containsExactly("T_Animal");
	}
	
	@Test
	public void getEmberClass() {
		EmberClass cls = typeRegistry.getEmberClass(Animal.class);
		assertThat(cls).isNotNull();
		assertThat(cls.getName()).isEqualTo("T_Animal");
	}
	
	@Test
	public void register() {
		assertThat(typeRegistry.getEmberClass(Lion.class)).isNull();

		typeRegistry.register(
				Lion.class, 
				new EmberClass(Lion.class, "T_Lion")
			);

		assertThat(typeRegistry.getEmberClass(Lion.class)).isNotNull();
	}
}
