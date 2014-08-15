package org.marcusk.embermg;

public class EmberTypeRef {
	public static final EmberTypeRef STRING = new EmberTypeRef("attr", "string");
	public static final EmberTypeRef NUMBER = new EmberTypeRef("attr", "number");
	public static final EmberTypeRef BOOLEAN = new EmberTypeRef("attr", "boolean");
	
	public static EmberTypeRef forType(Class<?> javaClass) {
		return forType(javaClass.getSimpleName());
	}
	
	public static EmberTypeRef forType(String s) {
		return new EmberTypeRef("fragment", s);
	}
	
	public static EmberTypeRef forCollection(String s) {
		return new EmberTypeRef("fragments", s);
	}
	
	private final String kind;
	private final String name;
	// fullName
	// shortName

	private EmberTypeRef(String kind, String name) {
		super();
		this.kind = kind;
		this.name = name;
	}
	
	public String getFullName() {
		return name;
	}
	
	public String getDeclaration() {
		return "DS." + kind + "('" + name + "')";
	}
	
	@Override
	public String toString() {
		return "EmberTypeRef[" + name + "]";
	}
}
