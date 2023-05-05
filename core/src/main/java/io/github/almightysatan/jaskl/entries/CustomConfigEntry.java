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

package io.github.almightysatan.jaskl.entries;

import io.github.almightysatan.jaskl.*;
import io.github.almightysatan.jaskl.impl.ConfigEntryImpl;
import io.github.almightysatan.jaskl.impl.WritableConfigEntry;
import io.github.almightysatan.jaskl.impl.WritableConfigEntryImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public interface CustomConfigEntry<T> extends ConfigEntry<T> {

    static <T> ConfigEntry<T> of(@NotNull Config config, @NotNull String path, @Nullable String description, @NotNull T defaultValue) {
        class CustomConfigEntryImpl extends ConfigEntryImpl<T> {

            private final Class<T> type;
            private final Property<?>[] properties;
            private T value;

            @SuppressWarnings("unchecked")
            private CustomConfigEntryImpl() {
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
                            Property<?> property = new Property<>(field, newEntry(propertyPath, null, fieldDefaultValue));
                            property.register(config);
                            properties.add(property);
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
                if (this.getDefaultValue().getClass() != value)
                    throw new InvalidTypeException(this.getDefaultValue().getClass(), value.getClass());
                try {
                    for (Property<?> property : this.properties)
                        property.setValueAsObject(property.field.get(value));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e); // TODO custom exception
                }
                this.value = value;
            }

            @SuppressWarnings({"unchecked", "rawtypes"})
            private WritableConfigEntry<?> newEntry(@NotNull String path, @Nullable String description, @NotNull Object defaultValue) {
                Objects.requireNonNull(defaultValue, String.format("Default value for path %s is null", path));

                if (defaultValue instanceof String)
                    return new WritableConfigEntryImpl<>(Type.STRING, path, description, (String) defaultValue);
                if (defaultValue instanceof Boolean)
                    return new WritableConfigEntryImpl<>(Type.BOOLEAN, path, description, (boolean) defaultValue);
                if (defaultValue instanceof Integer)
                    return new WritableConfigEntryImpl<>(Type.INTEGER, path, description, (int) defaultValue);
                if (defaultValue instanceof Long)
                    return new WritableConfigEntryImpl<>(Type.LONG, path, description, (long) defaultValue);
                if (defaultValue instanceof Float)
                    return new WritableConfigEntryImpl<>(Type.FLOAT, path, description, (float) defaultValue);
                if (defaultValue instanceof Double)
                    return new WritableConfigEntryImpl<>(Type.DOUBLE, path, description, (double) defaultValue);
                if (defaultValue instanceof Enum<?>)
                    return new WritableConfigEntryImpl<>(Type.enumType((Class) defaultValue.getClass()), path, description, defaultValue);

                throw new InvalidTypeException(path, defaultValue.getClass());
            }

            class Property<U> implements WritableConfigEntry<U> {

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
                public Type<U> getType() {
                    return this.entry.getType();
                }

                @Override
                public void putValue(@NotNull Object value) {
                    this.entry.putValue(value);
                    CustomConfigEntryImpl.this.value = null;
                }

                @Override
                public @NotNull Object getValueToWrite() {
                    return this.entry.getValueToWrite();
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

        return new CustomConfigEntryImpl();
    }
}
