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

try (EmberModelWriter writer = new EmberModelWriter(...)) {
	collector.write(writer);
}
```
