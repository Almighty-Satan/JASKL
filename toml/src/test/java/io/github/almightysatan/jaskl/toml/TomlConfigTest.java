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

package io.github.almightysatan.jaskl.toml;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static io.github.almightysatan.test.ConfigTest.*;

public class TomlConfigTest {

    File file0 = new File("src/test/resources/example.toml");
    File file1 = new File("build/tmp/test/write.toml");
    File file2 = new File("build/tmp/test/writeCustom.toml");

    @Test
    public void testLoadToml() throws IOException {
        testLoad(() -> TomlConfig.of(file0, "Example TOML Config"));
    }

    @Test
    public void testLoadAfterClosedToml() throws IOException {
        testLoadAfterClosed(() -> TomlConfig.of(file0, "Example TOML Config"));
    }

    @Test
    public void testAlreadyLoadedToml() throws IOException {
        testAlreadyLoaded(() -> TomlConfig.of(file0, "Example TOML Config"));
    }

    @Test
    public void testTomlValues0() throws IOException {
        testLoadValues(() -> TomlConfig.of(file0, "Example TOML Config"));
    }

    @Test
    public void testValidationToml() throws IOException {
        testValidation(() -> TomlConfig.of(file0, "Example TOML Config"));
    }

    @Test
    public void testEnumValuesToml() throws IOException {
        testEnumValues(() -> TomlConfig.of(file0, "Example TOML Config"));
    }

    @Test
    public void testListValuesToml() throws IOException {
        testListValues(() -> TomlConfig.of(file0, "Example TOML Config"));
    }

    @Test
    public void testMapValuesToml() throws IOException {
        testMapValues(() -> TomlConfig.of(file0, "Example TOML Config"));
    }

    @Test
    public void testInvalidPathsToml() throws IOException {
        testInvalidPaths(() -> TomlConfig.of(file0, "Example TOML Config"));
    }

    @Test
    public void testWriteAndLoadToml() throws IOException {
        testWriteAndLoad(() -> TomlConfig.of(file1, "Example TOML Config"), file1);
    }

    @Test
    public void testWriteAndLoadListToml() throws IOException {
        testWriteAndLoadList(() -> TomlConfig.of(file1, "Example TOML Config"), file1);
    }

    @Test
    public void testWriteAndLoadList2Toml() throws IOException {
        testWriteAndLoadList2(() -> TomlConfig.of(file1, "Example TOML Config"), file1);
    }

    @Test
    public void testWriteAndLoadListEnumToml() throws IOException {
        testWriteAndLoadListEnum(() -> TomlConfig.of(file1, "Example TOML Config"), file1);
    }

    @Test
    public void testWriteAndLoadMapToml() throws IOException {
        testWriteAndLoadMap(() -> TomlConfig.of(file1, "Example TOML Config"), file1);
    }

    @Test
    public void testStripToml() throws IOException {
        testStrip(() -> TomlConfig.of(file1, "Example TOML Config"), file1);
    }

    @Test
    public void testCustomToml() throws IOException {
        testCustom(() -> TomlConfig.of(file2, "Example TOML Config"));
    }
}
