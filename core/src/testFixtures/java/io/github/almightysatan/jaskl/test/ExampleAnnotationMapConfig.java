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

import io.github.almightysatan.jaskl.annotation.Entry;
import io.github.almightysatan.jaskl.annotation.TypeHint;
import io.github.almightysatan.jaskl.annotation.Validate;

import java.util.HashMap;
import java.util.Map;

public class ExampleAnnotationMapConfig {

    @Entry("test.annotation.map0")
    public Map<Integer, Map<Integer, String>> test0 = new HashMap<>();

    @Entry(value = "test.annotation.map1")
    @TypeHint({Map.class, Integer.class, Map.class, Integer.class, String.class})
    @Validate.MapMaxSize(1)
    public Map<?, ?> test1 = new HashMap<>();

    public ExampleAnnotationMapConfig() {}
}
