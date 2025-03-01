/*
 * JASKL - Just Another Simple Konfig Library
 * Copyright (C) 2023 LeStegii, Almighty-Satan
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 * USA
 */

package io.github.almightysatan.jaskl.test;

import io.github.almightysatan.jaskl.*;
import io.github.almightysatan.jaskl.annotation.AnnotationManager;
import io.github.almightysatan.jaskl.annotation.InvalidAnnotationConfigException;
import io.github.almightysatan.jaskl.entries.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public abstract class ConfigTest {

    protected abstract Config createEmptyConfig();

    protected abstract Config createExampleConfig(@Nullable ExceptionHandler exceptionHandler);

    protected abstract Config createTestConfig();

    protected abstract void clearTestConfig();

    protected abstract boolean testConfigExists();

    @BeforeEach
    public final void setup() {
        this.clearTestConfig();
    }

    /**
     * Test if the config can be loaded successfully.
     */
    @Test
    public void testLoad() throws IOException {
        Config config = this.createExampleConfig(null);
        config.load();

        config.close();
    }

    /**
     * Test if the config can be loaded successfully after closing it.
     */
    @Test
    public void testLoadAfterClosed() throws IOException {
        Config config = this.createExampleConfig(null);
        config.load();
        config.close();
        config.load();

        config.close();
    }

    /**
     * Test if the config cannot be loaded when it has been loaded before.
     */
    @Test
    public void testAlreadyLoaded() throws IOException {
        Config config = this.createExampleConfig(null);
        config.load();
        Assertions.assertThrows(IllegalStateException.class, config::load);

        config.close();
    }

    @Test
    public void testEmptyConfig() throws IOException {
        Config config = this.createEmptyConfig();
        config.load();
        config.write();
        config.close();
    }

    /**
     * Test if a config's values can be loaded successfully.
     * This test requires a predefined config file with inserted values.
     */
    @Test
    public void testLoadValues() throws IOException {
        Config config = this.createExampleConfig(null);

        ConfigEntry<Boolean> booleanConfigEntry = BooleanConfigEntry.of(config, "example.boolean", "Example Boolean", false);
        ConfigEntry<Double> doubleConfigEntry = DoubleConfigEntry.of(config, "example.double", "Example Double", 0.0D);
        ConfigEntry<Float> floatConfigEntry = FloatConfigEntry.of(config, "example.float", "Example Float", 0.0F);
        ConfigEntry<Integer> integerConfigEntry = IntegerConfigEntry.of(config, "example.integer", "Example Integer", 0);
        ConfigEntry<Long> longConfigEntry = LongConfigEntry.of(config, "example.long", "Example Long", 0L);
        ConfigEntry<String> stringConfigEntry = StringConfigEntry.of(config, "example.string", "Example String", "default");

        ConfigEntry<String> specialCharEntry = StringConfigEntry.of(config, "example.special-char_entry", "Example String with special chars in the path", "de-fau_lt");

        config.load();

        Assertions.assertEquals(true, booleanConfigEntry.getValue());
        Assertions.assertEquals(1.0D, doubleConfigEntry.getValue());
        Assertions.assertEquals(1.0F, floatConfigEntry.getValue());
        Assertions.assertEquals(1, integerConfigEntry.getValue());
        Assertions.assertEquals(1L, longConfigEntry.getValue());
        Assertions.assertEquals("modified", stringConfigEntry.getValue());
        Assertions.assertEquals("spe-ci_al", specialCharEntry.getValue());

        config.close();
    }

    /**
     * Test if a config's values can be loaded successfully.
     * This test requires a predefined config file with inserted values.
     */
    @Test
    public void testValidation() {
        Config config0 = this.createExampleConfig(null);

        Assertions.assertThrows(ValidationException.class, () -> IntegerConfigEntry.of(config0, "example.integer", "Example Integer", 0, Validator.INTEGER_NOT_ZERO));

        config0.close();

        Config config1 = this.createExampleConfig(null);
        IntegerConfigEntry.of(config1, "example.integer", "Example Integer", -1, Validator.INTEGER_NEGATIVE);

        Assertions.assertThrows(ValidationException.class, config1::load);

        config1.close();
    }

    @Test
    public void testValidationFailureHandler() throws IOException {
        Config config = this.createExampleConfig(new ExceptionHandler() {
            @Override
            public <T> T handle(@NotNull ConfigEntry<T> entry, @Nullable Object value, @NotNull Throwable exception) throws InvalidTypeException, ValidationException {
                return entry.getDefaultValue();
            }
        });
        IntegerConfigEntry entry = IntegerConfigEntry.of(config, "example.integer", "Example Integer", -1, Validator.INTEGER_NEGATIVE);

        config.load();

        Assertions.assertEquals(entry.getDefaultValue(), entry.getValue());

        config.close();
    }

    /**
     * Test if enum values can be loaded successfully.
     * This test requires a predefined config file with inserted values.
     */
    @Test
    public void testEnumValues() throws IOException {
        Config config = this.createExampleConfig(null);

        ConfigEntry<ExampleEnum> enumConfigEntry = EnumConfigEntry.of(config, "example.enum", "Example Enum", ExampleEnum.EXAMPLE);

        config.load();

        Assertions.assertEquals(ExampleEnum.ANOTHER_EXAMPLE, enumConfigEntry.getValue());

        config.close();
    }

    /**
     * Test if list values can be loaded successfully.
     * This test requires a predefined config file with inserted values.
     */
    @Test
    public void testListValues() throws IOException {
        Config config = this.createExampleConfig(null);

        List<String> example0 = Arrays.asList("Example1", "Example2");
        ConfigEntry<List<String>> listConfigEntry = ListConfigEntry.of(config, "example.list", "Example List", example0, Type.STRING);

        config.load();

        List<String> example1 = Arrays.asList("Example3", "Example4");
        Assertions.assertEquals(example1, listConfigEntry.getValue());

        config.close();
    }

    /**
     * Test if map values can be loaded successfully.
     * This test requires a predefined config file with inserted values.
     */
    @Test
    public void testMapValues() throws IOException {
        Config config = this.createExampleConfig(null);

        Map<String, String> example0 = new HashMap<>();
        example0.put("Hello", "World");
        example0.put("abc", "def");
        ConfigEntry<Map<String, String>> mapConfigEntry = MapConfigEntry.of(config, "example.map", "Example Map", example0, Type.STRING, Type.STRING);

        config.load();

        Map<String, String> example1 = new HashMap<>();
        example1.put("Hello", "there");
        example1.put("abc", "xyz");
        example1.put("x", "y");
        Assertions.assertEquals(example1, mapConfigEntry.getValue());

        config.close();
    }

    /**
     * Test if a config can be created, saved and loaded again.
     * This test requires a valid file path.
     */
    @Test
    public void testWriteAndLoad() throws IOException {
        Config config0 = this.createTestConfig();
        ConfigEntry<String> stringConfigEntry0 = StringConfigEntry.of(config0, "example.string", "Example String", "default");
        ConfigEntry<Integer> intConfigEntry0 = IntegerConfigEntry.of(config0, "example.integer", "Example Integer", 0);

        config0.load();

        stringConfigEntry0.setValue("modified");
        intConfigEntry0.setValue(1);

        config0.write();
        config0.close();

        Assertions.assertTrue(this.testConfigExists());

        Config config1 = this.createTestConfig();
        ConfigEntry<String> stringConfigEntry1 = StringConfigEntry.of(config1, "example.string", "Example String", "default");
        ConfigEntry<Integer> intConfigEntry1 = IntegerConfigEntry.of(config1, "example.integer", "Example Integer", 0);

        config1.load();

        Assertions.assertEquals("modified", stringConfigEntry1.getValue());
        Assertions.assertEquals(1, intConfigEntry1.getValue());

        config1.close();
    }

    @Test
    public void testWriteAndLoadBig() throws IOException {
        Config config0 = this.createTestConfig();

        ConfigEntry<BigDecimal> bigDecimalConfigEntry0 = BigDecimalConfigEntry.of(config0, "example.bigdecimal", "Example BigDecimal", new BigDecimal("99.000000000000000001"));
        ConfigEntry<BigInteger> bigIntegerConfigEntry0 = BigIntegerConfigEntry.of(config0, "example.biginteger", "Example BigInteger", new BigInteger("9923372036854775807"));

        config0.load();
        config0.write();
        config0.close();

        Assertions.assertTrue(this.testConfigExists());

        Config config1 = this.createTestConfig();
        ConfigEntry<BigDecimal> bigDecimalConfigEntry1 = BigDecimalConfigEntry.of(config1, "example.bigdecimal", "Example BigDecimal", new BigDecimal("0"));
        ConfigEntry<BigInteger> bigIntegerConfigEntry1 = BigIntegerConfigEntry.of(config1, "example.biginteger", "Example BigInteger", BigInteger.valueOf(0L));

        config1.load();

        Assertions.assertEquals(bigDecimalConfigEntry0.getValue(), bigDecimalConfigEntry1.getValue());
        Assertions.assertEquals(bigIntegerConfigEntry0.getValue(), bigIntegerConfigEntry1.getValue());

        config1.close();
    }

    /**
     * Test if a config can be created, saved and loaded again.
     * This test requires a valid file path.
     */
    @Test
    public void testWriteAndLoadList() throws IOException {
        Config config0 = this.createTestConfig();

        List<Double> list0 = Arrays.asList(1.0, 2.0);
        ConfigEntry<List<Double>> listConfigEntry0 = ListConfigEntry.of(config0, "example.list", "Example Double List", list0, Type.DOUBLE);
        config0.load();

        List<Double> list1 = Arrays.asList(3.0, 4.0);
        listConfigEntry0.setValue(list1);

        config0.write();
        config0.close();

        Assertions.assertTrue(this.testConfigExists());

        Config config1 = this.createTestConfig();
        ConfigEntry<List<Double>> listConfigEntry1 = ListConfigEntry.of(config1, "example.list", "Example Double List", list0, Type.DOUBLE);

        config1.load();

        Assertions.assertArrayEquals(list1.toArray(new Double[0]), listConfigEntry1.getValue().toArray(new Double[0]));

        config1.close();
    }

    /**
     * Test if a list of lists can be written and loaded again.
     * This test requires a valid file path.
     */
    @Test
    public void testWriteAndLoadList2() throws IOException {
        Config config0 = this.createTestConfig();

        List<List<Integer>> list0 = Arrays.asList(Arrays.asList(1, 2), Arrays.asList(3, 4));
        ConfigEntry<List<List<Integer>>> listConfigEntry0 = ListConfigEntry.of(config0, "example.list", "Example List", list0, Type.list(Type.INTEGER));
        config0.load();

        List<List<Integer>> list1 = Arrays.asList(Arrays.asList(5, 6), Arrays.asList(7, 8));
        listConfigEntry0.setValue(list1);

        config0.write();
        config0.close();

        Assertions.assertTrue(this.testConfigExists());

        Config config1 = this.createTestConfig();
        ConfigEntry<List<List<Integer>>> listConfigEntry1 = ListConfigEntry.of(config1, "example.list", "Example List", list0, Type.list(Type.INTEGER));

        config1.load();

        Assertions.assertArrayEquals(list1.stream().map(list -> list.toArray(new Integer[0])).toArray(Integer[][]::new),
                listConfigEntry1.getValue().stream().map(list -> list.toArray(new Integer[0])).toArray(Integer[][]::new));

        config1.close();
    }

    /**
     * Test if a list of enums can be written and loaded again.
     * This test requires a valid file path.
     */
    @Test
    public void testWriteAndLoadListEnum() throws IOException {
        Config config0 = this.createTestConfig();

        List<ExampleEnum> list0 = Arrays.asList(ExampleEnum.EXAMPLE, ExampleEnum.EXAMPLE);
        ConfigEntry<List<ExampleEnum>> listConfigEntry0 = ListConfigEntry.of(config0, "example.list", "Example Enum List", list0, Type.enumType(ExampleEnum.class));
        config0.load();

        List<ExampleEnum> list1 = Arrays.asList(ExampleEnum.ANOTHER_EXAMPLE, ExampleEnum.ANOTHER_EXAMPLE);
        listConfigEntry0.setValue(list1);

        config0.write();
        config0.close();

        Assertions.assertTrue(this.testConfigExists());

        Config config1 = this.createTestConfig();
        ConfigEntry<List<ExampleEnum>> listConfigEntry1 = ListConfigEntry.of(config1, "example.list", "Example Enum List", list0, Type.enumType(ExampleEnum.class));

        config1.load();

        Assertions.assertArrayEquals(list1.toArray(new ExampleEnum[0]), listConfigEntry1.getValue().toArray(new ExampleEnum[0]));

        config1.close();
    }

    /**
     * Test if a map can be written and loaded again.
     * This test requires a valid file path.
     */
    @Test
    public void testWriteAndLoadMap() throws IOException {
        Config config0 = this.createTestConfig();

        Map<Float, String> map0 = new HashMap<>();
        map0.put(10F, "Hello");
        map0.put(20F, "World");
        ConfigEntry<Map<Float, String>> mapConfigEntry0 = MapConfigEntry.of(config0, "example.map", "Example Map", map0, Type.FLOAT, Type.STRING);
        config0.load();

        Map<Float, String> map1 = new HashMap<>();
        map1.put(5.5F, "Test0");
        map1.put(6.9F, "Test1");
        mapConfigEntry0.setValue(map1);

        config0.write();
        config0.close();

        Assertions.assertTrue(this.testConfigExists());

        Config config1 = this.createTestConfig();
        ConfigEntry<Map<Float, String>> mapConfigEntry1 = MapConfigEntry.of(config1, "example.map", "Example Map", map0, Type.FLOAT, Type.STRING);

        config1.load();

        Assertions.assertEquals(map1, mapConfigEntry1.getValue());

        config1.close();
    }

    /**
     * Test if strip works as intended
     */
    @Test
    public void testStrip() throws IOException {
        Config config0 = this.createTestConfig();
        ConfigEntry<String> stringConfigEntry0 = StringConfigEntry.of(config0, "example.string", "Example String", "default");
        ConfigEntry<Integer> intConfigEntry0 = IntegerConfigEntry.of(config0, "example.integer", "Example String", 0);

        config0.load();

        stringConfigEntry0.setValue("modified");
        intConfigEntry0.setValue(1);

        config0.write();
        config0.close();

        Config config1 = this.createTestConfig();
        ConfigEntry<String> stringConfigEntry1 = StringConfigEntry.of(config1, "example.string", "Example String", "default");

        config1.load();
        Set<String> paths = config1.prune();
        config1.close();

        Config config2 = this.createTestConfig();
        ConfigEntry<String> stringConfigEntry2 = StringConfigEntry.of(config2, "example.string", "Example String", "default");
        ConfigEntry<Integer> intConfigEntry2 = IntegerConfigEntry.of(config2, "example.integer", "Example String", 0);

        config2.load();

        Assertions.assertEquals("modified", stringConfigEntry2.getValue());
        Assertions.assertEquals(0, intConfigEntry2.getValue());
        Assertions.assertEquals(1, paths.size());
        Assertions.assertTrue(paths.contains("example.integer"));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> paths.add("test"));

        config2.close();
    }

    /**
     * Test if strip can be run with maps
     */
    @Test
    public void testStripMap() throws IOException {
        Map<String, String> map0 = new HashMap<>();
        map0.put("Hello", "World");
        map0.put("abc", "def");
        Map<String, String> map1 = new HashMap<>();
        map1.put("0", "1");
        map1.put("2", "3");

        Map<String, String> map0New = new HashMap<>();
        map0New.put("Hello", "there");
        map0New.put("1234", "5678");
        Map<String, String> map1New = new HashMap<>();
        map1New.put("0", "0");
        map1New.put("1", "1");
        map1New.put("2", "2");
        map1New.put("3", "3");

        Config config0 = this.createTestConfig();
        MapConfigEntry<String, String> mapConfigEntry0 = MapConfigEntry.of(config0, "example.0.map0", null, map0, Type.STRING, Type.STRING);
        MapConfigEntry<String, String> mapConfigEntry1 = MapConfigEntry.of(config0, "example.1.map1", null, map1, Type.STRING, Type.STRING);

        config0.load();
        config0.write();
        config0.close();

        Config config1 = this.createTestConfig();
        MapConfigEntry.of(config1, "example.1.map1", null, map1New, Type.STRING, Type.STRING);

        config1.load();
        Set<String> paths = config1.prune();
        config1.close();

        Config config2 = this.createTestConfig();
        MapConfigEntry<String, String> mapConfigEntry0New = MapConfigEntry.of(config2, "example.0.map0", null, map0New, Type.STRING, Type.STRING);
        MapConfigEntry<String, String> mapConfigEntry1New = MapConfigEntry.of(config2, "example.1.map1", null, map1New, Type.STRING, Type.STRING);

        config2.load();

        Assertions.assertEquals(map0New, mapConfigEntry0New.getValue());
        Assertions.assertEquals(map1, mapConfigEntry1New.getValue());
        Assertions.assertFalse(paths.isEmpty());
        Assertions.assertTrue(paths.size() <= 2);
        if (paths.size() == 1) {
            Assertions.assertTrue(paths.contains("example.0.map0"));
        } else {
            Assertions.assertTrue(paths.contains("example.0.map0.Hello"));
            Assertions.assertTrue(paths.contains("example.0.map0.abc"));
        }

        config2.close();
    }

    /**
     * Test if custom values can be written and loaded successfully.
     */
    @Test
    public void testCustom() throws IOException {
        ExampleCustomObject value = new ExampleCustomObject("Default", 5, ExampleEnum.EXAMPLE);
        ExampleNestedCustomObject nestedValue = new ExampleNestedCustomObject(value);
        ObjectMapper<ExampleCustomObject> mapper = new ObjectMapper<ExampleCustomObject>() {
            @Override
            public @NotNull ExampleCustomObject createInstance(@Unmodifiable @NotNull Map<@NotNull String, @NotNull Object> values) throws InvalidTypeException, ValidationException {
                Assertions.assertFalse(values.containsKey("exampleOptionalString"));
                Assertions.assertFalse(values.containsKey("exampleOptionalInt"));
                Assertions.assertFalse(values.containsKey("exampleOptionalEnum"));
                return new ExampleCustomObject((String) values.get("exampleString"), (int) values.get("exampleInt"), (ExampleEnum) values.get("exampleEnum"));
            }

            @Override
            public @Unmodifiable @NotNull Map<@NotNull String, @NotNull Object> readValues(@NotNull ExampleCustomObject instance) throws InvalidTypeException {
                Map<String, Object> values = new HashMap<>();
                values.put("exampleString", instance.exampleString);
                values.put("exampleInt", instance.exampleInt);
                values.put("exampleEnum", instance.exampleEnum);
                values.put("exampleOptionalString", null);
                values.put("exampleOptionalInt", null);
                values.put("exampleOptionalEnum", null);
                return Collections.unmodifiableMap(values);
            }

            @Override
            public @NotNull Class<ExampleCustomObject> getObjectClass() {
                return ExampleCustomObject.class;
            }

            @Override
            public @NotNull Property<?> @NotNull [] getProperties() {
                return new Property[]{Property.of("exampleString", Type.STRING),
                        Property.of("exampleInt", Type.INTEGER, true),
                        Property.of("exampleEnum", Type.enumType(ExampleEnum.class)),
                        Property.of("exampleOptionalString", Type.STRING, true),
                        Property.of("exampleOptionalInt", Type.STRING, true),
                        Property.of("exampleOptionalEnum", Type.STRING, true)
                };
            }
        };
        Config config0 = this.createTestConfig();
        ConfigEntry<ExampleCustomObject> entry0 = CustomConfigEntry.of(config0, "example.custom", "Hello World", value, ExampleCustomObject.class);
        ConfigEntry<ExampleNestedCustomObject> entry1 = CustomConfigEntry.of(config0, "example.nestedCustom", "Hello World", nestedValue, ExampleNestedCustomObject.class);
        ConfigEntry<ExampleCustomObject> entry2 = CustomConfigEntry.of(config0, "example.mapper.custom", "Hello World", value, mapper);

        config0.load();
        config0.write();
        config0.close();

        Config config1 = this.createTestConfig();
        ConfigEntry<ExampleCustomObject> entryLoaded0 = CustomConfigEntry.of(config1, "example.custom", "Hello World", new ExampleCustomObject("Default1", 6, ExampleEnum.ANOTHER_EXAMPLE));
        ConfigEntry<ExampleNestedCustomObject> entryLoaded1 = CustomConfigEntry.of(config1, "example.nestedCustom", "Hello World", new ExampleNestedCustomObject(new ExampleCustomObject("Default1", 6, ExampleEnum.ANOTHER_EXAMPLE)));
        ConfigEntry<ExampleCustomObject> entryLoaded2 = CustomConfigEntry.of(config1, "example.mapper.custom", "Hello World", new ExampleCustomObject("Default1", 6, ExampleEnum.ANOTHER_EXAMPLE), mapper);

        Assertions.assertThrows(InvalidAnnotationConfigException.class, () -> CustomConfigEntry.of(config1, "example.nestedCustomInvalid", new ExampleCircularCustomObject(), ExampleCircularCustomObject.class));

        config1.load();
        config1.close();

        Assertions.assertEquals(value, entryLoaded0.getValue());
        Assertions.assertEquals(nestedValue, entryLoaded1.getValue());
        Assertions.assertEquals(value, entryLoaded2.getValue());
    }

    @Test
    public void testAnnotation() throws IOException {
        AnnotationManager annotationManager = AnnotationManager.create();

        Config config0 = this.createTestConfig();
        ExampleAnnotationConfig annotationConfig0 = annotationManager.registerEntries(config0, ExampleAnnotationConfig.class);

        config0.load();

        Assertions.assertEquals(new ExampleAnnotationConfig().annotationTestString, annotationConfig0.annotationTestString);

        annotationConfig0.annotationTestString = "Hello World";

        config0.write();
        config0.close();

        Config config1 = this.createTestConfig();
        ExampleAnnotationConfig annotationConfig1 = annotationManager.registerEntries(config1, ExampleAnnotationConfig.class);

        config1.load();

        Assertions.assertEquals("Hello World", annotationConfig1.annotationTestString);

        annotationConfig1.annotationTestString = "1234";

        Assertions.assertThrows(ValidationException.class, config1::write);

        annotationConfig1.annotationTestString = null;

        Assertions.assertThrows(InvalidTypeException.class, config1::write);

        config1.close();
    }

    @Test
    public void testAnnotationMap() throws IOException {
        AnnotationManager annotationManager = AnnotationManager.create();

        Config config0 = this.createTestConfig();
        ExampleAnnotationMapConfig annotationConfig0 = annotationManager.registerEntries(config0, ExampleAnnotationMapConfig.class);

        config0.load();

        Map<Integer, String> innerMap = new HashMap<>();
        innerMap.put(2, "Hello There");
        innerMap.put(5, "Hello World");

        Map<Integer, Map<Integer, String>> outerMap = new HashMap<>();
        outerMap.put(11, innerMap);

        annotationConfig0.test0 = outerMap;
        annotationConfig0.test1 = outerMap;

        config0.write();
        config0.close();

        Config config1 = this.createTestConfig();
        ExampleAnnotationMapConfig annotationConfig1 = annotationManager.registerEntries(config1, ExampleAnnotationMapConfig.class);

        config1.load();

        Assertions.assertEquals("Hello World", annotationConfig1.test0.get(11).get(5));
        Assertions.assertEquals("Hello World", ((Map<?, ?>) annotationConfig1.test1.get(11)).get(5));

        config1.close();
    }
}
