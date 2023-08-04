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

package io.github.almightysatan.jaskl.annotation;

import io.github.almightysatan.jaskl.Config;
import io.github.almightysatan.jaskl.InvalidTypeException;
import io.github.almightysatan.jaskl.Type;
import io.github.almightysatan.jaskl.ValidationException;
import io.github.almightysatan.jaskl.impl.WritableConfigEntry;
import io.github.almightysatan.jaskl.impl.WritableConfigEntryImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.function.Function;

public class AnnotationConfigManager {

    public static <T> T register(Config config, Class<T> configClass) throws InvalidAnnotationConfigException {
        Objects.requireNonNull(config);
        Objects.requireNonNull(configClass);
        try {
            T instance = configClass.newInstance();

            for (Field field : configClass.getFields()) {
                if (Modifier.isFinal(field.getModifiers()))
                    throw new InvalidAnnotationConfigException(String.format("Field %s is final", field.getName()));

                Entry annotation = field.getAnnotation(Entry.class);
                if (annotation != null) {
                    String path = annotation.value().isEmpty() ? field.getName() : annotation.value();
                    Object defaultValue = field.get(instance);
                    if (defaultValue == null)
                        throw new InvalidAnnotationConfigException(String.format("Default value of field %s is null", field.getName()));

                    @SuppressWarnings("unchecked")
                    Type<Object> type = (Type<Object>) Type.of(field.getType());
                    if (type == null)
                        throw new InvalidAnnotationConfigException(String.format("Unknown type for field %s", field.getName()));

                    Description descriptionAnnotation = field.getAnnotation(Description.class);
                    String description = descriptionAnnotation != null ? descriptionAnnotation.value() : null;

                    WritableConfigEntry<?> entry = new WritableAnnotationConfigEntry<>(type, path, description, defaultValue, field, instance);
                    entry.register(config);
                }
            }

            return instance;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new InvalidAnnotationConfigException(e);
        }
    }

    private static class WritableAnnotationConfigEntry<T> extends WritableConfigEntryImpl<T> {

        private final Field field;
        private final Object instance;
        private Object prevFieldValue;

        public WritableAnnotationConfigEntry(@NotNull Type<T> type, @NotNull String path, @Nullable String description, @NotNull T defaultValue, @NotNull Field field, @NotNull Object instance) {
            super(type, path, description, defaultValue);
            this.field = field;
            this.instance = instance;
            this.prevFieldValue = defaultValue;
        }

        @Override
        public @NotNull T getValue() {
            this.checkField();
            return super.getValue();
        }

        @Override
        public @NotNull Object getValueToWrite(@NotNull Function<@NotNull Object, @NotNull Object> keyPreprocessor) throws InvalidTypeException, ValidationException {
            this.checkField();
            return super.getValueToWrite(keyPreprocessor);
        }

        @Override
        public void setValue(@NotNull T value) throws InvalidTypeException, ValidationException {
            this.setField(value);
            super.setValue(value);
        }

        @Override
        public void putValue(@NotNull Object value) throws InvalidTypeException, ValidationException {
            this.setField(value);
            super.putValue(value);
        }

        private void checkField() {
            try {
                @SuppressWarnings("unchecked")
                T fieldValue = (T) this.field.get(this.instance);
                if (this.prevFieldValue != fieldValue)
                    this.setValue(fieldValue);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        private void setField(@NotNull Object value) {
            try {
                this.field.set(this.instance, value);
                this.prevFieldValue = value;
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
