package org.marcusk.embermg;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public class EmberClass {

	private final Class<?> javaClass;
	private final EmberTypeRef ref;
	private final Map<String,EmberProperty> properties;

	public EmberClass(Class<?> javaClass, EmberTypeRef ref) {
		super();
		this.javaClass = Preconditions.checkNotNull(javaClass);
		this.ref = Preconditions.checkNotNull(ref);
		this.properties = new LinkedHashMap<>();
	}
	
	public Class<?> getJavaClass() {
		return javaClass;
	}
	
	public String getName() {
		return ref.getFullName();
	}

	/**
	 * @return this class' own (non-inherited) properties.
	 */
	public Iterable<EmberProperty> ownProperties() {
		return Iterables.filter(properties.values(), new Predicate<EmberProperty>() {
			@Override
			public boolean apply(EmberProperty p) {
				return !isInheritedProperty(p.getName());
			}
		});
	}
	
	private boolean hasProperty(String name) {
		return properties.containsKey(name)
			|| isInheritedProperty(name);
	}
	
	private boolean isInheritedProperty(String name) {
		if (getSuperType().isPresent()) {
			return getSuperType().get().hasProperty(name);
		}
		return false;
	}
	
	public void addProperty(String name, EmberTypeRef typeRef) {
		Preconditions.checkNotNull(name);
		Preconditions.checkNotNull(typeRef);
		
		EmberProperty property = new EmberProperty(name, typeRef);
		properties.put(name, property);
	}
	
	// initialized by EmberModelCollector:

	private Optional<EmberClass> _superType = null;
	
	public Optional<EmberClass> getSuperType() {
		if (_superType == null) {
			throw new IllegalStateException();
		}
		return _superType;
	}
	
	public void initializeSuperType(EmberClass superType) {
		this._superType = Optional.fromNullable(superType);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("EmberClass ").append(getName()).append(" [\n");
		
		for (EmberProperty p : properties.values()) {
			sb.append("- ").append(p).append("\n");
		}
		
		sb.append("]\n");
		return sb.toString();
	}
}
