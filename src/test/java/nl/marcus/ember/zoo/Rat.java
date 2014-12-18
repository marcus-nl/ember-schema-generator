package nl.marcus.ember.zoo;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import nl.marcus.ember.EmberIgnore;

import java.util.HashMap;
import java.util.Map;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class Rat {
	// Explicitly mark this as Json property for this test
	// Jackson will find it even without it in the default configuration
	@EmberIgnore
	@JsonProperty
	public Map<String, String> getLinks() {
		Map<String, String> links = new HashMap<String, String>();
		links.put("parent", "parent");
		return links;
	}

	@JsonProperty
	public String getFamily() {
		return "Muridae";
	}
}
