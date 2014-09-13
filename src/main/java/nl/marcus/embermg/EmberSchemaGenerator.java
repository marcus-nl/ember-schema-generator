package nl.marcus.embermg;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonTypeName;
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

/**
 * Generator for an {@link EmberSchema} based on the data model known by a Jackson {@link ObjectMapper}.
 * <p>
 * The resulting schema contains all classes that were added by calling {@link #addClass(Class)}, {@link #addHierarchy(Class)}
 * and those classes that were encountered recursively by those methods.
 * 
 * @author Marcus Klimstra
 */
public class EmberSchemaGenerator {
	
	private final ObjectMapper objectMapper;
	private final EmberTypeRegistry typeRegistry;

	/**
	 * Creates a new EmberSchemaGenerator based on the specified Jackson {@link ObjectMapper}.
	 * @param objectMapper the Jackson ObjectMapper.
	 */
	public EmberSchemaGenerator(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		this.typeRegistry = new EmberTypeRegistry();
	}
	
	/**
	 * Adds a class to be processed.
	 * All classes that are related through properties that are visited will also be processed.
	 * 
	 * @param cls the class to add.
	 * @return this.
	 */
	public EmberSchemaGenerator addClass(Class<?> cls) {
		processClass(cls);
		return this;
	}

	/**
	 * Adds a class hierarchy to be processed.
	 * All classes that are related through properties that are visited will also be processed.
	 * 
	 * @param base the base class of the hierarchy.
	 * @return this.
	 */
	public EmberSchemaGenerator addHierarchy(Class<?> base) {
		processClass(base);
		
		for (NamedType nt : getSubTypes(base)) {
			processClass(nt.getType());
		}
		
		return this;
	}

	/**
	 * Returns the {@link EmberSchema} that results from processing the classes that were added to this generator.
	 */
	public EmberSchema getEmberSchema() {
		initializeSuperTypes();
		return new EmberSchema(typeRegistry.getEmberClasses());		
	}

	/**
	 * Returns a collection of Jackson {@link NamedType}s that represent the subtypes of the specified base class.
	 * Note that the collection does not have a specified order.
	 * 
	 * @param base the base class.
	 */
	protected Collection<NamedType> getSubTypes(Class<?> base) {
		MapperConfig<?> config = objectMapper.getDeserializationConfig();
		AnnotatedClass basetype = config.introspectClassAnnotations(base).getClassInfo();
		AnnotationIntrospector ai = config.getAnnotationIntrospector();
		return objectMapper.getSubtypeResolver().collectAndResolveSubtypes(basetype, config, ai);
	}
	
	/**
	 * Processes the specified class by visiting all properties that are known to Jackson. 
	 * All additional classes that are encountered by visiting these properties will also be processed recursively. 
	 * Returns an {@link EmberTypeRef} that serves as a reference to the class.
	 *  
	 * @param cls the class to process.
	 */
	protected EmberTypeRef processClass(Class<?> cls) {
		try {
			FormatVisitor visitor = new FormatVisitor(objectMapper.getSerializerProvider());
			objectMapper.acceptJsonFormatVisitor(cls, visitor);
			return visitor.getTypeRef();
		}
		catch (JsonMappingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Converts a Jackson {@link SimpleType} to an {@link EmberClass}.
	 */
	protected EmberClass convert(SimpleType jacksonType) {
		Class<?> javaClass = jacksonType.getRawClass();
		String typeName = getTypeName(jacksonType);
		
		return new EmberClass(javaClass, typeName);
	}

	private String getTypeName(SimpleType jacksonType) {
		Class<?> javaClass = jacksonType.getRawClass();
		JsonTypeName anno = javaClass.getAnnotation(JsonTypeName.class);
		return anno == null ? javaClass.getSimpleName() : anno.value();
	}

	private void initializeSuperTypes() {
		for (EmberClass c : typeRegistry.getEmberClasses()) {
			Class<?> superJavaClass = c.getJavaClass().getSuperclass();
			EmberTypeRef superTypeRef = superJavaClass == null ? null : typeRegistry.getTypeRef(superJavaClass);
			c.initializeSuperType(typeRegistry.getEmberClass(superTypeRef));
		}
	}

	private class FormatVisitor implements JsonFormatVisitorWrapper {

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
	
	private class ObjectVisitor extends JsonObjectFormatVisitor.Base {

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
