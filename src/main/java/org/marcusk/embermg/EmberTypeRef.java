package org.marcusk.embermg;

public class EmberTypeRef {
	public static final EmberTypeRef STRING = new EmberTypeRef("DS.attr('string')");
	public static final EmberTypeRef NUMBER = new EmberTypeRef("DS.attr('number')");
	public static final EmberTypeRef BOOLEAN = new EmberTypeRef("DS.attr('boolean')");
	
	public static EmberTypeRef forAsdf(String s) {
		String t = "DS.fragment('" + s + "')";
		return new EmberTypeRef(t);
	}
	
	public static EmberTypeRef forCollection(String s) {
		String t = "DS.fragments('" + s + "')";
		return new EmberTypeRef(t);
	}
	
	private final String name;

	private EmberTypeRef(String name) {
		super();
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return "EmberTypeRef[" + name + "]";
	}
}
