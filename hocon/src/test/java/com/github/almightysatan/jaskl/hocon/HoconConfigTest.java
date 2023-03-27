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

package com.github.almightysatan.jaskl.hocon;

import com.github.almightysatan.jaskl.Config;
import com.github.almightysatan.jaskl.ConfigEntry;
import com.github.almightysatan.jaskl.entries.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HoconConfigTest {

    @Test
    public void testFile() throws IOException {
        Config config = HoconConfig.of(new File("src/test/resources/basic.hocon"), null);
        ConfigEntry<String> stringConfigEntry = StringConfigEntry.of(config, "hocon.exampleString", null, "default");
        ConfigEntry<Integer> intConfigEntry = IntegerConfigEntry.of(config, "hocon.exampleInt", null, 0);
        ConfigEntry<Boolean> boolConfigEntry = BooleanConfigEntry.of(config, "hocon.exampleBool", null, false);
        ConfigEntry<Double> floatConfigEntry = DoubleConfigEntry.of(config, "hocon.exampleDouble", null, 0.0D);

        config.load();

        assertEquals("String", stringConfigEntry.getValue());
        assertEquals(42, intConfigEntry.getValue());
        assertEquals(true, boolConfigEntry.getValue());
        assertEquals(6.9D, floatConfigEntry.getValue());

        config.close();
    }

    @Test
    public void testLoadNonExisting() throws IOException {
        Config config = HoconConfig.of(new File("src/test/resources/basic.hocon"), null);

        ConfigEntry<String> nonExistingStringConfigEntry = StringConfigEntry.of(config, "hocon.doesnotexist", null, "default");

        config.load();

        assertEquals("default", nonExistingStringConfigEntry.getValue()); // Doesn't exist in the file therefore the default value should be used

        config.close();
    }

    @Test
    public void testList() throws IOException {
        Config config = HoconConfig.of(new File("src/test/resources/list.hocon"), null);
        ConfigEntry<List<String>> stringConfigEntry = ListConfigEntry.of(config, "hocon.subConf.exampleList", null, Collections.emptyList());

        config.load();

        assertEquals(Arrays.asList("ListContent1", "ListContent2"), stringConfigEntry.getValue());

        config.close();
    }
}
