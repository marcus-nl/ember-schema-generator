# ember-model-generator

Generates an Ember model from a Jackson model.

## License

Apache 2.0: http://www.apache.org/licenses/LICENSE-2.0

## Maven
```xml
<dependency>
	<groupId>nl.marcus</groupId>
	<artifactId>ember-model-generator</artifactId>
	<version>0.1-SNAPSHOT</version>
</dependency>
```

## Usage

Given the following data model:

![Zoo UML](https://raw.githubusercontent.com/marcus-nl/ember-model-generator/master/src/main/site/uml/Zoo.png "Zoo UML")

An EmberSchema can be created as follows:
```java
ObjectMapper objectMapper = ...;
EmberSchemaGenerator generator = new EmberSchemaGenerator(objectMapper);

generator.addClass(Zoo.class);
generator.addHierarchy(Animal.class);

EmberSchema schema = generator.getEmberSchema();
```

The EmberSchema has the following JSON representation:
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
