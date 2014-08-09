package org.marcusk.embermg;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class EmberTypeRegistry {

	private final Map<Class<?>, EmberTypeRef> refs = new HashMap<>();
	private final Map<EmberTypeRef, EmberClass> types = new LinkedHashMap<>();
	
	
	public EmberTypeRef getTypeRef(Class<?> javaClass) {
		EmberTypeRef ref = refs.get(javaClass);
		if (ref == null) {
			ref = EmberTypeRef.forAsdf(javaClass.getSimpleName());
			refs.put(javaClass, ref);
		}
		return ref;
	}
	
	public EmberClass getEmberClass(EmberTypeRef ref) {
		return types.get(ref);
	}
	
	public boolean containsType(EmberTypeRef typeRef) {
		return types.containsKey(typeRef);
	}

	public void register(EmberTypeRef ref, EmberClass emberClass) {
		types.put(ref, emberClass);
	}
	
	public void emit(EmberModelWriter writer) {
		for (EmberClass c : types.values()) {
			c.emit(writer);
		}
	}
}
