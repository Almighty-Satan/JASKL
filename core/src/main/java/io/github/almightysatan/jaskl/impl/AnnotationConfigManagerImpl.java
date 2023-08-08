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

package io.github.almightysatan.jaskl.impl;

import io.github.almightysatan.jaskl.*;
import io.github.almightysatan.jaskl.annotation.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class AnnotationConfigManagerImpl implements AnnotationConfigManager {

    private final Map<Class<?>, Function<Object, Validator<Object>>> validators = new HashMap<>();
    
    public AnnotationConfigManagerImpl() {
        this.registerDefaultAnnotations();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> void registerValidatorFunction(@NotNull Class<T> annotationClass, @NotNull Function<T, Validator<?>> validatorFunction) {
        Objects.requireNonNull(annotationClass);
        Objects.requireNonNull(validatorFunction);
        this.validators.put(annotationClass, (Function) validatorFunction);
    }

    public <T> @NotNull T init(@NotNull Config config, @NotNull Class<T> configClass) throws InvalidAnnotationConfigException, InvalidTypeException, ValidationException {
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
                    Type<Object> type = (Type<Object>) (annotation.type().length == 0 ? Type.of(field.getType()) : Type.of(annotation.type()));
                    if (type == null)
                        throw new InvalidAnnotationConfigException(String.format("Unknown type for field %s", field.getName()));

                    Description descriptionAnnotation = field.getAnnotation(Description.class);
                    String description = descriptionAnnotation != null ? descriptionAnnotation.value() : null;

                    for (Annotation a : field.getAnnotations()) {
                        Function<Object, Validator<Object>> validatorFunction = this.validators.get(a.annotationType());
                        if (validatorFunction != null) {
                            Object annotationInstance = field.getAnnotation(a.annotationType());
                            try {
                                type = Type.validated(type, validatorFunction.apply(annotationInstance));
                            } catch (Throwable t) {
                                throw new InvalidAnnotationConfigException(t);
                            }
                        }
                    }

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

        public WritableAnnotationConfigEntry(@NotNull Type<T> type, @NotNull String path, @Nullable String description, @NotNull T defaultValue,
                                             @NotNull Field field, @NotNull Object instance) throws InvalidTypeException, ValidationException {
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

        @Override
        public boolean isModified() {
            return this.checkField() || super.isModified();
        }

        private boolean checkField() {
            try {
                @SuppressWarnings("unchecked")
                T fieldValue = (T) this.field.get(this.instance);
                if (this.prevFieldValue != fieldValue) {
                    this.setValue(fieldValue);
                    return true;
                }
                return false;
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

    private void registerDefaultAnnotations() {
        this.registerValidator(Validate.DoubleNotZero.class, Validator.DOUBLE_NOT_ZERO);
        this.registerValidator(Validate.DoubleNotPositive.class, Validator.DOUBLE_NOT_POSITIVE);
        this.registerValidator(Validate.DoubleNotNegative.class, Validator.DOUBLE_NOT_NEGATIVE);
        this.registerValidator(Validate.DoublePositive.class, Validator.DOUBLE_POSITIVE);
        this.registerValidator(Validate.DoubleNegative.class, Validator.DOUBLE_NEGATIVE);
        this.registerValidator(Validate.FloatNotZero.class, Validator.FLOAT_NOT_ZERO);
        this.registerValidator(Validate.FloatNotPositive.class, Validator.FLOAT_NOT_POSITIVE);
        this.registerValidator(Validate.FloatNotNegative.class, Validator.FLOAT_NOT_NEGATIVE);
        this.registerValidator(Validate.FloatPositive.class, Validator.FLOAT_POSITIVE);
        this.registerValidator(Validate.FloatNegative.class, Validator.FLOAT_NEGATIVE);
        this.registerValidator(Validate.IntegerNotZero.class, Validator.INTEGER_NOT_ZERO);
        this.registerValidator(Validate.IntegerNotPositive.class, Validator.INTEGER_NOT_POSITIVE);
        this.registerValidator(Validate.IntegerNotNegative.class, Validator.INTEGER_NOT_NEGATIVE);
        this.registerValidator(Validate.IntegerPositive.class, Validator.INTEGER_POSITIVE);
        this.registerValidator(Validate.IntegerNegative.class, Validator.INTEGER_NEGATIVE);
        this.registerValidator(Validate.LongNotZero.class, Validator.LONG_NOT_ZERO);
        this.registerValidator(Validate.LongNotPositive.class, Validator.LONG_NOT_POSITIVE);
        this.registerValidator(Validate.LongNotNegative.class, Validator.LONG_NOT_NEGATIVE);
        this.registerValidator(Validate.LongPositive.class, Validator.LONG_POSITIVE);
        this.registerValidator(Validate.LongNegative.class, Validator.LONG_NEGATIVE);
        this.registerValidator(Validate.StringNotEmpty.class, Validator.STRING_NOT_EMPTY);
        this.registerValidatorFunction(Validate.StringMinLength.class, annotation -> Validator.stringMinLength(annotation.value()));
        this.registerValidatorFunction(Validate.StringMaxLength.class, annotation -> Validator.stringMaxLength(annotation.value()));
        this.registerValidator(Validate.ListNotEmpty.class, Validator.listNotEmpty());
        this.registerValidatorFunction(Validate.ListMinSize.class, annotation -> Validator.listMinSize(annotation.value()));
        this.registerValidatorFunction(Validate.ListMaxSize.class, annotation -> Validator.listMaxSize(annotation.value()));
        this.registerValidator(Validate.MapNotEmpty.class, Validator.mapNotEmpty());
        this.registerValidatorFunction(Validate.MapMinSize.class, annotation -> Validator.mapMinSize(annotation.value()));
        this.registerValidatorFunction(Validate.MapMaxSize.class, annotation -> Validator.mapMaxSize(annotation.value()));
    }
}
