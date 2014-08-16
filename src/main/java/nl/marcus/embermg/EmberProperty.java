package nl.marcus.embermg;

import com.google.common.base.Preconditions;

public class EmberProperty {
	private final String name;
	private final EmberTypeRef typeRef;

	public EmberProperty(String name, EmberTypeRef typeRef) {
		super();
		this.name = Preconditions.checkNotNull(name);
		this.typeRef = Preconditions.checkNotNull(typeRef);
	}
	
	public String getName() {
		return name;
	}
	
	public EmberTypeRef getTypeRef() {
		return typeRef;
	}

	@Override
	public String toString() {
		return "EmberProperty [name=" + name + ", typeRef=" + typeRef + "]";
	}
}
