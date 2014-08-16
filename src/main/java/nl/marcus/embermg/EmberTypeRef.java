package nl.marcus.embermg;

import com.google.common.base.Preconditions;


public class EmberTypeRef {
	public static final EmberTypeRef STRING = new EmberTypeRef("attr", "string");
	public static final EmberTypeRef NUMBER = new EmberTypeRef("attr", "number");
	public static final EmberTypeRef BOOLEAN = new EmberTypeRef("attr", "boolean");
	
	public static EmberTypeRef forType(String s) {
		return new EmberTypeRef("fragment", s);
	}
	
	public static EmberTypeRef forCollection(String s) {
		return new EmberTypeRef("fragments", s);
	}
	
	private final String kind;
	private final String name;

	private EmberTypeRef(String kind, String name) {
		super();
		this.kind = kind;
		this.name = name;
	}

	/**
	 * Capitalized.
	 */
	public String getUpperName() {
		return name;
	}
	
	/**
	 * Capitalized.
	 */
	public String getLowerName() {
		return toLowerName(name);
	}

	private static String toLowerName(String s) {
		Preconditions.checkNotNull(s);
		Preconditions.checkArgument(!s.isEmpty());
		return Character.toLowerCase(s.charAt(0)) + s.substring(1);
	}

	public String getDeclaration() {
		return "DS." + kind + "('" + getLowerName() + "')";
	}
	
	@Override
	public String toString() {
		return "EmberTypeRef[" + name + "]";
	}
}
