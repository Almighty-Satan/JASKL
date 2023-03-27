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

package com.github.almightysatan.jaskl.properties;

import com.github.almightysatan.jaskl.Config;
import com.github.almightysatan.jaskl.ConfigEntry;
import com.github.almightysatan.jaskl.entries.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PropertiesConfigTest {

    @Test
    public void testFile() throws IOException {
        Config config = PropertiesConfig.of(new File("src/test/resources/basic.properties"), null);
        ConfigEntry<String> stringConfigEntry = StringConfigEntry.of(config, "exampleString", null, "default");
        ConfigEntry<Integer> intConfigEntry = IntegerConfigEntry.of(config, "exampleInt", null, 0);
        ConfigEntry<Boolean> boolConfigEntry = BooleanConfigEntry.of(config, "exampleBool", null, false);
        ConfigEntry<Double> doubleConfigEntry = DoubleConfigEntry.of(config, "exampleDouble", null, 0.0d);
        ConfigEntry<Example> enumConfigEntry = EnumConfigEntry.of(config, "exampleEnum", null, Example.EXAMPLE);

        config.load();

        assertEquals("String", stringConfigEntry.getValue());
        assertEquals(42, intConfigEntry.getValue());
        assertEquals(true, boolConfigEntry.getValue());
        assertEquals(6.9d, doubleConfigEntry.getValue());
        assertEquals(Example.EXAMPLE2, enumConfigEntry.getValue());

        config.close();
    }

    @Test
    public void testLoadNonExisting() throws IOException {
        Config config = PropertiesConfig.of(new File("src/test/resources/basic.properties"), null);

        ConfigEntry<String> nonExistingStringConfigEntry = StringConfigEntry.of(config, "doesnotexist", null, "default");

        config.load();

        assertEquals("default", nonExistingStringConfigEntry.getValue()); // Doesn't exist in the file therefore the default value should be used

        config.close();
    }

    @Test
    public void testWriteFile() throws IOException {
        File file = new File("build/temp/properties/write.properties");
        file.delete();

        Config config0 = PropertiesConfig.of(file, null);
        ConfigEntry<String> stringConfigEntry0 = StringConfigEntry.of(config0, "exampleString", null, "default");
        ConfigEntry<Integer> intConfigEntry0 = IntegerConfigEntry.of(config0, "exampleInt", null, 69);

        config0.load();

        stringConfigEntry0.setValue("Example");
        intConfigEntry0.setValue(420);

        config0.write();
        config0.close();

        assertTrue(file.exists());

        Config config1 = PropertiesConfig.of(file, null);
        ConfigEntry<String> stringConfigEntry1 = StringConfigEntry.of(config1, "exampleString", null, "default");
        ConfigEntry<Integer> intConfigEntry1 = IntegerConfigEntry.of(config1, "exampleInt", null, 69);

        config1.load();

        assertEquals("Example", stringConfigEntry1.getValue());
        assertEquals(420, intConfigEntry1.getValue());
    }


    @Test
    public void testStripFile() throws IOException {
        File file = new File("build/temp/properties/strip.properties");
        file.delete();

        Config config0 = PropertiesConfig.of(file, null);
        ConfigEntry<String> stringConfigEntry0 = StringConfigEntry.of(config0, "exampleString", null, "default");
        ConfigEntry<Integer> intConfigEntry0 = IntegerConfigEntry.of(config0, "exampleInt", null, 69);

        stringConfigEntry0.setValue("String");
        intConfigEntry0.setValue(42);

        config0.load();
        config0.write();
        config0.close();

        Config config1 = PropertiesConfig.of(file, null);
        ConfigEntry<String> stringConfigEntry1 = StringConfigEntry.of(config1, "exampleString", null, "default");

        config1.load();
        config1.strip();
        config1.close();

        Config config2 = PropertiesConfig.of(file, null);
        ConfigEntry<String> stringConfigEntry2 = StringConfigEntry.of(config2, "exampleString", null, "default");
        ConfigEntry<Integer> intConfigEntry2 = IntegerConfigEntry.of(config2, "exampleInt", null, 69);

        config2.load();

        assertEquals("String", stringConfigEntry2.getValue());
        assertEquals(69, intConfigEntry2.getValue());

    }
}
