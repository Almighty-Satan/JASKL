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

package io.github.almightysatan.jaskl.properties;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static io.github.almightysatan.test.ConfigTest.*;

public class PropertiesConfigTest {

    File file0 = new File("src/test/resources/example.properties");
    File file1 = new File("build/tmp/test/write.properties");
    File file2 = new File("build/tmp/test/writeCustom.properties");

    @Test
    public void testLoadProperties() throws IOException {
        testLoad(() -> PropertiesConfig.of(file0, "Example Properties Config"));
    }

    @Test
    public void testLoadAfterClosedProperties() throws IOException {
        testLoadAfterClosed(() -> PropertiesConfig.of(file0, "Example Properties Config"));
    }

    @Test
    public void testAlreadyLoadedProperties() throws IOException {
        testAlreadyLoaded(() -> PropertiesConfig.of(file0, "Example Properties Config"));
    }

    @Test
    public void testLoadValuesProperties() throws IOException {
        testLoadValues(() -> PropertiesConfig.of(file0, "Example Properties Config"));
    }

    @Test
    public void testEnumValuesProperties() throws IOException {
        testEnumValues(() -> PropertiesConfig.of(file0, "Example Properties Config"));
    }

    @Test
    public void testListValuesProperties() throws IOException {
        Assertions.assertThrows(
                UnsupportedOperationException.class,
                () -> testListValues(() -> PropertiesConfig.of(file0, "Example Properties Config"))
        );
    }

    @Test
    public void testMapValuesProperties() throws IOException {
        testMapValues(() -> PropertiesConfig.of(file0, "Example Properties Config"));
    }

    @Test
    public void testInvalidPathsProperties() throws IOException {
        testInvalidPaths(() -> PropertiesConfig.of(file0, "Example Properties Config"));
    }

    @Test
    public void testWriteAndLoadProperties() throws IOException {
        testWriteAndLoad(() -> PropertiesConfig.of(file1, "Example Properties Config"), file1);
    }

    @Test
    public void testWriteAndLoadListProperties() throws IOException {
        Assertions.assertThrows(
                UnsupportedOperationException.class,
                () -> testWriteAndLoadList(() -> PropertiesConfig.of(file1, "Example Properties Config"), file1)
        );
    }

    @Test
    public void testStripProperties() throws IOException {
        testStrip(() -> PropertiesConfig.of(file1, "Example Properties Config"), file1);
    }

    @Test
    public void testCustomProperties() throws IOException {
        testCustom(() -> PropertiesConfig.of(file2, "Example Properties Config"));
    }
}
