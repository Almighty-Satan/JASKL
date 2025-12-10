/*
 * JASKL - Just Another Simple Konfig Library
 * Copyright (C) 2023 LeStegii, Almighty-Satan
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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.util.*;
import java.util.function.Function;

public class AnnotationManagerImpl implements AnnotationManager {

    public static final AnnotationManager INSTANCE = new AnnotationManagerImpl();

    private final Map<Class<?>, Function<Object, Validator<Object>>> validatorFunctions = new HashMap<>();
    private final Map<Class<?>, Validator<Object>> validators = new HashMap<>();
    private final Map<Class<?>, Type<?>> typeCache = new HashMap<>();

    public AnnotationManagerImpl() {
        this.registerDefaultAnnotations();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public <A, T> void addValidatorFunction(@NotNull Class<A> annotationClass, @NotNull Function<A, Validator<T>> validatorFunction) {
        Objects.requireNonNull(annotationClass);
        Objects.requireNonNull(validatorFunction);
        if (!annotationClass.isAnnotation())
            throw new IllegalArgumentException("Class is not an annotation");
        if (this.validatorFunctions.containsKey(annotationClass))
            throw new IllegalArgumentException("Class already registered");
        this.validatorFunctions.put(annotationClass, (Function) validatorFunction);
        this.typeCache.clear();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public <A, T> void addValidator(@NotNull Class<A> annotationClass, @NotNull Validator<T> validator) {
        this.addValidatorFunction(annotationClass, annotation -> validator);
        this.validators.put(annotationClass, (Validator) validator);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private <T> @NotNull Validator<T> getValidator(Class<?>... annotationClasses) {
        Validator[] validators = new Validator[annotationClasses.length];
        for (int i = 0; i < annotationClasses.length; i++) {
            Validator<?> validator = this.validators.get(annotationClasses[i]);
            if (validator == null)
                throw new InvalidAnnotationConfigException("Unknown validation annotation: " + annotationClasses[i].getName());
            validators[i] = validator;
        }
        return Validator.of(validators);
    }

    @Override
    public <T> @NotNull T registerEntries(@NotNull Config config, @NotNull Class<T> configClass) throws InvalidAnnotationConfigException, InvalidTypeException, ValidationException {
        Objects.requireNonNull(config);
        Objects.requireNonNull(configClass);
        try {
            T instance = configClass.newInstance();

            for (Property property : this.loadProperties(configClass, instance, true, Collections.emptySet())) {
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
        public void putValue(@NotNull Object value, @NotNull ExceptionHandler exceptionHandler) throws InvalidTypeException, ValidationException {
            super.putValue(value, exceptionHandler);
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
    private <T> @NotNull Type<T> createCustomObjectType(@NotNull Class<T> typeClass, @NotNull Set<Class<?>> parentCustomClasses) throws InvalidAnnotationConfigException {
        Objects.requireNonNull(typeClass);

        Type<T> cachedType = (Type<T>) this.typeCache.get(typeClass);
        if (cachedType != null)
            return cachedType;

        if (parentCustomClasses.contains(typeClass))
            throw new InvalidAnnotationConfigException("Circular type references are not allowed");

        parentCustomClasses = new HashSet<>(parentCustomClasses);
        parentCustomClasses.add(typeClass);

        try {
            Property[] properties = this.loadProperties(typeClass, typeClass.newInstance(), false, parentCustomClasses);

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
                            map.put(keyPreprocessor.apply(new EntryDescriptor(property.path, property.description)), property.type.toWritable(property.field.get(value), keyPreprocessor));
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

    private <T> @NotNull Property @NotNull [] loadProperties(@NotNull Class<T> clazz, @NotNull T instance, boolean loadDefaultValue, @NotNull Set<Class<?>> parentCustomClasses)
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
                Type<Object> type = (Type<Object>) (typeHintAnnotation != null ? this.resolveType(typeHintAnnotation.value(), parentCustomClasses) : this.resolveType(field, parentCustomClasses));
                if (type == null)
                    throw new InvalidAnnotationConfigException(String.format("Unknown type for field %s", field.getName()));
                
                Description descriptionAnnotation = field.getAnnotation(Description.class);
                String description = descriptionAnnotation != null ? descriptionAnnotation.value() : null;

                for (Annotation a : field.getAnnotations()) {
                    Function<Object, Validator<Object>> validatorFunction = this.validatorFunctions.get(a.annotationType());
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
    private @Nullable Type<?> resolveType(@Nullable Class<?> typeClass, @NotNull Set<Class<?>> parentCustomClasses) {
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
        if (typeClass == BigInteger.class)
            return Type.BIG_INTEGER;
        if (typeClass == BigDecimal.class)
            return Type.BIG_DECIMAL;
        if (typeClass == String.class)
            return Type.STRING;
        if (typeClass == UUID.class)
            return Type.UUID;
        if (typeClass == Instant.class)
            return Type.INSTANT;
        if (typeClass == OffsetDateTime.class)
            return Type.OFFSET_DATE_TIME;
        if (typeClass == ZonedDateTime.class)
            return Type.ZONED_DATE_TIME;
        if (typeClass == LocalDate.class)
            return Type.LOCAL_DATE;
        if (typeClass == LocalTime.class)
            return Type.LOCAL_TIME;
        if (typeClass == LocalDateTime.class)
            return Type.LOCAL_DATE_TIME;
        if (typeClass == Duration.class)
            return Type.DURATION;
        if (typeClass == Period.class)
            return Type.PERIOD;
        if (typeClass.isEnum())
            return Type.enumType((Class<? extends Enum>) typeClass);
        if (this.typeCache.containsKey(typeClass) | Arrays.stream(typeClass.getFields()).anyMatch(field -> field.getAnnotation(Entry.class) != null))
            return this.createCustomObjectType(typeClass, parentCustomClasses);
        return null;
    }

    private @Nullable Type<?> resolveType(@Nullable Iterator<Class<?>> typeClasses, @NotNull Set<Class<?>> parentCustomClasses) {
        if (typeClasses == null || !typeClasses.hasNext())
            return null;

        Class<?> typeClass = typeClasses.next();
        if (typeClass == null)
            return null;

        if (List.class.isAssignableFrom(typeClass)) {
            Type<?> type = this.resolveType(typeClasses, parentCustomClasses);
            if (type == null)
                return null;
            return Type.list(type);
        }
        if (Map.class.isAssignableFrom(typeClass)) {
            Type<?> keyType = this.resolveType(typeClasses, parentCustomClasses);
            Type<?> valueType = this.resolveType(typeClasses, parentCustomClasses);
            if (keyType == null)
                return null;
            if (valueType == null)
                return null;
            return Type.map(keyType, valueType);
        }

        return this.resolveType(typeClass, parentCustomClasses);
    }

    private @Nullable Type<?> resolveType(@Nullable Class<?>[] typeClasses, @NotNull Set<Class<?>> parentCustomClasses) {
        return this.resolveType(Arrays.asList(typeClasses).iterator(), parentCustomClasses);
    }

    private @Nullable Type<?> resolveType(@Nullable ParameterizedType generics, @NotNull Set<Class<?>> parentCustomClasses) {
        if (generics == null)
            return null;

        Class<?> typeClass = (Class<?>) generics.getRawType();

        if (List.class.isAssignableFrom(typeClass)) {
            java.lang.reflect.Type[] typeArgs = generics.getActualTypeArguments();
            if (typeArgs.length != 1)
                return null;
            java.lang.reflect.Type typeArg = typeArgs[0];
            Type<?> type = typeArg instanceof ParameterizedType ? this.resolveType((ParameterizedType) typeArg, parentCustomClasses) : this.resolveType((Class<?>) typeArg, parentCustomClasses);
            if (type == null)
                return null;
            return Type.list(type);
        }
        if (Map.class.isAssignableFrom(typeClass)) {
            java.lang.reflect.Type[] typeArgs = generics.getActualTypeArguments();
            if (typeArgs.length != 2)
                return null;
            java.lang.reflect.Type keyTypeArg = typeArgs[0];
            Type<?> keyType = keyTypeArg instanceof ParameterizedType ? this.resolveType((ParameterizedType) keyTypeArg, parentCustomClasses) : this.resolveType((Class<?>) keyTypeArg, parentCustomClasses);
            if (keyType == null)
                return null;
            java.lang.reflect.Type valueTypeArg = typeArgs[1];
            Type<?> valueType = valueTypeArg instanceof ParameterizedType ? this.resolveType((ParameterizedType) valueTypeArg, parentCustomClasses) : this.resolveType((Class<?>) valueTypeArg, parentCustomClasses);
            if (valueType == null)
                return null;
            return Type.map(keyType, valueType);
        }

        return this.resolveType(typeClass, parentCustomClasses);
    }

    private @Nullable Type<?> resolveType(@Nullable Field field, @NotNull Set<Class<?>> parentCustomClasses) {
        if (field == null)
            return null;
        if (field.getGenericType() instanceof ParameterizedType)
            return this.resolveType((ParameterizedType) field.getGenericType(), parentCustomClasses);
        return this.resolveType(field.getType(), parentCustomClasses);
    }

    private void registerDefaultAnnotations() {
        this.addValidator(Validate.DoubleNotZero.class, Validator.DOUBLE_NOT_ZERO);
        this.addValidator(Validate.DoubleNotPositive.class, Validator.DOUBLE_NOT_POSITIVE);
        this.addValidator(Validate.DoubleNotNegative.class, Validator.DOUBLE_NOT_NEGATIVE);
        this.addValidator(Validate.DoublePositive.class, Validator.DOUBLE_POSITIVE);
        this.addValidator(Validate.DoubleNegative.class, Validator.DOUBLE_NEGATIVE);
        this.addValidatorFunction(Validate.DoubleGreater.class, annotation -> Validator.doubleGreater(annotation.value()));
        this.addValidatorFunction(Validate.DoubleGreaterOrEqual.class, annotation -> Validator.doubleGreaterOrEqual(annotation.value()));
        this.addValidatorFunction(Validate.DoubleLess.class, annotation -> Validator.doubleLess(annotation.value()));
        this.addValidatorFunction(Validate.DoubleLessOrEqual.class, annotation -> Validator.doubleLessOrEqual(annotation.value()));
        this.addValidator(Validate.FloatNotZero.class, Validator.FLOAT_NOT_ZERO);
        this.addValidator(Validate.FloatNotPositive.class, Validator.FLOAT_NOT_POSITIVE);
        this.addValidator(Validate.FloatNotNegative.class, Validator.FLOAT_NOT_NEGATIVE);
        this.addValidator(Validate.FloatPositive.class, Validator.FLOAT_POSITIVE);
        this.addValidator(Validate.FloatNegative.class, Validator.FLOAT_NEGATIVE);
        this.addValidatorFunction(Validate.FloatGreater.class, annotation -> Validator.floatGreater(annotation.value()));
        this.addValidatorFunction(Validate.FloatGreaterOrEqual.class, annotation -> Validator.floatGreaterOrEqual(annotation.value()));
        this.addValidatorFunction(Validate.FloatLess.class, annotation -> Validator.floatLess(annotation.value()));
        this.addValidatorFunction(Validate.FloatLessOrEqual.class, annotation -> Validator.floatLessOrEqual(annotation.value()));
        this.addValidator(Validate.IntegerNotZero.class, Validator.INTEGER_NOT_ZERO);
        this.addValidator(Validate.IntegerNotPositive.class, Validator.INTEGER_NOT_POSITIVE);
        this.addValidator(Validate.IntegerNotNegative.class, Validator.INTEGER_NOT_NEGATIVE);
        this.addValidator(Validate.IntegerPositive.class, Validator.INTEGER_POSITIVE);
        this.addValidator(Validate.IntegerNegative.class, Validator.INTEGER_NEGATIVE);
        this.addValidatorFunction(Validate.IntegerGreater.class, annotation -> Validator.integerGreater(annotation.value()));
        this.addValidatorFunction(Validate.IntegerGreaterOrEqual.class, annotation -> Validator.integerGreaterOrEqual(annotation.value()));
        this.addValidatorFunction(Validate.IntegerLess.class, annotation -> Validator.integerLess(annotation.value()));
        this.addValidatorFunction(Validate.IntegerLessOrEqual.class, annotation -> Validator.integerLessOrEqual(annotation.value()));
        this.addValidator(Validate.LongNotZero.class, Validator.LONG_NOT_ZERO);
        this.addValidator(Validate.LongNotPositive.class, Validator.LONG_NOT_POSITIVE);
        this.addValidator(Validate.LongNotNegative.class, Validator.LONG_NOT_NEGATIVE);
        this.addValidator(Validate.LongPositive.class, Validator.LONG_POSITIVE);
        this.addValidator(Validate.LongNegative.class, Validator.LONG_NEGATIVE);
        this.addValidatorFunction(Validate.LongGreater.class, annotation -> Validator.longGreater(annotation.value()));
        this.addValidatorFunction(Validate.LongGreaterOrEqual.class, annotation -> Validator.longGreaterOrEqual(annotation.value()));
        this.addValidatorFunction(Validate.LongLess.class, annotation -> Validator.longLess(annotation.value()));
        this.addValidatorFunction(Validate.LongLessOrEqual.class, annotation -> Validator.longLessOrEqual(annotation.value()));
        this.addValidator(Validate.BigIntegerNotZero.class, Validator.BIG_INTEGER_NOT_ZERO);
        this.addValidator(Validate.BigIntegerNotPositive.class, Validator.BIG_INTEGER_NOT_POSITIVE);
        this.addValidator(Validate.BigIntegerNotNegative.class, Validator.BIG_INTEGER_NOT_NEGATIVE);
        this.addValidator(Validate.BigIntegerPositive.class, Validator.BIG_INTEGER_POSITIVE);
        this.addValidator(Validate.BigIntegerNegative.class, Validator.BIG_INTEGER_NEGATIVE);
        this.addValidatorFunction(Validate.BigIntegerGreater.class, annotation -> Validator.bigIntegerGreater(new BigInteger(annotation.value())));
        this.addValidatorFunction(Validate.BigIntegerGreaterOrEqual.class, annotation -> Validator.bigIntegerGreaterOrEqual(new BigInteger(annotation.value())));
        this.addValidatorFunction(Validate.BigIntegerLess.class, annotation -> Validator.bigIntegerLess(new BigInteger(annotation.value())));
        this.addValidatorFunction(Validate.BigIntegerLessOrEqual.class, annotation -> Validator.bigIntegerLessOrEqual(new BigInteger(annotation.value())));
        this.addValidator(Validate.BigDecimalNotZero.class, Validator.BIG_DECIMAL_NOT_ZERO);
        this.addValidator(Validate.BigDecimalNotPositive.class, Validator.BIG_DECIMAL_NOT_POSITIVE);
        this.addValidator(Validate.BigDecimalNotNegative.class, Validator.BIG_DECIMAL_NOT_NEGATIVE);
        this.addValidator(Validate.BigDecimalPositive.class, Validator.BIG_DECIMAL_POSITIVE);
        this.addValidator(Validate.BigDecimalNegative.class, Validator.BIG_DECIMAL_NEGATIVE);
        this.addValidatorFunction(Validate.BigDecimalGreater.class, annotation -> Validator.bigDecimalGreater(new BigDecimal(annotation.value())));
        this.addValidatorFunction(Validate.BigDecimalGreaterOrEqual.class, annotation -> Validator.bigDecimalGreaterOrEqual(new BigDecimal(annotation.value())));
        this.addValidatorFunction(Validate.BigDecimalLess.class, annotation -> Validator.bigDecimalLess(new BigDecimal(annotation.value())));
        this.addValidatorFunction(Validate.BigDecimalLessOrEqual.class, annotation -> Validator.bigDecimalLessOrEqual(new BigDecimal(annotation.value())));
        this.addValidator(Validate.StringNotEmpty.class, Validator.STRING_NOT_EMPTY);
        this.addValidator(Validate.StringAlphabetic.class, Validator.STRING_ALPHABETIC);
        this.addValidator(Validate.StringNumeric.class, Validator.STRING_NUMERIC);
        this.addValidator(Validate.StringAlphanumeric.class, Validator.STRING_ALPHANUMERIC);
        this.addValidatorFunction(Validate.StringMinLength.class, annotation -> Validator.stringMinLength(annotation.value()));
        this.addValidatorFunction(Validate.StringMaxLength.class, annotation -> Validator.stringMaxLength(annotation.value()));
        this.addValidator(Validate.ListNotEmpty.class, Validator.listNotEmpty());
        this.addValidatorFunction(Validate.ListMinSize.class, annotation -> Validator.listMinSize(annotation.value()));
        this.addValidatorFunction(Validate.ListMaxSize.class, annotation -> Validator.listMaxSize(annotation.value()));
        this.addValidatorFunction(Validate.ListForEach.class, annotation -> Validator.listForEach(getValidator(annotation.value())));
        this.addValidator(Validate.MapNotEmpty.class, Validator.mapNotEmpty());
        this.addValidatorFunction(Validate.MapMinSize.class, annotation -> Validator.mapMinSize(annotation.value()));
        this.addValidatorFunction(Validate.MapMaxSize.class, annotation -> Validator.mapMaxSize(annotation.value()));
        this.addValidatorFunction(Validate.MapForEachKey.class, annotation -> Validator.mapForEachKey(getValidator(annotation.value())));
        this.addValidatorFunction(Validate.MapForEachValue.class, annotation -> Validator.mapForEachValue(getValidator(annotation.value())));
    }
}
