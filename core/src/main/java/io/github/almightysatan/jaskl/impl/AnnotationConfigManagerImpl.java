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
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.function.Function;

public class AnnotationConfigManagerImpl implements AnnotationConfigManager {

    public static final AnnotationConfigManager INSTANCE = new AnnotationConfigManagerImpl();

    private final Map<Class<?>, Function<Object, Validator<Object>>> validators = new HashMap<>();
    private final Map<Class<?>, Type<?>> typeCache = new HashMap<>();

    public AnnotationConfigManagerImpl() {
        this.registerDefaultAnnotations();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public <T> void registerValidatorFunction(@NotNull Class<T> annotationClass, @NotNull Function<T, Validator<?>> validatorFunction) {
        Objects.requireNonNull(annotationClass);
        Objects.requireNonNull(validatorFunction);
        this.validators.put(annotationClass, (Function) validatorFunction);
    }

    @Override
    public <T> @NotNull T init(@NotNull Config config, @NotNull Class<T> configClass) throws InvalidAnnotationConfigException, InvalidTypeException, ValidationException {
        Objects.requireNonNull(config);
        Objects.requireNonNull(configClass);
        try {
            T instance = configClass.newInstance();

            for (Property property : this.loadProperties(configClass, instance, true, true, Collections.emptySet())) {
                WritableConfigEntry<?> entry = new WritableAnnotationConfigEntry<>(property.type, property.path, property.description, property.defaultValue, property.field, instance);
                entry.register(config);
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
            super.setValue(value);
            this.setField(super.getValue());
        }

        @Override
        public void putValue(@NotNull Object value) throws InvalidTypeException, ValidationException {
            super.putValue(value);
            this.setField(super.getValue());
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

    @Override
    public <T> @NotNull Type<T> createCustomObjectType(@NotNull Class<T> typeClass) throws InvalidAnnotationConfigException {
        return this.createCustomObjectType(typeClass, Collections.emptySet());
    }

    @SuppressWarnings("unchecked")
    public <T> @NotNull Type<T> createCustomObjectType(@NotNull Class<T> typeClass, @NotNull Set<Class<?>> parentCustomTypes) throws InvalidAnnotationConfigException {
        Objects.requireNonNull(typeClass);

        Type<T> cachedType = (Type<T>) this.typeCache.get(typeClass);
        if (cachedType != null)
            return cachedType;

        if (parentCustomTypes.contains(typeClass))
            throw new InvalidAnnotationConfigException("Circular type references are not allowed");

        parentCustomTypes = new HashSet<>(parentCustomTypes);
        parentCustomTypes.add(typeClass);

        try {
            Property[] properties = this.loadProperties(typeClass, typeClass.newInstance(), false, false, parentCustomTypes);

            if (properties.length == 0)
                throw new InvalidAnnotationConfigException("No annotated fields found");

            Type<T> type = new Type<T>() {
                @SuppressWarnings("unchecked")
                @Override
                public @NotNull T toEntryType(@NotNull Object value) throws InvalidTypeException, ValidationException {
                    if (typeClass.isAssignableFrom(value.getClass()))
                        return (T) value;
                    if (value instanceof Map) {
                        Map<String, ?> mapValue = (Map<String, ?>) value;

                        try {
                            T instance = typeClass.newInstance();
                            for (Property property : properties) {
                                Object propertyValue = mapValue.get(property.path);
                                if (propertyValue == null)
                                    throw new InvalidTypeException(property.path);
                                property.field.set(instance, property.type.toEntryType(propertyValue));
                            }
                            return instance;
                        } catch (InstantiationException | IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    throw new InvalidTypeException(Map.class, value.getClass());
                }

                @Override
                public @NotNull Object toWritable(@NotNull T value, @NotNull Function<@NotNull Object, @NotNull Object> keyPreprocessor) throws InvalidTypeException {
                    Map<Object, Object> map = new HashMap<>();
                    for (Property property : properties)
                        try {
                            map.put(keyPreprocessor.apply(property.path), property.type.toWritable(property.field.get(value), keyPreprocessor));
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }

                    return Collections.unmodifiableMap(map);
                }
            };
            this.typeCache.put(typeClass, type);
            return type;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new InvalidAnnotationConfigException(e);
        }
    }

    private <T> @NotNull Property[] loadProperties(@NotNull Class<T> clazz, @NotNull T instance, boolean loadDescription,
                                                   boolean loadDefaultValue, @NotNull Set<Class<?>> parentCustomTypes)
            throws IllegalAccessException, InvalidAnnotationConfigException {
        List<Property> properties = new ArrayList<>();

        for (Field field : clazz.getFields()) {
            if (Modifier.isFinal(field.getModifiers()))
                throw new InvalidAnnotationConfigException(String.format("Field %s is final", field.getName()));

            Entry annotation = field.getAnnotation(Entry.class);
            if (annotation != null) {
                String path = annotation.value().isEmpty() ? field.getName() : annotation.value();
                Object defaultValue = null;
                if (loadDefaultValue) {
                    defaultValue = field.get(instance);
                    if (defaultValue == null)
                        throw new InvalidAnnotationConfigException(String.format("Default value of field %s is null", field.getName()));
                }

                TypeHint typeHintAnnotation = field.getAnnotation(TypeHint.class);
                @SuppressWarnings("unchecked")
                Type<Object> type = (Type<Object>) (typeHintAnnotation != null ? this.resolveType(typeHintAnnotation.value(), parentCustomTypes) : this.resolveType(field, parentCustomTypes));
                if (type == null)
                    throw new InvalidAnnotationConfigException(String.format("Unknown type for field %s", field.getName()));

                String description = null;
                if (loadDescription) {
                    Description descriptionAnnotation = field.getAnnotation(Description.class);
                    description = descriptionAnnotation != null ? descriptionAnnotation.value() : null;
                }

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
                properties.add(new Property(field, path, description, type, defaultValue));
            }
        }

        return properties.toArray(new Property[0]);
    }

    private static class Property {

        private final Field field;
        private final String path;
        private final String description;
        private final Type<Object> type;
        private final Object defaultValue;

        private Property(Field field, String path, String description, Type<Object> type, Object defaultValue) {
            this.field = field;
            this.path = path;
            this.description = description;
            this.type = type;
            this.defaultValue = defaultValue;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private @Nullable Type<?> resolveType(@Nullable Class<?> typeClass, @NotNull Set<Class<?>> parentTypes) {
        if (typeClass == null)
            return null;
        if (typeClass == boolean.class || typeClass == Boolean.class)
            return Type.BOOLEAN;
        if (typeClass == double.class || typeClass == Double.class)
            return Type.DOUBLE;
        if (typeClass == float.class || typeClass == Float.class)
            return Type.FLOAT;
        if (typeClass == int.class || typeClass == Integer.class)
            return Type.INTEGER;
        if (typeClass == long.class || typeClass == Long.class)
            return Type.LONG;
        if (typeClass == String.class)
            return Type.STRING;
        if (typeClass.isEnum())
            return Type.enumType((Class<? extends Enum>) typeClass);
        if (this.typeCache.containsKey(typeClass) | Arrays.stream(typeClass.getFields()).anyMatch(field -> field.getAnnotation(Entry.class) != null))
            return this.createCustomObjectType(typeClass, parentTypes);
        return null;
    }

    private @Nullable Type<?> resolveType(@Nullable Iterator<Class<?>> typeClasses, @NotNull Set<Class<?>> parentCustomTypes) {
        if (typeClasses == null || !typeClasses.hasNext())
            return null;

        Class<?> typeClass = typeClasses.next();
        if (typeClass == null)
            return null;

        if (List.class.isAssignableFrom(typeClass)) {
            Type<?> type = this.resolveType(typeClasses, parentCustomTypes);
            if (type == null)
                return null;
            return Type.list(type);
        }
        if (Map.class.isAssignableFrom(typeClass)) {
            Type<?> keyType = this.resolveType(typeClasses, parentCustomTypes);
            Type<?> valueType = this.resolveType(typeClasses, parentCustomTypes);
            if (keyType == null)
                return null;
            if (valueType == null)
                return null;
            return Type.map(keyType, valueType);
        }

        return this.resolveType(typeClass, parentCustomTypes);
    }

    private @Nullable Type<?> resolveType(@Nullable Class<?>[] typeClasses, @NotNull Set<Class<?>> parentCustomTypes) {
        return this.resolveType(Arrays.asList(typeClasses).iterator(), parentCustomTypes);
    }

    private @Nullable Type<?> resolveType(@Nullable ParameterizedType generics, @NotNull Set<Class<?>> parentCustomTypes) {
        if (generics == null)
            return null;

        Class<?> typeClass = (Class<?>) generics.getRawType();

        if (List.class.isAssignableFrom(typeClass)) {
            java.lang.reflect.Type[] typeArgs = generics.getActualTypeArguments();
            if (typeArgs.length != 1)
                return null;
            java.lang.reflect.Type typeArg = typeArgs[0];
            Type<?> type = typeArg instanceof ParameterizedType ? this.resolveType((ParameterizedType) typeArg, parentCustomTypes) : this.resolveType((Class<?>) typeArg, parentCustomTypes);
            if (type == null)
                return null;
            return Type.list(type);
        }
        if (Map.class.isAssignableFrom(typeClass)) {
            java.lang.reflect.Type[] typeArgs = generics.getActualTypeArguments();
            if (typeArgs.length != 2)
                return null;
            java.lang.reflect.Type keyTypeArg = typeArgs[0];
            Type<?> keyType = keyTypeArg instanceof ParameterizedType ? this.resolveType((ParameterizedType) keyTypeArg, parentCustomTypes) : this.resolveType((Class<?>) keyTypeArg, parentCustomTypes);
            if (keyType == null)
                return null;
            java.lang.reflect.Type valueTypeArg = typeArgs[1];
            Type<?> valueType = valueTypeArg instanceof ParameterizedType ? this.resolveType((ParameterizedType) valueTypeArg, parentCustomTypes) : this.resolveType((Class<?>) valueTypeArg, parentCustomTypes);
            if (valueType == null)
                return null;
            return Type.map(keyType, valueType);
        }

        return this.resolveType(typeClass, parentCustomTypes);
    }

    private @Nullable Type<?> resolveType(@Nullable Field field, @NotNull Set<Class<?>> parentCustomTypes) {
        if (field == null)
            return null;
        if (field.getGenericType() instanceof ParameterizedType)
            return this.resolveType((ParameterizedType) field.getGenericType(), parentCustomTypes);
        return this.resolveType(field.getType(), parentCustomTypes);
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
