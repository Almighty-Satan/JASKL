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
import com.github.almightysatan.jaskl.ConfigProperty;
import com.github.almightysatan.jaskl.InvalidTypeException;
import com.github.almightysatan.jaskl.impl.ConfigEntryImpl;
import com.github.almightysatan.jaskl.impl.WritableConfigEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CustomConfigEntry<T> extends ConfigEntryImpl<T> {

    private final Class<T> type;
    private final Property<?>[] properties;
    private T value;

    private CustomConfigEntry(@NotNull Config config, @NotNull String path, @Nullable String description, @NotNull T defaultValue) {
        super(path, description, defaultValue);
        Objects.requireNonNull(config);
        this.type = (Class<T>) defaultValue.getClass();

        List<Property<?>> properties = new ArrayList<>();
        try {
            for (Field field : type.getDeclaredFields()) {
                ConfigProperty annotation = field.getAnnotation(ConfigProperty.class);
                if (annotation != null) {
                    field.setAccessible(true);
                    String propertyPath = path + "." + (annotation.value().isEmpty() ? field.getName() : annotation.value());
                    Object fieldDefaultValue = field.get(defaultValue);
                    properties.add(new Property<>(field, newEntry(config, propertyPath, null, fieldDefaultValue)));
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e); // TODO custom exception
        }
        this.properties = properties.toArray(new Property[0]);
        this.value = defaultValue;
    }

    @Override
    public @NotNull T getValue() {
        T value = this.value;
        if (value == null) {
            try {
                value = this.type.newInstance();
                for (Property<?> property : this.properties)
                    property.getField().set(value, property.getValue());
                this.value = value;
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e); // TODO custom exception
            }
        }
        return value;
    }

    @Override
    public void setValue(@NotNull T value) {
        Objects.requireNonNull(value);
        this.checkType(value);
        try {
            for (Property<?> property : this.properties)
                property.setValueAsObject(property.field.get(value));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e); // TODO custom exception
        }
        this.value = value;
    }

    private static WritableConfigEntry<?> newEntry(@NotNull Config config, @NotNull String path, @Nullable String description, @NotNull Object defaultValue) {
        Objects.requireNonNull(defaultValue, String.format("Default value for path %s is null", path));

        if (defaultValue instanceof String)
            return (WritableConfigEntry<?>) StringConfigEntry.of(config, path, description, (String) defaultValue);
        if (defaultValue instanceof Boolean)
            return (WritableConfigEntry<?>) BooleanConfigEntry.of(config, path, description, (Boolean) defaultValue);
        if (defaultValue instanceof Integer)
            return (WritableConfigEntry<?>) IntegerConfigEntry.of(config, path, description, (Integer) defaultValue);
        if (defaultValue instanceof Long)
            return (WritableConfigEntry<?>) LongConfigEntry.of(config, path, description, (Long) defaultValue);
        if (defaultValue instanceof Float)
            return (WritableConfigEntry<?>) FloatConfigEntry.of(config, path, description, (Float) defaultValue);
        if (defaultValue instanceof DoubleConfigEntry)
            return (WritableConfigEntry<?>) DoubleConfigEntry.of(config, path, description, (Double) defaultValue);
        if (defaultValue instanceof Enum<?>)
            return (WritableConfigEntry<?>) EnumConfigEntry.of(config, path, description, (Enum) defaultValue);

        throw new InvalidTypeException(path, defaultValue.getClass());
    }

    public static <T> ConfigEntry<T> of(@NotNull Config config, @NotNull String path, @Nullable String description, @NotNull T defaultValue) {
        return new CustomConfigEntry<>(config, path, description, defaultValue);
    }

    private class Property<U> implements WritableConfigEntry<U> {

        private final Field field;
        private final WritableConfigEntry<U> entry;

        Property(@NotNull Field field, @NotNull WritableConfigEntry<U> entry) {
            this.field = field;
            this.entry = entry;
        }

        @Override
        public @NotNull String getPath() {
            return this.entry.getPath();
        }

        @Override
        public @Nullable String getDescription() {
            return this.entry.getDescription();
        }

        @Override
        public @NotNull U getValue() {
            return this.entry.getValue();
        }

        @Override
        public @NotNull U getDefaultValue() {
            return this.entry.getDefaultValue();
        }

        @Override
        public void setValue(@NotNull U value) {
            this.entry.setValue(value);
        }

        @SuppressWarnings("unchecked")
        private void setValueAsObject(@NotNull Object value) {
            this.entry.setValue((U) value);
        }

        @Override
        public void putValue(@NotNull Object value) {
            this.entry.putValue(value);
            CustomConfigEntry.this.value = null;
        }

        @Override
        public boolean isModified() {
            return this.entry.isModified();
        }

        private @NotNull Field getField() {
            return field;
        }
    }
}
