package nl.marcus.embermg;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents an Ember schema, containing a list of {@link EmberClass}es.
 * 
 * @author Marcus Klimstra
 */
public class EmberSchema {

	private final List<EmberClass> emberClasses;

	/**
	 * Creates a new instance with the specified list of {@link EmberClass}es.
	 */
	public EmberSchema(List<EmberClass> emberClasses) {
		super();
		this.emberClasses = emberClasses;
	}
	
	/**
	 * The list of {@link EmberClass}es.
	 */
	@JsonProperty("classes")
	public List<EmberClass> getEmberClasses() {
		return emberClasses;
	}
}
