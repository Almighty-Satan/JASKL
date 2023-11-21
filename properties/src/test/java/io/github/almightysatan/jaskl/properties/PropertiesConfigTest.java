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

package io.github.almightysatan.jaskl.properties;

import io.github.almightysatan.jaskl.Config;
import io.github.almightysatan.jaskl.InvalidTypeException;
import io.github.almightysatan.jaskl.test.ConfigTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

public class PropertiesConfigTest extends ConfigTest {

    private static final File FILE_EMPTY = new File("src/test/resources/empty.properties");
    private static final File FILE_EXAMPLE = new File("src/test/resources/example.properties");
    private static final File FILE_TEST = new File("build/tmp/test/test.properties");

    @Override
    protected Config createEmptyConfig() {
        return PropertiesConfig.of(FILE_EMPTY);
    }

    @Override
    protected Config createExampleConfig() {
        return PropertiesConfig.of(FILE_EXAMPLE);
    }

    @Override
    protected Config createTestConfig() {
        return PropertiesConfig.of(FILE_TEST, "Test Properties config");
    }

    @Override
    protected void clearTestConfig() {
        FILE_TEST.delete();
    }

    @Override
    protected boolean testConfigExists() {
        return FILE_TEST.exists();
    }

    @Test
    @Override
    public void testListValues() throws IOException {
        Assertions.assertThrows(UnsupportedOperationException.class, super::testListValues);
    }

    @Test
    @Override
    public void testMapValues() throws IOException {
        Assertions.assertThrows(UnsupportedOperationException.class, super::testMapValues);
    }

    @Test
    @Override
    public void testWriteAndLoadList() throws IOException {
        Assertions.assertThrows(UnsupportedOperationException.class, super::testWriteAndLoadList);
    }

    @Test
    @Override
    public void testWriteAndLoadList2() throws IOException {
        Assertions.assertThrows(UnsupportedOperationException.class, super::testWriteAndLoadList2);
    }

    @Test
    @Override
    public void testWriteAndLoadListEnum() throws IOException {
        Assertions.assertThrows(UnsupportedOperationException.class, super::testWriteAndLoadListEnum);
    }

    @Test
    @Override
    public void testWriteAndLoadMap() throws IOException {
        Assertions.assertThrows(UnsupportedOperationException.class, super::testWriteAndLoadMap);
    }

    @Test
    @Override
    public void testStripMap() throws IOException {
        Assertions.assertThrows(UnsupportedOperationException.class, super::testStripMap);
    }

    @Test
    @Override
    public void testCustom() throws IOException {
        Assertions.assertThrows(InvalidTypeException.class, super::testCustom);
    }

    @Test
    @Override
    public void testAnnotationMap() throws IOException {
        Assertions.assertThrows(InvalidTypeException.class, super::testAnnotationMap);
    }
}
