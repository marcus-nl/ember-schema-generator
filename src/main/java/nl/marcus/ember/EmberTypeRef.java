package nl.marcus.ember;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

/**
 * A type reference inside an Ember schema.
 * <p>
 * For example, a numeric type is represented by an instance of this class with kind "attr" and name "number".
 * The JSON representation is <code>{ "kind" : "attr", "name" : "boolean" }</code>.
 * This corresponds to an Ember Data type declaration of "attr('number')". 
 * <p>
 * A (non-collection) object type "Foo" is represented by instance of this class with kind "one" and name "Foo".  
 * By default this corresponds to an Ember Data type declaration of "DS.hasOne('foo')". 
 * <p>
 * For collection types the kind is "many". The corresponding Ember Data type declaration would be, for example, "DS.forMany('foo')" by default.
 * <p>
 * Note that since this is a <em>declarative</em> representation of the type reference, the schema loader 
 * (see <a href="https://github.com/marcus-nl/ember-schema-loader">Ember Schema Loader</a>)
 * can decide how exactly to handle it. This means that by default the kind "one" will be translated to "DS.hasOne(...)",
 * but it can also use <a href="https://github.com/lytics/ember-data.model-fragments">Ember Data: Model Fragments</a> 
 * by translating it to "DS.hasOneFragment(...)".
 * <p>
 * 
 * @author Marcus Klimstra
 */
public final class EmberTypeRef {
	
	/**
	 * Represents a string attribute type.
	 */
	public static final EmberTypeRef STRING  = new EmberTypeRef("attr", "string");
	
	/**
	 * Represents a number attribute type.
	 */
	public static final EmberTypeRef NUMBER  = new EmberTypeRef("attr", "number");
	
	/**
	 * Represents a boolean attribute type.
	 */
	public static final EmberTypeRef BOOLEAN = new EmberTypeRef("attr", "boolean");
	
	/**
	 * Returns an {@link EmberTypeRef} for a non-collection object type with the specified name.
	 */
	public static EmberTypeRef forType(String name) {
		return new EmberTypeRef("one", name);
	}

	/**
	 * Returns an {@link EmberTypeRef} for a collection type with the specified name.
	 */
	public static EmberTypeRef forCollection(String name) {
		return new EmberTypeRef("many", name);
	}
	
	private final String kind;
	private final String name;

	private EmberTypeRef(String kind, String name) {
		super();
		this.kind = kind;
		this.name = name;
	}

	/**
	 * Returns the type reference kind: 'attr', 'one' or 'many'.
	 */
	@JsonProperty
	public String getKind() {
		return kind;
	}

	/**
	 * Returns the type name.
	 */
	@JsonProperty("name")
	public String getName() {
		return name;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return Objects.hashCode(kind, name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof EmberTypeRef)) return false;
		
		EmberTypeRef other = (EmberTypeRef) obj;
		return Objects.equal(this.kind, other.kind)
			&& Objects.equal(this.name, other.name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "EmberTypeRef[" + kind + "|" + name + "]";
	}
}
