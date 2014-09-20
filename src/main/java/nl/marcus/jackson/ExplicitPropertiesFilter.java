package nl.marcus.jackson;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;

/**
 * Bean property filter that includes only those properties that were explicitly annotated by the {@link JsonProperty} annotation. 
 * 
 * @author Marcus Klimstra
 */
public class ExplicitPropertiesFilter extends SimpleBeanPropertyFilter {

	@Override
	protected boolean include(BeanPropertyWriter writer) {
		JsonProperty anno = writer.getAnnotation(JsonProperty.class);
		return anno != null;
	}

	@Override
	protected boolean include(PropertyWriter writer) {
		if (writer instanceof BeanPropertyWriter) {
			return include((BeanPropertyWriter) writer);
		}
		return true;
	}
}
