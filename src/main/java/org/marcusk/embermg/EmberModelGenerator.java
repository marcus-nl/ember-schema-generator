package org.marcusk.embermg;

import java.beans.Introspector;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;

import org.reflections.Reflections;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.base.Strings;

@Deprecated
public class EmberModelGenerator {
	private static final String APP = "App";

	private final Reflections reflections;
	private final EmberModelWriter writer;
	
	public EmberModelGenerator(EmberModelWriter writer, String pckg) {
		this.reflections = new Reflections(pckg);
		this.writer = writer;
	}
	
	public void processHierarchy(Class<?> base) {
		processBaseClass(base);
		
		for (Class<?> c : reflections.getSubTypesOf(base)) {
			processSubClass(c);
		}
	}
	
	protected void processBaseClass(Class<?> base) {
		startBaseModel(base);
		processProperties(base);
		endBaseModel(base);
	}

	protected void processSubClass(Class<?> sub) {
		startSubModel(sub);
		processProperties(sub);
		endSubModel(sub);
	}

	protected void processProperties(Class<?> clazz) {
		for (Method m : clazz.getDeclaredMethods()) {
			if (m.isAnnotationPresent(JsonProperty.class)) {
				addProperty(m);
			}
		}
	}

	protected void addProperty(Method m) {
		String propertyName = convertPropertyName(m);
		String type = convertType(m);
		
		writer.addProperty(propertyName, type);
	}

	protected void startBaseModel(Class<?> base) {
		String methodName = "defineBaseModel";
		String modelName = getModelName(base);
		String superName = getModelName(base.getSuperclass());
		String alias = getAlias(base);

		writer.startModel(modelName, superName, alias);
	}

	protected void endBaseModel(Class<?> base) {
		writer.endModel();
	}

	protected void startSubModel(Class<?> clazz) {
		String methodName = "defineSubModel";
		String modelName = getModelName(clazz);
		String superName = getModelName(clazz.getSuperclass());
		String alias = getAlias(clazz);

		writer.startModel(modelName, superName, alias);
	}

	protected void endSubModel(Class<?> clazz) {
		writer.endModel();
	}

	protected String getModelName(Class<?> clazz) {
		return APP + "." + clazz.getSimpleName();
	}

	protected String getAlias(Class<?> clazz) {
		JsonTypeName anno = clazz.getAnnotation(JsonTypeName.class);
		return anno != null ? anno.value() : null;
	}

	protected String convertType(Method m) {
		Objects.requireNonNull(m);
		Class<?> rt = m.getReturnType();
		
		if (String.class.isAssignableFrom(rt)) {
			return "DS.attr('string')";
		}
		else if (Number.class.isAssignableFrom(rt)) {
			return "DS.attr('number')";
		}
		else if (Boolean.class.isAssignableFrom(rt)) {
			return "DS.attr('boolean')";
		}
		else if (Collection.class.isAssignableFrom(rt)) {
			ParameterizedType pt = (ParameterizedType) m.getGenericReturnType();
			Type arg = pt.getActualTypeArguments()[0];
			return "fragments() /* " + arg + " */";
		}
		else {
			return "fragment() /* " + rt + " */";
		}
	}

	protected String convertPropertyName(Method m) {
		JsonProperty pann = m.getAnnotation(JsonProperty.class);
		if (Strings.isNullOrEmpty(pann.value())) {
			return getDefaultPropertyName(m);
		}
		else {
			return pann.value();
		}
	}

	protected String getDefaultPropertyName(Method m) {
		String methodName = m.getName();
		if (methodName.startsWith("get") || methodName.startsWith("set")) {
			return Introspector.decapitalize(methodName.substring(3));
		}
		else if (methodName.startsWith("is")) {
			return Introspector.decapitalize(methodName.substring(2));
		}
		else {
			return methodName;
		}
	}
}

// http://www.java2s.com/Tutorial/Java/0125__Reflection/Atreestructurethatmapsinheritancehierarchiesofclasses.htm
// http://codereview.stackexchange.com/questions/46032/class-for-printing-class-hierarchy-as-text
class ClassComparator implements Comparator<Class<?>> {

	@Override
	public int compare(Class<?> a, Class<?> b) {
		if (a.equals(b)) {
			return 0;
		}
		else if (b.isAssignableFrom(a)) {
			return 2;
		}
		else if (a.isAssignableFrom(b)) {
			return -2;
		}
		else {
			return 0;
		}
		//	return a.getName().compareTo(b.getName());
	}
}