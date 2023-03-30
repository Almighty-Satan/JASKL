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
import com.github.almightysatan.jaskl.impl.ConfigEntryImpl;
import com.github.almightysatan.jaskl.impl.WritableConfigEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EnumConfigEntry<T extends Enum<T>> extends ConfigEntryImpl<T> implements WritableConfigEntry<T> {

    private final WritableConfigEntry<String> internal;
    private T value;

    EnumConfigEntry(@NotNull String path, @Nullable String description, @NotNull T defaultValue) {
        super(path, description, defaultValue);
        this.internal = new InternalConfigEntry(path, description, defaultValue.name());
        this.value = defaultValue;
    }

    @Override
    public WritableConfigEntry<T> register(@NotNull Config config) {
        this.internal.register(config);
        return this;
    }

    @Override
    public @NotNull T getValue() {
        return value;
    }

    @Override
    public void setValue(@NotNull T value) {
        this.internal.setValue(value.toString());
    }

    @Override
    public void putValue(@NotNull Object value) {
        this.internal.putValue(value.toString());
    }

    @Override
    public boolean isModified() {
        return this.internal.isModified();
    }

    public static <T extends Enum<T>> ConfigEntry<T> of(@NotNull Config config, @NotNull String path, @Nullable String description, T defaultValue) {
        return new EnumConfigEntry<>(path, description, defaultValue).register(config);
    }

    public class InternalConfigEntry extends StringConfigEntry {

        private InternalConfigEntry(@NotNull String path, @Nullable String description, @NotNull String defaultValue) {
            super(path, description, defaultValue);
        }

        @Override
        public void setValue(@NotNull String value) {
            super.setValue(value);
            EnumConfigEntry.this.value = this.getEnum(value);
        }

        @Override
        public void putValue(@NotNull Object value) {
            super.putValue(value);
            EnumConfigEntry.this.value = this.getEnum((String) value);
        }

        @Override
        protected @NotNull String checkType(@NotNull Object type) {
            String value = super.checkType(type);
            this.getEnum(value);
            return value;
        }

        @SuppressWarnings("unchecked")
        private T getEnum(String value) {
            try {
                return (T) Enum.valueOf(EnumConfigEntry.this.getDefaultValue().getClass(), value);
            } catch (IllegalArgumentException e) {
                throw new InvalidTypeException(this.getPath(), EnumConfigEntry.this.getDefaultValue().getClass(), value);
            }
        }
    }
}
