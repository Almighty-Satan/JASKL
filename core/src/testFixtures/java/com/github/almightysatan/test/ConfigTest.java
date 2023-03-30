/*
 * JASKL - Just Another Simple Konfig Library
 * Copyright (C) 2023 UeberallGebannt, Almighty-Satan
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

package com.github.almightysatan.test;

import com.github.almightysatan.jaskl.Config;
import com.github.almightysatan.jaskl.ConfigEntry;
import com.github.almightysatan.jaskl.entries.*;
import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class ConfigTest {

    /**
     * Test if the config can be loaded successfully.
     */
    public static void testLoad(Supplier<Config> configSupplier) throws IOException {
        Config config = configSupplier.get();
        config.load();

        config.close();
    }


    /**
     * Test if the config can be loaded successfully after closing it.
     */
    public static void testLoadAfterClosed(Supplier<Config> configSupplier) throws IOException {
        Config config = configSupplier.get();
        config.load();
        config.close();
        config.load();

        config.close();
    }


    /**
     * Test if the config cannot be loaded when it has been loaded before.
     */
    public static void testAlreadyLoaded(Supplier<Config> configSupplier) throws IOException {
        Config config = configSupplier.get();
        config.load();
        Assertions.assertThrows(IllegalStateException.class, () -> config.load());

        config.close();
    }


    /**
     * Test if a config's values can be loaded successfully.
     * This test requires a predefined config file with inserted values.
     */
    public static void testLoadValues(Supplier<Config> configSupplier) throws IOException {
        Config config = configSupplier.get();

        ConfigEntry<Boolean> booleanConfigEntry = BooleanConfigEntry.of(config, "example.boolean", "Example Boolean", false);
        ConfigEntry<Double> doubleConfigEntry = DoubleConfigEntry.of(config, "example.double", "Example Double", 0.0D);
        ConfigEntry<Float> floatConfigEntry = FloatConfigEntry.of(config, "example.float", "Example Float", 0.0F);
        ConfigEntry<Integer> integerConfigEntry = IntegerConfigEntry.of(config, "example.integer", "Example Integer", 0);
        ConfigEntry<Long> longConfigEntry = LongConfigEntry.of(config, "example.long", "Example Long", 0L);
        ConfigEntry<String> stringConfigEntry = StringConfigEntry.of(config, "example.string", "Example String", "default");

        config.load();

        Assertions.assertEquals(true, booleanConfigEntry.getValue());
        Assertions.assertEquals(1.0D, doubleConfigEntry.getValue());
        Assertions.assertEquals(1.0F, floatConfigEntry.getValue());
        Assertions.assertEquals(1, integerConfigEntry.getValue());
        Assertions.assertEquals(1L, longConfigEntry.getValue());
        Assertions.assertEquals("modified", stringConfigEntry.getValue());

        config.close();
    }


    /**
     * Test if enum values can be loaded successfully.
     * This test requires a predefined config file with inserted values.
     */
    public static void testEnumValues(Supplier<Config> configSupplier) throws IOException {
        Config config = configSupplier.get();

        ConfigEntry<ExampleEnum> enumConfigEntry = EnumConfigEntry.of(config, "example.enum", "Example Enum", ExampleEnum.EXAMPLE);

        config.load();

        Assertions.assertEquals(ExampleEnum.ANOTHER_EXAMPLE, enumConfigEntry.getValue());

        config.close();
    }


    /**
     * Test if list values can be loaded successfully.
     * This test requires a predefined config file with inserted values.
     */
    public static void testListValues(Supplier<Config> configSupplier) throws IOException {
        Config config = configSupplier.get();

        List<String> example0 = Arrays.asList("Example1", "Example2");
        ConfigEntry<List<String>> listConfigEntry = ListConfigEntry.of(config, "example.list", "Example List", example0);

        config.load();

        List<String> example1 = Arrays.asList("Example3", "Example4");
        Assertions.assertEquals(example1, listConfigEntry.getValue());

        config.close();
    }


    /**
     * Test if custom values can be loaded successfully.
     * This test requires a predefined config file with inserted values.
     */
    public static void testCustomValues(Supplier<Config> configSupplier) throws IOException {
        // TODO: Implement custom entries correctly
    }


    /**
     * Test if map values can be loaded successfully.
     * This test requires a predefined config file with inserted values.
     */
    public static void testMapValues(Supplier<Config> configSupplier) throws IOException {
        // TODO: Implement maps correctly
    }


    /**
     * Test if an entry cannot be registered twice (same path).
     */
    public static void testInvalidPaths(Supplier<Config> configSupplier) {
        Config config = configSupplier.get();
        BooleanConfigEntry.of(config, "example.boolean", "Example Boolean", false);
        Assertions.assertThrows(IllegalArgumentException.class, () -> BooleanConfigEntry.of(config, "example.boolean", "Example Boolean", false));
    }


    /**
     * Test if a config can be created, saved and loaded again.
     * This test requires a valid file path.
     */
    public static void testWriteAndLoad(Supplier<Config> configSupplier, File file) throws IOException {
        if (file.exists() && !file.delete())
            Assertions.fail(String.format("Couldn't delete file %s even though it exists.", file));

        Config config0 = configSupplier.get();
        ConfigEntry<String> stringConfigEntry0 = StringConfigEntry.of(config0, "example.string", "Example String", "default");
        ConfigEntry<Integer> intConfigEntry0 = IntegerConfigEntry.of(config0, "example.integer", "Example String", 0);

        config0.load();

        stringConfigEntry0.setValue("modified");
        intConfigEntry0.setValue(1);

        config0.write();
        config0.close();

        Assertions.assertTrue(file.exists());

        Config config1 = configSupplier.get();
        ConfigEntry<String> stringConfigEntry1 = StringConfigEntry.of(config1, "example.string", "Example String", "default");
        ConfigEntry<Integer> intConfigEntry1 = IntegerConfigEntry.of(config1, "example.integer", "Example String", 0);

        config1.load();

        Assertions.assertEquals("modified", stringConfigEntry1.getValue());
        Assertions.assertEquals(1, intConfigEntry1.getValue());

        config1.close();
    }


    /**
     * Test if a config can be created, saved and loaded again.
     * This test requires a valid file path.
     */
    public static void testStrip(Supplier<Config> configSupplier, File file) throws IOException {
        if (file.exists() && !file.delete())
            Assertions.fail(String.format("Couldn't delete file %s even though it exists.", file));

        Config config0 = configSupplier.get();
        ConfigEntry<String> stringConfigEntry0 = StringConfigEntry.of(config0, "example.string", "Example String", "default");
        ConfigEntry<Integer> intConfigEntry0 = IntegerConfigEntry.of(config0, "example.integer", "Example String", 0);

        config0.load();

        stringConfigEntry0.setValue("modified");
        intConfigEntry0.setValue(1);

        config0.write();
        config0.close();

        Config config1 = configSupplier.get();
        ConfigEntry<String> stringConfigEntry1 = StringConfigEntry.of(config1, "example.string", "Example String", "default");

        config1.load();
        config1.strip();
        config1.close();

        Config config2 = configSupplier.get();
        ConfigEntry<String> stringConfigEntry2 = StringConfigEntry.of(config2, "example.string", "Example String", "default");
        ConfigEntry<Integer> intConfigEntry2 = IntegerConfigEntry.of(config2, "example.integer", "Example String", 0);

        config2.load();

        Assertions.assertEquals("modified", stringConfigEntry2.getValue());
        Assertions.assertEquals(0, intConfigEntry2.getValue());
    }

    public static void testCustom(Supplier<Config> configSupplier) throws IOException {
        ExampleCustomObject value = new ExampleCustomObject("Default", 5, ExampleEnum.EXAMPLE);
        Config config0 = configSupplier.get();
        ConfigEntry<ExampleCustomObject> entry0 = CustomConfigEntry.of(config0, "example", "Hello World", value);

        config0.load();
        config0.write();

        Config config1 = configSupplier.get();
        ConfigEntry<ExampleCustomObject> entry1 = CustomConfigEntry.of(config1, "example", "Hello World", new ExampleCustomObject("Default1", 6, ExampleEnum.ANOTHER_EXAMPLE));

        config0.load();

        Assertions.assertEquals(value, entry1.getValue());
    }
}
