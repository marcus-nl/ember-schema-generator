package nl.marcus.embermg.zoo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Zoo {

	public String name;
	public String city;
	public Animal star;
	public List<Animal> animals;

	public Zoo(String name, String city, Animal star, List<Animal> animals) {
		this.name = name;
		this.city = city;
		this.star = star;
		this.animals = animals;
	}

	@JsonProperty
	public String getName() {
		return name;
	}
	
	@JsonProperty
	public String getCity() {
		return city;
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