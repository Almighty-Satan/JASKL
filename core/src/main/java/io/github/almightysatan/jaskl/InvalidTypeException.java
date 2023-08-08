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

package io.github.almightysatan.jaskl;

import org.jetbrains.annotations.NotNull;

public class InvalidTypeException extends RuntimeException {

    public InvalidTypeException(@NotNull Class<?> expected, @NotNull Class<?> actual) {
        super(String.format("Invalid type: expected=%s, actual=%s", expected.getName(), actual.getName()));
    }

    public InvalidTypeException(@NotNull Class<?> enumClass, @NotNull String enumName) {
        super(String.format("Invalid type: enumClass=%s enumName=%s", enumClass.getName(), enumName));
    }

    public InvalidTypeException(@NotNull String path, @NotNull Class<?> type) {
        super(String.format("Unknown type: path=%s, type=%s", path, type.getName()));
    }

    public InvalidTypeException(@NotNull String path, @NotNull InvalidTypeException cause) {
        super(String.format("Invalid type: path=%s", path), cause);
    }

    public InvalidTypeException(@NotNull String path) {
        super(String.format("Invalid type: path=%s is null", path));
    }
}
