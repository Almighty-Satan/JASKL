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

package io.github.almightysatan.jaskl.test;

import io.github.almightysatan.jaskl.Config;
import io.github.almightysatan.jaskl.entries.BooleanConfigEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConfigImplTest {

    @Test
    public void testConfigPaths() {
        Config config = new NopConfigImpl();

        Assertions.assertThrows(IllegalArgumentException.class, () -> BooleanConfigEntry.of(config, "", false));
        Assertions.assertThrows(IllegalArgumentException.class, () -> BooleanConfigEntry.of(config, ".", false));
        Assertions.assertThrows(IllegalArgumentException.class, () -> BooleanConfigEntry.of(config, "x.", false));
        Assertions.assertThrows(IllegalArgumentException.class, () -> BooleanConfigEntry.of(config, ".x", false));
        Assertions.assertThrows(IllegalArgumentException.class, () -> BooleanConfigEntry.of(config, "x..y", false));
        Assertions.assertThrows(IllegalArgumentException.class, () -> BooleanConfigEntry.of(config, "!", false));
        Assertions.assertThrows(IllegalArgumentException.class, () -> BooleanConfigEntry.of(config, "x!x", false));

        BooleanConfigEntry.of(config, "abcdefghijklmnopqrstuvwxyz.ABCDEFGHIJKLMNOPQRSTUVWXYZ.0123456789", false);
        BooleanConfigEntry.of(config, "test.abc", false);
        BooleanConfigEntry.of(config, "test.def", false);
        BooleanConfigEntry.of(config, "test.defghi", false);
        BooleanConfigEntry.of(config, "testtest", false);

        Assertions.assertThrows(IllegalArgumentException.class, () -> BooleanConfigEntry.of(config, "test", false));
        Assertions.assertThrows(IllegalArgumentException.class, () -> BooleanConfigEntry.of(config, "test.def", false));
        Assertions.assertThrows(IllegalArgumentException.class, () -> BooleanConfigEntry.of(config, "test.def.ghi", false));
    }
}
