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

package io.github.almightysatan.jaskl.toml;

import io.github.almightysatan.jaskl.Config;
import io.github.almightysatan.jaskl.test.ConfigTest;

import java.io.File;

public class TomlConfigTest extends ConfigTest {

    private static final File FILE_EMPTY = new File("src/test/resources/empty.toml");
    private static final File FILE_EXAMPLE = new File("src/test/resources/example.toml");
    private static final File FILE_TEST = new File("build/tmp/test/test.toml");

    @Override
    protected Config createEmptyConfig() {
        return TomlConfig.of(FILE_EMPTY);
    }

    @Override
    protected Config createExampleConfig() {
        return TomlConfig.of(FILE_EXAMPLE);
    }

    @Override
    protected Config createTestConfig() {
        return TomlConfig.of(FILE_TEST);
    }

    @Override
    protected void clearTestConfig() {
        FILE_TEST.delete();
    }

    @Override
    protected boolean testConfigExists() {
        return FILE_TEST.exists();
    }
}
