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

package io.github.almightysatan.jaskl.mongodb;

import io.github.almightysatan.jaskl.Config;
import io.github.almightysatan.jaskl.ConfigEntry;
import io.github.almightysatan.jaskl.entries.IntegerConfigEntry;
import io.github.almightysatan.jaskl.entries.StringConfigEntry;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MongodbConfigTest {

    @Test
    @Disabled
    public void testWrite() throws IOException {
        String mongoAddress = System.getenv("MONGO_ADDRESS");

        Config config0 = MongodbConfig.of(mongoAddress, "configTest", "abc");
        ConfigEntry<String> stringConfigEntry0 = StringConfigEntry.of(config0, "exampleString", null, "default");
        ConfigEntry<Integer> intConfigEntry0 = IntegerConfigEntry.of(config0, "exampleInt", null, 69);

        config0.load();

        stringConfigEntry0.setValue("Example");
        intConfigEntry0.setValue(420);

        config0.write();
        config0.close();

        Config config1 = MongodbConfig.of(mongoAddress, "configTest", "abc");
        ConfigEntry<String> stringConfigEntry1 = StringConfigEntry.of(config1, "exampleString", null, "default");
        ConfigEntry<Integer> intConfigEntry1 = IntegerConfigEntry.of(config1, "exampleInt", null, 69);

        config1.load();
        config1.strip();

        assertEquals("Example", stringConfigEntry1.getValue());
        assertEquals(420, intConfigEntry1.getValue());
    }
}
