# JASKL - Just Another Simple ~~C~~Konfig Library
___

JASKL is a simple config library supporting multiple different formats.

### How to use it?
JASKL is based on `Config`s and `ConfigEntry`s. Create a new Config by instantiating one of the following classes: `TomlConfig`, `PropertiesConfig` or `HoconConfig`. After that, new `ConfigEntry`s can be created and added to the config.

### Example

```java
File file = new File("path/to/config.toml");

// Create a config based on a file
Config config = TomlConfig.of(file, "Config for example values");

// Create entries and add them to the config
ConfigEntry<String> stringValue = StringConfigEntry.of(config, "example.path.string", "An example String!", "This is the default value!");
ConfigEntry<ExampleEnum> enumValue = EnumConfigEntry.of(config, "example.path.enum", "An example String!", ExampleEnum.EXAMPLE);

config.load(); // Load the config from file (doesn't create missing entries)
config.write(); // Save the config to write missing entries

System.out.println(stringValue.getValue()); // "This is the default value!"

stringValue.setValue("This is another example!"); // Change values

System.out.println(stringValue.getValue()); // "This is another example!"

config.write(); // Save the config
```

### Implementations

| Type       | Description                                                                         |
|------------|-------------------------------------------------------------------------------------|
| TOML       | A very easy to read config supporting sub categories and many different data types. |
| Hocon      | A more user friendly version of JSON supporting many different data types.          |
| Properties | A very simple implementation for minimalistic config systems.                       |

### Config Entry Types
| Type    | TOML | Hocon | Properties |
|---------|------|-------|------------|
| String  | ✅    | ✅*¹   | ✅          |
| Boolean | ✅    | ✅*¹   | ✅          |
| Integer | ✅    | ✅*¹   | ✅          |
| Long    | ✅    | ✅*¹   | ✅          |
| Float   | ✅    | ✅*¹   | ✅          |
| Double  | ✅    | ✅*¹   | ✅          |
| Enum    | ✅    | ✅*¹   | ✅          |
| List    | ✅    | ✅*¹   | ❌          |
| Map     | ✅    | ✅*¹   | ❌          |
| Custom  | ❓    | ❓*¹   | ❌          |
*¹ Read Only