package nl.marcus.ember;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

/**
 * Registry of known mappings from {@link Class} to {@link EmberClass}.
 * 
 * @author Marcus Klimstra
 */
class EmberTypeRegistry {

	private final Map<Class<?>, EmberClass> types;
	
	public EmberTypeRegistry() {
		this.types = new LinkedHashMap<>();
	}
	
	public List<EmberClass> getEmberClasses() {
		return ImmutableList.copyOf(types.values());
	}
	
	public EmberClass getEmberClass(Class<?> javaClass) {
		Preconditions.checkNotNull(javaClass);
		return types.get(javaClass);
	}
	
	public void register(Class<?> javaClass, EmberClass emberClass) {
		Preconditions.checkNotNull(javaClass);
		Preconditions.checkNotNull(emberClass);
		types.put(javaClass, emberClass);
	}
}
