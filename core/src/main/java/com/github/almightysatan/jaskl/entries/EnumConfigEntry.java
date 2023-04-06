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

package com.github.almightysatan.jaskl.entries;

import com.github.almightysatan.jaskl.Config;
import com.github.almightysatan.jaskl.ConfigEntry;
import com.github.almightysatan.jaskl.InvalidTypeException;
import com.github.almightysatan.jaskl.Type;
import com.github.almightysatan.jaskl.impl.WritableConfigEntryImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class EnumConfigEntry<T extends Enum<T>> extends WritableConfigEntryImpl<T> {

    EnumConfigEntry(@NotNull String path, @Nullable String description, @NotNull T defaultValue) {
        super(path, description, defaultValue);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void putValue(@NotNull Object value) {
        Objects.requireNonNull(value);
        String stringValue = Type.STRING.cast(value);
        try {
            super.putValue(Enum.valueOf(this.getDefaultValue().getClass(), stringValue));
        } catch (IllegalArgumentException e) {
            throw new InvalidTypeException(this.getPath(), this.getDefaultValue().getClass(), stringValue);
        }
    }

    @Override
    public @NotNull Object getValueToWrite() {
        return this.getValue().name();
    }

    public static <T extends Enum<T>> ConfigEntry<T> of(@NotNull Config config, @NotNull String path, @Nullable String description, T defaultValue) {
        return new EnumConfigEntry<>(path, description, defaultValue).register(config);
    }
}
