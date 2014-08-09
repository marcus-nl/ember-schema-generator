package org.marcusk.embermg.zoo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = As.PROPERTY, property = "@class")
@JsonSubTypes({ @Type(value = Lion.class, name = "lion"), @Type(value = Elephant.class, name = "elephant") })
public abstract class Animal {
	@JsonProperty("name")
	String name;

	@JsonProperty("sound")
	String sound;

	@JsonProperty("type")
	String type;

	@JsonProperty("endangered")
	boolean endangered;

}