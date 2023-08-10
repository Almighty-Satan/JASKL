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

package io.github.almightysatan.jaskl.hocon;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static io.github.almightysatan.jaskl.test.ConfigTest.*;

public class HoconConfigTest {

    File fileEmpty = new File("src/test/resources/empty.hocon");
    File file0 = new File("src/test/resources/example.hocon");
    File file1 = new File("build/tmp/test/write.hocon");
    File file2 = new File("build/tmp/test/writeCustom.hocon");

    @Test
    public void testLoadHocon() throws IOException {
        testLoad(() -> HoconConfig.of(file0));
    }

    @Test
    public void testLoadAfterClosedHocon() throws IOException {
        testLoadAfterClosed(() -> HoconConfig.of(file0));
    }

    @Test
    public void testAlreadyLoadedHocon() throws IOException {
        testAlreadyLoaded(() -> HoconConfig.of(file0));
    }

    @Test
    public void testEmptyConfigHocon() throws IOException {
        testEmptyConfig(() -> HoconConfig.of(fileEmpty));
    }

    @Test
    public void testLoadValuesHocon() throws IOException {
        testLoadValues(() -> HoconConfig.of(file0));
    }

    @Test
    public void testValidationHocon() throws IOException {
        testValidation(() -> HoconConfig.of(file0));
    }

    @Test
    public void testEnumValuesHocon() throws IOException {
        testEnumValues(() -> HoconConfig.of(file0));
    }

    @Test
    public void testListValuesHocon() throws IOException {
        testListValues(() -> HoconConfig.of(file0));
    }

    @Test
    public void testMapValuesHocon() throws IOException {
        testMapValues(() -> HoconConfig.of(file0));
    }

    @Test
    public void testWriteAndLoadHocon() throws IOException {
        testWriteAndLoad(() -> HoconConfig.of(file1), file1);
    }

    @Test
    public void testWriteAndLoadListHocon() throws IOException {
        testWriteAndLoadList(() -> HoconConfig.of(file1), file1);
    }

    @Test
    public void testWriteAndLoadList2Hocon() throws IOException {
        testWriteAndLoadList2(() -> HoconConfig.of(file1), file1);
    }

    @Test
    public void testWriteAndLoadListEnumHocon() throws IOException {
        testWriteAndLoadListEnum(() -> HoconConfig.of(file1), file1);
    }

    @Test
    public void testWriteAndLoadMapHocon() throws IOException {
        testWriteAndLoadMap(() -> HoconConfig.of(file1), file1);
    }

    @Test
    public void testStripHocon() throws IOException {
        testStrip(() -> HoconConfig.of(file1), file1);
    }

    @Test
    public void testStripMapHocon() throws IOException {
        testStripMap(() -> HoconConfig.of(file1), file1);
    }

    @Test
    public void testCustomHocon() throws IOException {
        testCustom(() -> HoconConfig.of(file2));
    }

    @Test
    public void testAnnotationHocon() throws IOException {
        testAnnotation(() -> HoconConfig.of(file1));
    }

    @Test
    public void testAnnotationMapHocon() throws IOException {
        testAnnotationMap(() -> HoconConfig.of(file1));
    }
}
