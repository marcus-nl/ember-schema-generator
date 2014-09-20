package nl.marcus.ember;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

/**
 * Represents a class inside an Ember schema.
 * 
 * A class has a name, properties and an optional super type.
 * 
 * @author Marcus Klimstra
 */
public class EmberClass {

	private final Class<?> javaClass;
	private final String name;
	private final Map<String,EmberProperty> properties;
	private Optional<EmberClass> _superType; // set by initializeSuperType.

	public EmberClass(Class<?> javaClass, String name) {
		super();
		this.javaClass = Preconditions.checkNotNull(javaClass);
		this.name = Preconditions.checkNotNull(name);
		this.properties = new LinkedHashMap<>();
	}
	
	@JsonIgnore
	public Class<?> getJavaClass() {
		return javaClass;
	}
	
	@JsonProperty
	public String getName() {
		return name;
	}

	@JsonProperty("superType")
	public String getSuperTypeName() {
		if (getSuperType().isPresent()) {
			return getSuperType().get().getName();
		}
		return null;
	}
	
	@JsonIgnore
	public Optional<EmberClass> getSuperType() {
		if (_superType == null) {
			throw new IllegalStateException("Super type was not set");
		}
		return _superType;
	}
	
	public void initializeSuperType(EmberClass superType) {
		this._superType = Optional.fromNullable(superType);
	}
	
	/**
	 * @return this class' own (non-inherited) properties.
	 */
	@JsonProperty("props")
	public List<EmberProperty> getOwnProperties() {
		ImmutableList.Builder<EmberProperty> builder = ImmutableList.builder();
		for (EmberProperty property : properties.values()) {
			if (!isInheritedProperty(property.getName())) {
				builder.add(property);
			}
		}
		return builder.build();
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
