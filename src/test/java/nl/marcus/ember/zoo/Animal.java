package nl.marcus.ember.zoo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = As.PROPERTY, property = "@class")
@JsonSubTypes({ 
	@Type(value = Lion.class, name = "lion"), 
	@Type(value = Elephant.class, name = "elephant") 
})
public abstract class Animal {
	
	private final String name;

	protected Animal(String name) {
		this.name = name;
	}
	
	@JsonProperty("name")
	public final String getName() {
		return name;
	}

	@JsonProperty("type")
	public abstract String getType();
	
	public String getFoo() {
		return "foo";
	}
}