![Maven Central](https://img.shields.io/maven-central/v/io.github.almighty-satan.jaskl/jaskl-yaml?style=flat-square)
![GitHub](https://img.shields.io/github/license/Almighty-Satan/JASKL?style=flat-square)
![CI](https://img.shields.io/github/actions/workflow/status/Almighty-Satan/JASKL/gradle-build.yml?branch=master&style=flat-square)
![Last Commit](https://img.shields.io/github/last-commit/Almighty-Satan/JASKL?style=flat-square)

# JASKL - Just Another Simple Config Library
___

JASKL is a simple config library supporting multiple different formats.

### How to use it?
JASKL is based on `Config`s and `ConfigEntry`s. 
Create a new Config by instantiating one of the following classes: 
`YamlConfig`, `JsonConfig`, `TomlConfig`, `PropertiesConfig`,  `HoconConfig` or `MongodbConfig`. 
After that, new `ConfigEntry`s can be created and added to the config. The value of a `ConfigEntry` is retrieved via
`ConfigEntry#getValue` and can never be `null`.

### Example
```java
File file = new File("path/to/config.yaml");

// Create a config based on a file
Config config = YamlConfig.of(file, "Config for example values");

// Create entries and add them to the config
StringConfigEntry stringValue = StringConfigEntry.of(config, "example.path.string", "An example String!", "This is the default value!");
EnumConfigEntry<ExampleEnum> enumValue = EnumConfigEntry.of(config, "example.path.enum", "An example String!", ExampleEnum.EXAMPLE);

config.load(); // Load the config from file (doesn't create missing entries)
config.write(); // Save the config to write missing entries

System.out.println(stringValue.getValue()); // "This is the default value!"

stringValue.setValue("This is another example!"); // Change values

System.out.println(stringValue.getValue()); // "This is another example!"

config.write(); // Save the config
```

### Implementations
| Type       | Description                                                                         | Base                                                                                        |
|------------|-------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------|
| YAML       | A human-readable data-serialization language.                                       | [SnakeYAML](https://bitbucket.org/snakeyaml/snakeyaml)                                      |
| Hocon      | A more user friendly superset of JSON supporting many different data types.         | [Lightbend Config](https://github.com/lightbend/config)                                     |
| JSON       | JavaScript Object Notation                                                          | [Jackson](https://github.com/FasterXML/jackson)                                             |
| TOML       | A very easy to read config supporting sub categories and many different data types. | [Jackson](https://github.com/FasterXML/jackson)                                             |
| Properties | A very simple implementation for minimalistic config systems.                       | [java.util.Properties](https://docs.oracle.com/javase/8/docs/api/java/util/Properties.html) |
| MongoDB    | A NoSQL database based implementation for complex configs with remote saves.        | [mongodb-driver](https://mvnrepository.com/artifact/org.mongodb/mongodb-driver-sync)        |

### Config Entry Types
| Type                    | YAML | Hocon | JSON | TOML | Properties | MongoDB |
|-------------------------|------|-------|------|------|------------|---------|
| String                  | ✅    | ✅     | ✅    | ✅    | ✅          | ✅       |
| Boolean                 | ✅    | ✅     | ✅    | ✅    | ✅          | ✅       |
| Integer                 | ✅    | ✅     | ✅    | ✅    | ✅          | ✅       |
| Long                    | ✅    | ✅     | ✅    | ✅    | ✅          | ✅       |
| Float                   | ✅    | ✅     | ✅    | ✅    | ✅          | ✅       |
| Double                  | ✅    | ✅     | ✅    | ✅    | ✅          | ✅       |
| BigInteger / BigDecimal | ✅    | ✅     | ✅    | ✅    | ✅          | ✅       |
| Enum                    | ✅    | ✅     | ✅    | ✅    | ✅          | ✅       |
| List                    | ✅    | ✅     | ✅    | ✅    | ❌          | ✅       |
| Map                     | ✅    | ✅     | ✅    | ✅    | ❌          | ✅       |
| Custom Objects          | ✅    | ✅     | ✅    | ✅    | ❌          | ✅       |
| Comments                | ✅    | ✅     | ❌    | ❌    | ✅*¹        | ❌       |  

*¹ Properties only allows for a single comment at the beginning of the file

### Config Entry Validation
JASKL can automatically validate config entries (e.g. ensure that a number is always greater than zero) and throws a
`ValidationException` if an invalid value is detected.
```java
IntegerConfigEntry positiveIntegerConfigEntry = IntegerConfigEntry.of(config, "example.integer", "Example Integer", 1, Validator.INTEGER_POSITIVE);
```

### Custom Objects (Annotations)
```java
public class MyObject {

    @Entry
    @Validate.StringNotEmpty
    public String myString = "Default String"; // Annotated fields must be public and default values should not be null

    @Entry("some.other.path")
    public int myInt = 5;

    public MyObject() {} // An empty constructor is required
}
```
You can than register a ConfigEntry:
```java
ConfigEntry<MyObject> entry = CustomConfigEntry.of(config, "example.myObject", "Some description", new MyObject());
```

### Custom Objects (ObjectMapper)
If you don't want to use annotations, an ObjectMapper can be used instead.
```java
ObjectMapper<MyObject> mapper = new ObjectMapper<MyObject>() {
    @Override
    public @NotNull MyObject createInstance(@Unmodifiable @NotNull Map<@NotNull String, @NotNull Object> values) throws InvalidTypeException, ValidationException {
        return new MyObject((String) values.get("myString"), (int) values.get("myInt"));
    }

    @Override
    public @Unmodifiable @NotNull Map<@NotNull String, @NotNull Object> readValues(@NotNull MyObject instance) throws InvalidTypeException {
        Map<String, Object> values = new HashMap<>();
        values.put("myString", instance.getMyString());
        values.put("myInt", instance.getMyInt());
        return Collections.unmodifiableMap(values);
    }

    @Override
    public @NotNull Class<MyObject> getObjectClass() {
        return MyObject.class;
    }

    @Override
    public @NotNull Map<@NotNull String, @NotNull Type<?>> getProperties() {
        Map<String, Type<?>> properties = new HashMap<>();
        properties.put("myString", Type.validated(Type.STRING, Validator.STRING_NOT_EMPTY));
        properties.put("myInt", Type.INTEGER);
        return Collections.unmodifiableMap(properties);
    }
};

// register the entry
ConfigEntry<MyObject> entry = CustomConfigEntry.of(config, "example.mapper.myObject", "Some description", new MyObject(), mapper);
```

### Annotation-based Configs
You can also use annotation-based configs:
```java
public class ExampleAnnotationConfig {

    @Entry
    @Validate.StringNotEmpty
    public String myString = "Default String"; // Annotated fields must be public and default values should not be null

    @Entry("some.other.path")
    @Description("Enter description here") // May be ignored if the implementation does not support comments
    public int myInt = 5;

    public ExampleAnnotationConfig() {} // An empty constructor is required
}
```
```java
AnnotationManager annotationManager = AnnotationManager.create(); // Create an AnnotationManager. This instance can be reused.

Config yamlConfig = YamlConfig.of(file); // Create a config

// Register our annotated class
ExampleAnnotationConfig config = annotationManager.registerEntries(yamlConfig, ExampleAnnotationConfig.class);

yamlConfig.load(); // Load the config from storage

System.out.println(config.myString); // Print some value we just loaded

config.myString = "Hello World"; // Change the value

// Write the config to storage
// This also checks/validates changed values
yamlConfig.write();
```

### Building
To build the project, open the terminal and type `./gradlew build`. All jars will be located at `/<implementation>/build/libs/<implementation>-<version>.jar`.

### Gradle
```gradle
repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.almighty-satan.jaskl:jaskl-<implementation>:<version>")
}
```
