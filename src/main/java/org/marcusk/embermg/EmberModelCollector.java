package org.marcusk.embermg;

import org.reflections.Reflections;

import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonAnyFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonBooleanFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonIntegerFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonMapFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonNullFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonNumberFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.SimpleType;


public class EmberModelCollector {
	private final ObjectMapper objectMapper;
	private final EmberTypeRegistry typeRegistry;

	public EmberModelCollector(ObjectMapper objectMapper, EmberTypeRegistry typeRegistry) {
		this.objectMapper = objectMapper;
		this.typeRegistry = typeRegistry;
	}
	
	public void processClass(Class<?> c) {
		try {
			FormatVisitor visitor = new FormatVisitor(objectMapper.getSerializerProvider());
			objectMapper.acceptJsonFormatVisitor(c, visitor);
		}
		catch (JsonMappingException e) {
			throw new RuntimeException(e);
		}
	}

	public void processHierarchy(Class<?> base) {
		processClass(base);
		
		Reflections reflections = new Reflections();
		
		for (Class<?> c : reflections.getSubTypesOf(base)) {
			processClass(c);
		}
	}

	protected EmberClass convert(SimpleType jacksonType) {
		EmberTypeRef superType = null;
		String name = jacksonType.getRawClass().getSimpleName();
		EmberTypeRef ref = EmberTypeRef.forAsdf(name);
		return new EmberClass(ref, superType);
	}
	
	protected EmberTypeRef convertTypeRef(SimpleType type) {
		return EmberTypeRef.forAsdf(type.toString());
	}

	protected EmberTypeRef convertTypeRef(CollectionType type) {
		return EmberTypeRef.forAsdf(type.toString());
	}
	
	class FormatVisitor implements JsonFormatVisitorWrapper {

		private SerializerProvider provider;
		private EmberTypeRef typeRef;

		public FormatVisitor(SerializerProvider provider) {
			this.provider = provider;
		}

		@Override
		public SerializerProvider getProvider() {
			return provider;
		}
		
		@Override
		public void setProvider(SerializerProvider serializerProvider) {
			this.provider = serializerProvider;
		}
		
		public EmberTypeRef getTypeRef() {
			return typeRef;
		}
		
		@Override
		public JsonObjectFormatVisitor expectObjectFormat(JavaType type) throws JsonMappingException {
			SimpleType jacksonType = (SimpleType) type;
			typeRef = typeRegistry.getTypeRef(jacksonType.getRawClass());
			
			if (!typeRegistry.containsType(typeRef)) {
				EmberClass emberClass = convert(jacksonType);
				typeRegistry.register(typeRef, emberClass);

				return new ObjectVisitor(getProvider(), emberClass);
			}
			
			return null;
		}

		@Override
		public JsonArrayFormatVisitor expectArrayFormat(JavaType type) throws JsonMappingException {
			CollectionType collectionType = (CollectionType) type;
			String s = collectionType.getContentType().getRawClass().getSimpleName();
			typeRef = EmberTypeRef.forCollection(s);
			return new ArrayVisitor(getProvider());
		}

		@Override
		public JsonMapFormatVisitor expectMapFormat(JavaType type) throws JsonMappingException {
			//TODO?
			return null;
		}
		
		@Override
		public JsonStringFormatVisitor expectStringFormat(JavaType type) throws JsonMappingException {
			this.typeRef = EmberTypeRef.STRING;
			return null;
		}

		@Override
		public JsonNumberFormatVisitor expectNumberFormat(JavaType type) throws JsonMappingException {
			this.typeRef = EmberTypeRef.NUMBER;
			return null;
		}

		@Override
		public JsonIntegerFormatVisitor expectIntegerFormat(JavaType type) throws JsonMappingException {
			this.typeRef = EmberTypeRef.NUMBER;
			return null;
		}

		@Override
		public JsonBooleanFormatVisitor expectBooleanFormat(JavaType type) throws JsonMappingException {
			this.typeRef = EmberTypeRef.BOOLEAN;
			return null;
		}

		@Override
		public JsonNullFormatVisitor expectNullFormat(JavaType type) throws JsonMappingException {
			throw new UnsupportedOperationException();
		}

		@Override
		public JsonAnyFormatVisitor expectAnyFormat(JavaType type) throws JsonMappingException {
			throw new UnsupportedOperationException();
		}
	}
	
	class ObjectVisitor extends JsonObjectFormatVisitor.Base {

		private final EmberClass emberClass;
		
		public ObjectVisitor(SerializerProvider provider, EmberClass emberClass) {
			super(provider);
			this.emberClass = emberClass;
		}

		@Override
		public void property(BeanProperty prop) throws JsonMappingException {
			JsonSerializer<Object> ser = getSerializer(prop);
			if (ser != null) {
				property(prop.getName(), ser, prop.getType());
			}
		}

		@Override
		public void optionalProperty(BeanProperty prop) throws JsonMappingException {
			JsonSerializer<Object> ser = getSerializer(prop);
			if (ser != null) {
				optionalProperty(prop.getName(), ser, prop.getType());
			}
		}

		@Override
		public void property(String name, JsonFormatVisitable handler, JavaType jacksonType) throws JsonMappingException {
			addProperty(name, handler, jacksonType);
		}

		@Override
		public void optionalProperty(String name, JsonFormatVisitable handler, JavaType jacksonType) throws JsonMappingException {
			addProperty(name, handler, jacksonType);
		}
		
		private void addProperty(String name, JsonFormatVisitable handler, JavaType jacksonType) throws JsonMappingException {
			if (jacksonType == null) {
				throw new IllegalArgumentException("Missing type for property '" + name + "'");
			}

			FormatVisitor visitor = new FormatVisitor(getProvider());
			handler.acceptJsonFormatVisitor(visitor, jacksonType);
			
			emberClass.addProperty(name, visitor.getTypeRef());
		}

		/** @see com.fasterxml.jackson.module.jsonSchema.factories.ObjectVisitor#getSer */
		private JsonSerializer<Object> getSerializer(BeanProperty prop) throws JsonMappingException {
			JsonSerializer<Object> ser = null;
			if (prop instanceof BeanPropertyWriter) {
				ser = ((BeanPropertyWriter) prop).getSerializer();
			}
			if (ser == null) {
				ser = getProvider().findValueSerializer(prop.getType(), prop);
			}
			return ser;
		}
	}
	
	class ArrayVisitor extends JsonArrayFormatVisitor.Base {

		public ArrayVisitor(SerializerProvider provider) {
			super(provider);
		}

		@Override
		public void itemsFormat(JsonFormatVisitable handler, JavaType elementType) throws JsonMappingException {
			FormatVisitor visitor = new FormatVisitor(getProvider());
			handler.acceptJsonFormatVisitor(visitor, elementType);
		}

		@Override
		public void itemsFormat(JsonFormatTypes format) throws JsonMappingException {
			// NOP
		}
	}
}
