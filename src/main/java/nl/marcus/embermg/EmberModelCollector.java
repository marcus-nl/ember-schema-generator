package nl.marcus.embermg;

import java.util.Collection;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
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
import com.fasterxml.jackson.databind.jsontype.NamedType;
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
	
	public EmberModelCollector(ObjectMapper objectMapper) {
		this(objectMapper, new EmberTypeRegistry());
	}
	
	public EmberModelCollector addClass(Class<?> c) {
		processClass(c);
		return this;
	}

	public EmberModelCollector addHierarchy(Class<?> base) {
		processClass(base);
		
		for (NamedType nt : getSubTypes(base)) {
			System.out.println(nt);
			processClass(nt.getType());
		}
		
		return this;
	}

	protected Collection<NamedType> getSubTypes(Class<?> base) {
		MapperConfig<?> config = objectMapper.getDeserializationConfig();
		AnnotatedClass basetype = config.introspectClassAnnotations(base).getClassInfo();
		AnnotationIntrospector ai = config.getAnnotationIntrospector();
		return objectMapper.getSubtypeResolver().collectAndResolveSubtypes(basetype, config, ai);
	}

	public void write(EmberModelWriter writer) {
		initializeSuperTypes();
		
		for (EmberClass c : typeRegistry.getEmberClasses()) {
			writeClass(writer, c);
		}
	}

	protected void writeClass(EmberModelWriter writer, EmberClass c) {
		writer.startModel(c);
		
		for (EmberProperty p : c.ownProperties()) {
			writer.addProperty(p.getName(), p.getTypeRef().getDeclaration());
		}
		
		writer.endModel();
	}
	
	private void initializeSuperTypes() {
		for (EmberClass c : typeRegistry.getEmberClasses()) {
			Class<?> superJavaClass = c.getJavaClass().getSuperclass();
			EmberTypeRef superTypeRef = superJavaClass == null ? null : typeRegistry.getTypeRef(superJavaClass);
			c.initializeSuperType(typeRegistry.getEmberClass(superTypeRef));
		}
	}

	protected EmberTypeRef processClass(Class<?> c) {
		try {
			FormatVisitor visitor = new FormatVisitor(objectMapper.getSerializerProvider());
			objectMapper.acceptJsonFormatVisitor(c, visitor);
			return visitor.getTypeRef();
		}
		catch (JsonMappingException e) {
			throw new RuntimeException(e);
		}
	}

	protected EmberClass convert(SimpleType jacksonType) {
		Class<?> javaClass = jacksonType.getRawClass();
		EmberTypeRef ref = typeRegistry.getTypeRef(javaClass);
		
		return new EmberClass(javaClass, ref);
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
		
		@Override
		public JsonMapFormatVisitor expectMapFormat(JavaType type) throws JsonMappingException {
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
			addProperty(prop, false);
		}

		@Override
		public void optionalProperty(BeanProperty prop) throws JsonMappingException {
			addProperty(prop, true);
		}

		@Override
		public void property(String name, JsonFormatVisitable handler, JavaType jacksonType) throws JsonMappingException {
			throw new UnsupportedOperationException();
		}

		@Override
		public void optionalProperty(String name, JsonFormatVisitable handler, JavaType jacksonType) throws JsonMappingException {
			throw new UnsupportedOperationException();
		}
		
		private void addProperty(BeanProperty prop, boolean optional) throws JsonMappingException {
			JsonSerializer<Object> ser = getSerializer(prop);
			if (ser == null) {
				return;
			}
			
			JavaType jacksonType = prop.getType();
			if (jacksonType == null) {
				throw new IllegalArgumentException("Missing type for property '" + prop.getName() + "'");
			}
			
			FormatVisitor visitor = new FormatVisitor(getProvider());
			ser.acceptJsonFormatVisitor(visitor, jacksonType);

			emberClass.addProperty(prop.getName(), visitor.getTypeRef());
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
