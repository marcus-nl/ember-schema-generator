package nl.marcus.embermg;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmberSchema {

	private final List<EmberClass> emberClasses;

	public EmberSchema(List<EmberClass> emberClasses) {
		super();
		this.emberClasses = emberClasses;
	}
	
	@JsonProperty("classes")
	public List<EmberClass> getEmberClasses() {
		return emberClasses;
	}
}
