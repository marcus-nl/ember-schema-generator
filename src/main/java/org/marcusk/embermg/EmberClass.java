package org.marcusk.embermg;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;

public class EmberClass {

	private final EmberTypeRef ref;
	private final EmberTypeRef superType;
	private final List<EmberProperty> properties;
	
	public EmberClass(EmberTypeRef ref, EmberTypeRef superType) {
		super();
		this.ref = ref;
		this.superType = superType;
		this.properties = new ArrayList<EmberProperty>();
	}
	
	public String getName() {
		return ref.getName();
	}

	public EmberTypeRef getTypeRef() {
		return ref;
	}
	
	public EmberTypeRef getSuperType() {
		return superType;
	}
	
	public void addProperty(String name, EmberTypeRef typeRef) {
		Preconditions.checkNotNull(name);
		Preconditions.checkNotNull(typeRef);
		
		EmberProperty property = new EmberProperty(name, typeRef);
		properties.add(property);
	}

	public void emit(EmberModelWriter writer) {
		writer.startModel(this);

		for (EmberProperty p : properties) {
			writer.addProperty(p.getName(), p.getTypeRef().getName());
		}
		
		writer.endModel();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("EmberClass ").append(getName()).append(" [\n");
		
		for (EmberProperty p : properties) {
			sb.append("- ").append(p).append("\n");
		}
		
		sb.append("]\n");
		return sb.toString();
	}
}
