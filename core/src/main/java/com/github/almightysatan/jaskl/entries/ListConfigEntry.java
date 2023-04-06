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
import com.github.almightysatan.jaskl.Type;
import com.github.almightysatan.jaskl.impl.WritableConfigEntryImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class ListConfigEntry<T> extends WritableConfigEntryImpl<List<T>> {

    private final Type<List<T>> type;

    ListConfigEntry(@NotNull String path, @Nullable String description, @NotNull List<T> defaultValue, @NotNull Type<List<T>> type) {
        super(path, description, defaultValue);
        this.type = Objects.requireNonNull(type);
    }

    @Override
    protected @NotNull List<T> checkType(@NotNull Object type) {
        return this.type.cast(type);
    }

    public static <T> ConfigEntry<List<T>> of(@NotNull Config config, @NotNull String path, @Nullable String description, @NotNull List<T> defaultValue, @NotNull Type<T> type) {
        return new ListConfigEntry<>(path, description, defaultValue, Type.list(type)).register(config);
    }
}
