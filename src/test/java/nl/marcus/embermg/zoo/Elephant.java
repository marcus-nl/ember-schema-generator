package nl.marcus.embermg.zoo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Elephant extends Animal {

	@JsonCreator
	public Elephant(@JsonProperty("name") String name) {
		super(name);
	}

	@Override
	public String getType() {
		return "herbivorous";
	}

	@JsonProperty
	public int getTrunkLength() {
		return 10;
	}

	@Override
	public String toString() {
		return "Elephant [getName()=" + getName() + 
				", getType()=" + getType() + 
				", getTrunkLength()=" + getTrunkLength() + 
				"]";
	}
}