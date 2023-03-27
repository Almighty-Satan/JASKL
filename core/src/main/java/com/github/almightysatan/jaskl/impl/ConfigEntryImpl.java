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

package com.github.almightysatan.jaskl.impl;

import com.github.almightysatan.jaskl.ConfigEntry;
import com.github.almightysatan.jaskl.InvalidTypeException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class ConfigEntryImpl<T> implements ConfigEntry<T> {

    private final String path;
    private final String description;
    private final T defaultValue;

    public ConfigEntryImpl(@NotNull String path, @Nullable String description, @NotNull T defaultValue) {
        this.path = Objects.requireNonNull(path);
        if (path.isEmpty())
            throw new IllegalArgumentException("path cannot be empty!");
        this.description = description;
        this.defaultValue = Objects.requireNonNull(defaultValue);
    }

    @Override
    public @NotNull String getPath() {
        return this.path;
    }

    @Override
    public @Nullable String getDescription() {
        return this.description;
    }

    @Override
    public @NotNull T getDefaultValue() {
        return this.defaultValue;
    }

    protected @NotNull T checkType(@NotNull Object type) {
        if (this.getDefaultValue().getClass() != type.getClass())
            throw new InvalidTypeException(this.getPath(), this.getDefaultValue().getClass(), type.getClass());
        return (T) type;
    }
}
