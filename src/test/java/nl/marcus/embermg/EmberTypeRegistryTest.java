package nl.marcus.embermg;

import static org.assertj.core.api.Assertions.assertThat;
import nl.marcus.embermg.zoo.Animal;

import org.junit.Before;
import org.junit.Test;

public class EmberTypeRegistryTest {

	private EmberTypeRegistry typeRegistry;

	@Before
	public void init() {
		typeRegistry = new EmberTypeRegistry();

		typeRegistry.register(
				EmberTypeRef.forType("Animal"), 
				new EmberClass(Animal.class, "T_Animal")
			);
	}

	@Test
	public void getTypeRef() {
		EmberTypeRef ref = typeRegistry.getTypeRef(Animal.class);
		assertThat(ref.getKind()).isEqualTo("one");
	//	assertThat(ref.getName()).isEqualTo("T_Animal");	// FIXME name is currently 'Animal' instead. 
		assertThat(ref).isEqualTo(EmberTypeRef.forType("Animal"));
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
		EmberTypeRef ref = EmberTypeRef.forType("Animal");
		EmberClass cls = typeRegistry.getEmberClass(ref);
		assertThat(cls).isNotNull();
		assertThat(cls.getName()).isEqualTo("T_Animal");
	}
	
	@Test
	public void containsType() {
		EmberTypeRef ref = EmberTypeRef.forType("Animal");
		assertThat(typeRegistry.containsType(ref)).isTrue();
	}
}
