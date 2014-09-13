package nl.marcus.embermg;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

/**
 * Represents a property of an Ember class.
 * 
 * @author Marcus Klimstra
 */
public class EmberProperty {
	
	private final String name;
	private final EmberTypeRef typeRef;

	/**
	 * Creates a new EmberProperty with the specified name and type.
	 */
	public EmberProperty(String name, EmberTypeRef typeRef) {
		super();
		this.name = Preconditions.checkNotNull(name);
		this.typeRef = Preconditions.checkNotNull(typeRef);
	}
	
	/**
	 * The property name.
	 */
	@JsonProperty
	public String getName() {
		return name;
	}
	
	/**
	 * The property type.
	 */
	@JsonProperty("type")
	public EmberTypeRef getTypeRef() {
		return typeRef;
	}

	@Override
	public String toString() {
		return "EmberProperty [name=" + name + ", typeRef=" + typeRef + "]";
	}
}
