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

package io.github.almightysatan.jaskl.ini;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static io.github.almightysatan.jaskl.test.ConfigTest.*;

public class IniConfigTest {

    File fileEmpty = new File("src/test/resources/empty.ini");
    File file0 = new File("src/test/resources/example.ini");
    File file1 = new File("build/tmp/test/write.ini");
    File file2 = new File("build/tmp/test/writeCustom.ini");

    @Test
    public void testLoadIni() throws IOException {
        testLoad(() -> IniConfig.of(file0));
    }

    @Test
    public void testLoadAfterClosedIni() throws IOException {
        testLoadAfterClosed(() -> IniConfig.of(file0));
    }

    @Test
    public void testAlreadyLoadedIni() throws IOException {
        testAlreadyLoaded(() -> IniConfig.of(file0));
    }

    @Test
    public void testEmptyConfigIni() throws IOException {
        testEmptyConfig(() -> IniConfig.of(fileEmpty));
    }

    @Test
    public void testLoadValuesIni() throws IOException {
        testLoadValues(() -> IniConfig.of(file0));
    }

    @Test
    public void testValidationIni() {
        testValidation(() -> IniConfig.of(file0));
    }

    @Test
    public void testEnumValuesIni() throws IOException {
        testEnumValues(() -> IniConfig.of(file0));
    }

    @Test
    public void testListValuesIni() {
        Assertions.assertThrows(
                UnsupportedOperationException.class,
                () -> testListValues(() -> IniConfig.of(file0))
        );
    }

    @Test
    public void testMapValuesIni() {
        Assertions.assertThrows(UnsupportedOperationException.class,
                () -> testMapValues(() -> IniConfig.of(file0)));
    }

    @Test
    public void testWriteAndLoadIni() throws IOException {
        testWriteAndLoad(() -> IniConfig.of(file1), file1);
    }

    @Test
    public void testWriteAndLoadListIni() {
        Assertions.assertThrows(
                UnsupportedOperationException.class,
                () -> testWriteAndLoadList(() -> IniConfig.of(file1), file1)
        );
    }

    @Test
    public void testStripIni() throws IOException {
        testStrip(() -> IniConfig.of(file1), file1);
    }

    @Test
    public void testAnnotationIni() throws IOException {
        testAnnotation(() -> IniConfig.of(file1));
    }
}
