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

package io.github.almightysatan.jaskl.impl;

import io.github.almightysatan.jaskl.ConfigEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class ConfigEntryImpl<T> implements ConfigEntry<T> {

    private final String path;
    private final String description;
    private final T defaultValue;

    public ConfigEntryImpl(@NotNull String path, @Nullable String description, @NotNull T defaultValue) {
        this.checkPath(Objects.requireNonNull(path));
        this.path = path;
        this.description = description;
        this.defaultValue = Objects.requireNonNull(defaultValue);
    }

    private void checkPath(@NotNull String path) {
        if (path.isEmpty())
            throw new IllegalArgumentException("Path should not be empty!");

        boolean prevDot = false;
        char[] chars = path.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];

            if (c == 0x2E) {
                if (i == 0 || i == chars.length - 1)
                    throw new IllegalArgumentException("Path cannot start or end with a dot!");
                if (prevDot)
                    throw new IllegalArgumentException("Path cannot contain two or more dots directly following each other!");
                prevDot = true;
                continue;
            }
            prevDot = false;

            // digits - uppercase letters - lowercase letters - legal special chars (e.g. dash)
            if ((c >= 0x30 && c <= 0x39) || (c >= 0x41 && c <= 0x5A) || (c >= 0x61 && c <= 0x7A) || c == 0x5F || c == 0x2D)
                continue;

            throw new IllegalArgumentException("Path contains invalid characters!");
        }
    }

    @Override
    public @NotNull String path() {
        return this.path;
    }

    @Override
    public @Nullable String description() {
        return this.description;
    }

    @Override
    public @NotNull T defaultValue() {
        return this.defaultValue;
    }
}
