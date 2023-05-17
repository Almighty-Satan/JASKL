![Maven Central](https://img.shields.io/maven-central/v/io.github.almighty-satan.jaskl/jaskl-yaml?style=flat-square)
![GitHub](https://img.shields.io/github/license/Almighty-Satan/JASKL?style=flat-square)

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
| JSON       | JavaScript Object Notation                                                          | [Jackson](https://github.com/FasterXML/jackson)                                             |
| TOML       | A very easy to read config supporting sub categories and many different data types. | [Jackson](https://github.com/FasterXML/jackson)                                             |
| Hocon      | A more user friendly superset of JSON supporting many different data types.         | [Lightbend Config](https://github.com/lightbend/config)                                     |
| Properties | A very simple implementation for minimalistic config systems.                       | [java.util.Properties](https://docs.oracle.com/javase/8/docs/api/java/util/Properties.html) |
| MongoDB    | A NoSQL database based implementation for complex configs with remote saves.        | [mongodb-driver](https://mvnrepository.com/artifact/org.mongodb/mongodb-driver-sync)        |

### Config Entry Types
| Type     | YAML | JSON | TOML | Properties | Ini | Hocon*¹ | MongoDB |
|----------|------|------|------|------------|-----|---------|---------|
| String   | ✅    | ✅    | ✅    | ✅          | ✅   | ✅       | ✅       |
| Boolean  | ✅    | ✅    | ✅    | ✅          | ✅   | ✅       | ✅       |
| Integer  | ✅    | ✅    | ✅    | ✅          | ✅   | ✅       | ✅       |
| Long     | ✅    | ✅    | ✅    | ✅          | ✅   | ✅       | ✅       |
| Float    | ✅    | ✅    | ✅    | ✅          | ✅   | ✅       | ✅       |
| Double   | ✅    | ✅    | ✅    | ✅          | ✅   | ✅       | ✅       |
| Enum     | ✅    | ✅    | ✅    | ✅          | ✅   | ✅       | ✅       |
| List     | ✅    | ✅    | ✅    | ❌          | ❌   | ✅       | ✅       |
| Map      | ✅    | ✅    | ✅    | ❌          | ❌   | ✅       | ❓*³     |
| Custom*² | ✅    | ✅    | ✅    | ✅          | ✅   | ✅       | ✅       |
| Comments | ✅    | ❌    | ❌    | ❌          | ❌   | ❌       | ❌       |

*¹ Read Only  
*² Only for supported types  
*³ TBD

### Config Entry Validation
JASKL can automatically validate config entries (e.g. ensure that a number is always greater than zero) and throws a
`ValidationException` if an invalid value is detected.
```java
IntegerConfigEntry positiveIntegerConfigEntry = IntegerConfigEntry.of(config, "example.integer", "Example Integer", 1, Validator.INTEGER_POSITIVE);
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
