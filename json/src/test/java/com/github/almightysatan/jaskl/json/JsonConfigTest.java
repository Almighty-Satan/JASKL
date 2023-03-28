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

package com.github.almightysatan.jaskl.json;

import com.github.almightysatan.jaskl.Config;
import com.github.almightysatan.jaskl.ConfigEntry;
import com.github.almightysatan.jaskl.entries.DoubleConfigEntry;
import com.github.almightysatan.jaskl.entries.IntegerConfigEntry;
import com.github.almightysatan.jaskl.entries.LongConfigEntry;
import com.github.almightysatan.jaskl.entries.StringConfigEntry;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonConfigTest {

    @Test
    public void testLoadSimple() throws IOException {
        Config config = JsonConfig.of(new File("src/test/resources/basic.json"), null);
        ConfigEntry<String> stringConfigEntry = StringConfigEntry.of(config, "exampleString", null, "default");
        ConfigEntry<Double> doubleConfigEntry = DoubleConfigEntry.of(config, "exampleDouble", null, 0.0D);
        ConfigEntry<Integer> integerConfigEntry = IntegerConfigEntry.of(config, "exampleInteger", null, 0);
        ConfigEntry<Long> longConfigEntry = LongConfigEntry.of(config, "exampleLong", null, 0L);

        config.load();

        assertEquals("String", stringConfigEntry.getValue());
        assertEquals(4.2, doubleConfigEntry.getValue());
        assertEquals(123, integerConfigEntry.getValue());
        assertEquals(123L, longConfigEntry.getValue());

        config.close();
    }

    @Test
    public void testLoadComplex() throws IOException {
        Config config = JsonConfig.of(new File("src/test/resources/complex.json"), null);
        ConfigEntry<String> subEntry = StringConfigEntry.of(config, "subCategory.subEntry", null, "default");
        ConfigEntry<String> subSubEntry = StringConfigEntry.of(config, "subCategory.subSubCategory.subSubEntry", null, "default");

        config.load();

        assertEquals("Sub", subEntry.getValue());
        assertEquals("SubSub", subSubEntry.getValue());

        config.close();
    }

    @Test
    public void testLoadNonExisting() throws IOException {
        Config config = JsonConfig.of(new File("src/test/resources/simple.json"), null);

        ConfigEntry<String> nonExistingStringConfigEntry = StringConfigEntry.of(config, "doesnotexist", null, "default");

        config.load();

        assertEquals("default", nonExistingStringConfigEntry.getValue()); // Doesn't exist in the file therefore the default value should be used

        config.close();
    }

    @Test
    public void testWriteFile() throws IOException {
        File file = new File("build/temp/json/write.json");
        file.delete();

        Config config0 = JsonConfig.of(file, null);
        ConfigEntry<String> stringConfigEntry0 = StringConfigEntry.of(config0, "abc.exampleString", null, "default");
        ConfigEntry<Integer> intConfigEntry0 = IntegerConfigEntry.of(config0, "abc.exampleInt", null, 69);

        config0.load();

        stringConfigEntry0.setValue("Example");
        intConfigEntry0.setValue(420);

        config0.write();
        config0.close();

        assertTrue(file.exists());

        Config config1 = JsonConfig.of(file, null);
        ConfigEntry<String> stringConfigEntry1 = StringConfigEntry.of(config1, "abc.exampleString", null, "default");
        ConfigEntry<Integer> intConfigEntry1 = IntegerConfigEntry.of(config1, "abc.exampleInt", null, 69);

        config1.load();

        assertEquals("Example", stringConfigEntry1.getValue());
        assertEquals(420, intConfigEntry1.getValue());
    }
}