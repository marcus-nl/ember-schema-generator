package nl.marcus.embermg;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmberTypeRef {
	
	public static final EmberTypeRef STRING  = new EmberTypeRef("attr", "string");
	public static final EmberTypeRef NUMBER  = new EmberTypeRef("attr", "number");
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

	@JsonProperty
	public String getKind() {
		return kind;
	}
	
	@JsonProperty("name")
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return "EmberTypeRef[" + kind + "|" + name + "]";
	}
}
