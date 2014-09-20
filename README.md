# Ember Schema Generator

Ember Schema Generator can be used to generate an Ember schema from a Jackson model. This makes integration between Java and Ember much easier, because you only need to define your data model once, avoiding code duplication.

For our purpuses, a Jackson model is the set of classes that are mapped by [Jackson Databind](https://github.com/FasterXML/jackson-databind). The mapping can be configured in any way supported by Jackson Databind, usually through [Jackson Annotations](https://github.com/FasterXML/jackson-annotations).

The generated Ember schema is a _declarative_ JSON representation of the mapped classes and their properties. The classes have an optional super type. Each property has a name and a declared type. 

To use the schema on the client side load it by using [Ember Schema Loader](https://github.com/marcus-nl/ember-schema-loader).

## License

Apache 2.0: http://www.apache.org/licenses/LICENSE-2.0

## Maven
```xml
<dependency>
	<groupId>com.github.marcus-nl</groupId>
	<artifactId>ember-schema-generator</artifactId>
	<version>1.0.1</version>
</dependency>
```

## JavaDoc

http://marcus-nl.github.io/ember-schema-generator/

## Usage

Create an instance of EmberSchemaGenerator by passing your Jackson ObjectMapper to its constructor. Then register the classes and hierarchies you want included in the model by calling addClass and addHierarchy respectively. Those classes will be processed by inspecting all properties that are known to the ObjectMapper. All classes that were encountered by inspecting those properties will also be processed. 

The generator will respect the property filters of the object mapper, so that only those properties that are included by the filters will end up in the schema.

Note that addHierarchy will only add those classes that are known by the object mapper, either through ObjectMapper#registerSubtypes or by using the @JsonSubTypes annotation on the base type.

## Example

Given the following data model:

![Zoo UML](https://raw.githubusercontent.com/marcus-nl/ember-model-generator/master/src/main/site/uml/Zoo.png "Zoo UML")

An EmberSchema can be generated as follows:
```java
ObjectMapper objectMapper = ...;
EmberSchemaGenerator generator = new EmberSchemaGenerator(objectMapper);

generator.addClass(Zoo.class);
generator.addHierarchy(Animal.class);

EmberSchema schema = generator.getEmberSchema();
```
The EmberSchema can then be converted to JSON in the usual way ([example](https://gist.github.com/marcus-nl/e1e70202c3890fc8e809)), which will look like this:
```json
{
  "classes" : [ {
    "name" : "Zoo",
    "superType" : null,
    "props" : [ {
      "name" : "name",
      "type" : { "kind" : "attr", "name" : "string" }
    }, {
      "name" : "city",
      "type" : { "kind" : "attr", "name" : "string" }
    }, {
      "name" : "star",
      "type" : { "kind" : "one", "name" : "Animal" }
    }, {
      "name" : "animals",
      "type" : { "kind" : "many", "name" : "Animal" }
    } ]
  }, {
    "name" : "Animal",
    "superType" : null,
    "props" : [ {
      "name" : "name",
      "type" : { "kind" : "attr", "name" : "string" }
    }, {
      "name" : "type",
      "type" : { "kind" : "attr", "name" : "string" }
    } ]
  }, {
    "name" : "Elephant",
    "superType" : "Animal",
    "props" : [ {
      "name" : "trunkLength",
      "type" : { "kind" : "attr", "name" : "number" }
    } ]
  }, {
    "name" : "Lion",
    "superType" : "Animal",
    "props" : [ {
      "name" : "hasManes",
      "type" : { "kind" : "attr", "name" : "boolean" }
    } ]
  } ]
}
```
