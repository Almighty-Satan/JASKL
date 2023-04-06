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

package com.github.almightysatan.jaskl.yaml;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static com.github.almightysatan.test.ConfigTest.*;

public class YamlConfigTest {

    File file0 = new File("src/test/resources/example.yaml");
    File file1 = new File("build/tmp/test/write.yaml");
    File file2 = new File("build/tmp/test/writeCustom.yaml");

    @Test
    public void testLoadYaml() throws IOException {
        testLoad(() -> YamlConfig.of(file0, "Example YAML Config"));
    }

    @Test
    public void testLoadAfterClosedYaml() throws IOException {
        testLoadAfterClosed(() -> YamlConfig.of(file0, "Example YAML Config"));
    }

    @Test
    public void testAlreadyLoadedYaml() throws IOException {
        testAlreadyLoaded(() -> YamlConfig.of(file0, "Example YAML Config"));
    }

    @Test
    public void testLoadValuesYaml() throws IOException {
        testLoadValues(() -> YamlConfig.of(file0, "Example YAML Config"));
    }

    @Test
    public void testEnumValuesYaml() throws IOException {
        testEnumValues(() -> YamlConfig.of(file0, "Example YAML Config"));
    }

    @Test
    public void testListValuesYaml() throws IOException {
        testListValues(() -> YamlConfig.of(file0, "Example YAML Config"));
    }

    @Test
    public void testMapValuesYaml() throws IOException {
        testMapValues(() -> YamlConfig.of(file0, "Example YAML Config"));
    }

    @Test
    public void testInvalidPathsYaml() throws IOException {
        testInvalidPaths(() -> YamlConfig.of(file0, "Example YAML Config"));
    }

    @Test
    public void testWriteAndLoadYaml() throws IOException {
        testWriteAndLoad(() -> YamlConfig.of(file1, "Example YAML Config"), file1);
    }

    @Test
    public void testWriteAndLoadListYaml() throws IOException {
        testWriteAndLoadList(() -> YamlConfig.of(file1, "Example YAML Config"), file1);
    }

    @Test
    public void testStripYaml() throws IOException {
        testStrip(() -> YamlConfig.of(file1, "Example YAML Config"), file1);
    }

    @Test
    public void testCustomYaml() throws IOException {
        testCustom(() -> YamlConfig.of(file2, "Example YAML Config"));
    }
}
