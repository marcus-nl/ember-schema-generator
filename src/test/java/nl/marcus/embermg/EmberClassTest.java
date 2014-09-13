package nl.marcus.embermg;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.assertj.guava.api.Assertions.assertThat;
import nl.marcus.embermg.zoo.Animal;
import nl.marcus.embermg.zoo.Lion;
import nl.marcus.embermg.zoo.Zoo;

import org.junit.Test;

public class EmberClassTest {

	@Test
	public void noInheritance() {
		EmberClass zoo = new EmberClass(Zoo.class, "AZoo");
		zoo.initializeSuperType(null);
		
		assertThat(zoo.getJavaClass()).isEqualTo(Zoo.class);
		assertThat(zoo.getName()).isEqualTo("AZoo");
		assertThat(zoo.getSuperType()).isAbsent();
		assertThat(zoo.getSuperTypeName()).isNull();
		assertThat(zoo.getOwnProperties()).isEmpty();

		zoo.addProperty("name", EmberTypeRef.STRING);
		zoo.addProperty("star", EmberTypeRef.forType("Animal"));
		zoo.addProperty("animals", EmberTypeRef.forCollection("Animal"));
		
		assertThat(zoo.getOwnProperties())
			.hasSize(3)
			.extracting("name").containsExactly("name", "star", "animals");
	}
	
	@Test
	public void withInheritance() {
		EmberClass animal = new EmberClass(Animal.class, "T_Animal");
		animal.initializeSuperType(null);
		animal.addProperty("name", EmberTypeRef.STRING);
		animal.addProperty("type", EmberTypeRef.STRING);
		
		assertThat(animal.getJavaClass()).isEqualTo(Animal.class);
		assertThat(animal.getName()).isEqualTo("T_Animal");
		assertThat(animal.getSuperType()).isAbsent();
		assertThat(animal.getSuperTypeName()).isNull();

		assertThat(animal.getOwnProperties())
			.hasSize(2)
			.extracting("name").containsExactly("name", "type");

		EmberClass lion = new EmberClass(Lion.class, "T_Lion");
		lion.initializeSuperType(animal);
		lion.addProperty("hasManes", EmberTypeRef.BOOLEAN);
		
		assertThat(lion.getSuperType()).isPresent();
		assertThat(lion.getSuperTypeName()).isEqualTo("T_Animal");

		assertThat(lion.getOwnProperties())
			.hasSize(1)
			.extracting("name").containsExactly("hasManes");
	}
	
	@Test
	public void getSuperType_illegalState() {
		EmberClass lion = new EmberClass(Lion.class, "Lion");
		try {
			lion.getSuperType();
			failBecauseExceptionWasNotThrown(IllegalStateException.class);
		}
		catch (IllegalStateException e) {
			assertThat(e).hasMessage("Super type was not set");
		}

		try {
			lion.getSuperTypeName();
			failBecauseExceptionWasNotThrown(IllegalStateException.class);
		}
		catch (IllegalStateException e) {
			assertThat(e).hasMessage("Super type was not set");
		}
	}
}
