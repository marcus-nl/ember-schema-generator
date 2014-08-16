package nl.marcus.embermg.zoo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Lion extends Animal {

	@JsonCreator
	public Lion(@JsonProperty("name") String name) {
		super(name);
	}

	@Override
	public String getType() {
		return "carnivorous";
	}
	
	@JsonProperty
	public boolean hasManes() {
		return true;
	}

	@Override
	public String toString() {
		return "Lion [getName()=" + getName() + 
				", getType()=" + getType() + 
				", hasManes()=" + hasManes() + 
				"]";
	}
}