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

package io.github.almightysatan.jaskl.json;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static io.github.almightysatan.jaskl.test.ConfigTest.*;

public class JsonConfigTest {

    File file0 = new File("src/test/resources/example.json");
    File file1 = new File("build/tmp/test/write.json");
    File file2 = new File("build/tmp/test/writeCustom.json");

    @Test
    public void testLoadJson() throws IOException {
        testLoad(() -> JsonConfig.of(file0));
    }

    @Test
    public void testLoadAfterClosedJson() throws IOException {
        testLoadAfterClosed(() -> JsonConfig.of(file0));
    }

    @Test
    public void testAlreadyLoadedJson() throws IOException {
        testAlreadyLoaded(() -> JsonConfig.of(file0));
    }

    @Test
    public void testLoadValuesJson() throws IOException {
        testLoadValues(() -> JsonConfig.of(file0));
    }

    @Test
    public void testValidationJson() throws IOException {
        testValidation(() -> JsonConfig.of(file0));
    }

    @Test
    public void testEnumValuesJson() throws IOException {
        testEnumValues(() -> JsonConfig.of(file0));
    }

    @Test
    public void testListValuesJson() throws IOException {
        testListValues(() -> JsonConfig.of(file0));
    }

    @Test
    public void testMapValuesJson() throws IOException {
        testMapValues(() -> JsonConfig.of(file0));
    }

    @Test
    public void testInvalidPathsJson() throws IOException {
        testInvalidPaths(() -> JsonConfig.of(file0));
    }

    @Test
    public void testWriteAndLoadJson() throws IOException {
        testWriteAndLoad(() -> JsonConfig.of(file1), file1);
    }

    @Test
    public void testWriteAndLoadListJson() throws IOException {
        testWriteAndLoadList(() -> JsonConfig.of(file1), file1);
    }

    @Test
    public void testWriteAndLoadList2Json() throws IOException {
        testWriteAndLoadList2(() -> JsonConfig.of(file1), file1);
    }

    @Test
    public void testWriteAndLoadListEnumJson() throws IOException {
        testWriteAndLoadListEnum(() -> JsonConfig.of(file1), file1);
    }

    @Test
    public void testWriteAndLoadMapJson() throws IOException {
        testWriteAndLoadMap(() -> JsonConfig.of(file1), file1);
    }

    @Test
    public void testStripJson() throws IOException {
        testStrip(() -> JsonConfig.of(file1), file1);
    }

    @Test
    public void testStripMapJson() throws IOException {
        testStripMap(() -> JsonConfig.of(file1), file1);
    }

    @Test
    public void testCustomJson() throws IOException {
        testCustom(() -> JsonConfig.of(file2));
    }
}
