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

```java
ObjectMapper objectMapper = ...;
EmberModelCollector collector = new EmberModelCollector(objectMapper);

collector.addClass(Zoo.class);
collector.addHierarchy(Animal.class);

JsonGenerator jgen = objectMapper.getFactory()
		.createGenerator(System.out)
		.useDefaultPrettyPrinter();

jgen.writeObject(collector.getEmberClasses());

jgen.close();
```

This will generate the following output:

```json
[ {
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
      "kind" : "fragment",
      "name" : "Animal"
    }
  }, {
    "name" : "animals",
    "type" : {
      "kind" : "fragments",
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
  "name" : "Elephant",
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
```
