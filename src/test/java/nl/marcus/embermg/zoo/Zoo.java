package nl.marcus.embermg.zoo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = As.PROPERTY, property = "@class")
public class Zoo {

	public String name;
	public String city;
	public Animal star;
	public List<Animal> animals;

	@JsonCreator
	public Zoo(@JsonProperty("name") String name, @JsonProperty("city") String city) {
		this.name = name;
		this.city = city;
	}
	
	@JsonProperty
	public Animal getStar() {
		return star;
	}
	
	@JsonProperty
	public List<Animal> getAnimals() {
		return animals;
	}

	@Override
	public String toString() {
		return "Zoo [name=" + name + ", city=" + city + ", star=" + star + ", animals=" + animals + "]";
	}
}