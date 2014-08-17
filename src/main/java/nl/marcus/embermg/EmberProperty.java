package nl.marcus.embermg;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

public class EmberProperty {
	
	private final String name;
	private final EmberTypeRef typeRef;

	public EmberProperty(String name, EmberTypeRef typeRef) {
		super();
		this.name = Preconditions.checkNotNull(name);
		this.typeRef = Preconditions.checkNotNull(typeRef);
	}
	
	@JsonProperty
	public String getName() {
		return name;
	}
	
	@JsonProperty("type")
	public EmberTypeRef getTypeRef() {
		return typeRef;
	}

	@Override
	public String toString() {
		return "EmberProperty [name=" + name + ", typeRef=" + typeRef + "]";
	}
}
