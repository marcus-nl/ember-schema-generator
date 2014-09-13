# ember-schema-generator

Generates an Ember schema from a Jackson model.

## License

Apache 2.0: http://www.apache.org/licenses/LICENSE-2.0

## Maven
```xml
<dependency>
	<groupId>com.github.marcus-nl</groupId>
	<artifactId>ember-schema-generator</artifactId>
	<version>0.1.0-SNAPSHOT</version>
</dependency>
```

## Usage

Create an instance of EmberSchemaGenerator by passing your Jackson ObjectMapper to its constructor. Then register the classes and hierarchies you want included in the model by calling addClass and addHierarchy respectively. Those classes will be processed by inspecting all properties that are known to the ObjectMapper. All classes that were encountered by inspecting those properties will also be processed. 

The generator will respect the property filters of the object mapper, so that only those properties that are included by the filters will end up in the schema.

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
      "type" : {
        "kind" : "attr",
        "name" : "string"
      }
    }, {
      "name" : "city",
      "type" : {
        "kind" : "attr",
        "name" : "string"
      }
    }, {
      "name" : "star",
      "type" : {
        "kind" : "one",
        "name" : "Animal"
      }
    }, {
      "name" : "animals",
      "type" : {
        "kind" : "many",
        "name" : "Animal"
      }
    } ]
  }, {
    "name" : "Animal",
    "superType" : null,
    "props" : [ {
      "name" : "name",
      "type" : {
        "kind" : "attr",
        "name" : "string"
      }
    }, {
      "name" : "type",
      "type" : {
        "kind" : "attr",
        "name" : "string"
      }
    } ]
  }, {
    "name" : "Elephas",
    "superType" : "Animal",
    "props" : [ {
      "name" : "trunkLength",
      "type" : {
        "kind" : "attr",
        "name" : "number"
      }
    } ]
  }, {
    "name" : "Lion",
    "superType" : "Animal",
    "props" : [ {
      "name" : "hasManes",
      "type" : {
        "kind" : "attr",
        "name" : "boolean"
      }
    } ]
  } ]
}
```
