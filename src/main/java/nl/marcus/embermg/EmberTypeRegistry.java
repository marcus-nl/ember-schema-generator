package nl.marcus.embermg;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

public class EmberTypeRegistry {

	private final Map<Class<?>, EmberTypeRef> refs = new HashMap<>();
	private final Map<EmberTypeRef, EmberClass> types = new LinkedHashMap<>();
	
	public List<EmberClass> getEmberClasses() {
		return ImmutableList.copyOf(types.values());
	}

	public EmberTypeRef getTypeRef(Class<?> javaClass) {
		Preconditions.checkNotNull(javaClass);
		EmberTypeRef ref = refs.get(javaClass);
		if (ref == null) {
			ref = EmberTypeRef.forType(javaClass.getSimpleName());
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
}
