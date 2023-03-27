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
import com.github.almightysatan.jaskl.impl.ConfigEntryImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EnumConfigEntry<E extends Enum<E>> extends ConfigEntryImpl<E> {

    private final Class<E> enumClass;
    private final ConfigEntry<String> internal;

    public EnumConfigEntry(@NotNull Config config, @NotNull String path, @Nullable String description, @NotNull E defaultValue) {
        super(path, description, defaultValue);
        this.enumClass = (Class<E>) defaultValue.getClass();
        this.internal = StringConfigEntry.of(config, path, description, defaultValue.toString());
    }

    @Override
    public @NotNull E getValue() {
        return Enum.valueOf(this.enumClass, this.internal.getValue());
    }

    @Override
    public void setValue(@NotNull Enum value) {
        this.internal.setValue(value.toString());
    }

    public static <T extends Enum<T>> ConfigEntry<T> of(@NotNull Config config, @NotNull String path, @Nullable String description, T defaultValue) {
        return new EnumConfigEntry(config, path, description, defaultValue);
    }
}
