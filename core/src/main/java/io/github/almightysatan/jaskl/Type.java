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

package io.github.almightysatan.jaskl;

import io.github.almightysatan.jaskl.annotation.InvalidAnnotationConfigException;
import io.github.almightysatan.jaskl.impl.AnnotationManagerImpl;
import io.github.almightysatan.jaskl.impl.SimpleType;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;

public interface Type<T> {

    @NotNull T toEntryType(@NotNull Object value) throws InvalidTypeException, ValidationException;

    @NotNull Object toWritable(@NotNull T value, @NotNull Function<@NotNull Object, @NotNull Object> keyPreprocessor) throws InvalidTypeException;

    static <T> @NotNull Type<T> validated(@NotNull Type<T> type, @NotNull Validator<? super T> validator) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(validator);
        return new Type<T>() {

            @Override
            public @NotNull T toEntryType(@NotNull Object value) throws InvalidTypeException, ValidationException {
                T casted = type.toEntryType(value);
                try {
                    validator.validate(casted);
                } catch (ValidationException e) {
                    throw e;
                } catch (Throwable t) {
                    throw new ValidationException("Exception while validating", t);
                }
                return casted;
            }

            @Override
            public @NotNull Object toWritable(@NotNull T value, @NotNull Function<@NotNull Object, @NotNull Object> keyPreprocessor) throws InvalidTypeException {
                return type.toWritable(value, keyPreprocessor);
            }
        };
    }

    static <T> @NotNull Type<T> validated(@NotNull Type<T> type, @NotNull Validator<? super T>[] validators) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(validators);
        return validated(type, Validator.of(validators));
    }

    Type<Boolean> BOOLEAN = (SimpleType<Boolean>) value -> {
        if (value instanceof Boolean)
            return (Boolean) value;

        if (value instanceof Integer) {
            return (Integer) value > 0;
        }

        if (value instanceof String) {
            try {
                return Boolean.valueOf((String) value);
            } catch (NumberFormatException ignored) {}
        }

        throw new InvalidTypeException(Boolean.class, value.getClass());
    };

    Type<Double> DOUBLE = (SimpleType<Double>) value -> {
        if (value instanceof Double)
            return (Double) value;

        if (value instanceof BigDecimal) {
            BigDecimal bigDecimal = (BigDecimal) value;
            if (bigDecimal.compareTo(BigDecimal.valueOf(Double.MIN_VALUE)) > 0 && bigDecimal.compareTo(BigDecimal.valueOf(Double.MAX_VALUE)) < 0)
                return bigDecimal.doubleValue();
        }

        if (value instanceof Integer) {
            return (double) (int) value;
        }

        if (value instanceof Long) {
            return (double) (long) value;
        }

        if (value instanceof String) {
            try {
                return Double.valueOf((String) value);
            } catch (NumberFormatException ignored) {}
        }

        throw new InvalidTypeException(Double.class, value.getClass());
    };

    Type<Float> FLOAT = (SimpleType<Float>) value -> {
        if (value instanceof Float)
            return (Float) value;

        if (value instanceof BigDecimal) {
            BigDecimal bigDecimal = (BigDecimal) value;
            if (bigDecimal.compareTo(BigDecimal.valueOf(Float.MIN_VALUE)) > 0 && bigDecimal.compareTo(BigDecimal.valueOf(Float.MAX_VALUE)) < 0)
                return bigDecimal.floatValue();
        }

        if (value instanceof Integer) {
            return (float) (int) value;
        }

        if (value instanceof Long) {
            return (float) (long) value;
        }

        if (value instanceof Double) {
            double doubleVal = (double) value;
            if (doubleVal > Float.MIN_VALUE && doubleVal < Float.MAX_VALUE)
                return (float) doubleVal;
        }

        if (value instanceof String) {
            try {
                return Float.valueOf((String) value);
            } catch (NumberFormatException ignored) {}
        }

        throw new InvalidTypeException(Float.class, value.getClass());
    };

    Type<Integer> INTEGER = (SimpleType<Integer>) value -> {
        if (value instanceof Integer)
            return (Integer) value;

        if (value instanceof Long) {
            long longVal = (Long) value;
            if (longVal > Integer.MIN_VALUE && longVal < Integer.MAX_VALUE)
                return (int) longVal;
        }

        if (value instanceof BigInteger) {
            BigInteger bigInt = (BigInteger) value;
            if (bigInt.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) > 0 && bigInt.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) < 0)
                return bigInt.intValue();
        }

        if (value instanceof String) {
            try {
                return Integer.valueOf((String) value);
            } catch (NumberFormatException ignored) {}
        }

        throw new InvalidTypeException(Integer.class, value.getClass());
    };

    Type<Long> LONG = (SimpleType<Long>) value -> {
        if (value instanceof Long)
            return (Long) value;

        if (value instanceof Integer)
            return ((Integer) value).longValue();

        if (value instanceof BigInteger) {
            BigInteger bigInt = (BigInteger) value;
            if (bigInt.compareTo(BigInteger.valueOf(Long.MIN_VALUE)) > 0 && bigInt.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) < 0)
                return bigInt.longValue();
        }

        if (value instanceof String) {
            try {
                return Long.valueOf((String) value);
            } catch (NumberFormatException ignored) {}
        }

        throw new InvalidTypeException(Long.class, value.getClass());
    };

    Type<String> STRING = (SimpleType<String>) value -> {
        if (value instanceof String)
            return (String) value;
        throw new InvalidTypeException(String.class, value.getClass());
    };

    Type<BigInteger> BIG_INTEGER = (SimpleType<BigInteger>) value -> {
        if (value instanceof BigInteger)
            return (BigInteger) value;

        if (value instanceof Integer)
            return BigInteger.valueOf((Integer) value);

        if (value instanceof Long)
            return BigInteger.valueOf((Long) value);

        if (value instanceof String) {
            try {
                return new BigInteger((String) value);
            } catch (NumberFormatException ignored) {}
        }

        throw new InvalidTypeException(BigInteger.class, value.getClass());
    };

    Type<BigDecimal> BIG_DECIMAL = (SimpleType<BigDecimal>) value -> {
        if (value instanceof BigDecimal)
            return (BigDecimal) value;

        if (value instanceof Double)
            return BigDecimal.valueOf((Double) value);

        if (value instanceof Integer)
            return BigDecimal.valueOf((Integer) value);

        if (value instanceof Long)
            return BigDecimal.valueOf((Long) value);

        if (value instanceof String) {
            try {
                return new BigDecimal((String) value);
            } catch (NumberFormatException ignored) {}
        }

        throw new InvalidTypeException(BigDecimal.class, value.getClass());
    };

    @SuppressWarnings("unchecked")
    static <T extends Enum<T>> @NotNull Type<T> enumType(@NotNull Class<T> clazz) {
        Objects.requireNonNull(clazz);
        return new Type<T>() {
            @Override
            public @NotNull T toEntryType(@NotNull Object value) throws InvalidTypeException, ValidationException {
                if (value.getClass() == clazz)
                    return (T) value;

                String stringValue = Type.STRING.toEntryType(value);
                try {
                    return Enum.valueOf(clazz, stringValue);
                } catch (IllegalArgumentException e) {
                    throw new InvalidTypeException(clazz, stringValue);
                }
            }

            @Override
            public @NotNull Object toWritable(@NotNull T value, @NotNull Function<@NotNull Object, @NotNull Object> keyPreprocessor) throws InvalidTypeException {
                return value.name();
            }
        };
    }

    @SuppressWarnings("unchecked")
    static <T> @NotNull Type<List<T>> list(@NotNull Type<T> type) {
        Objects.requireNonNull(type);
        return new Type<List<T>>() {
            @Override
            public @NotNull List<T> toEntryType(@NotNull Object value) throws InvalidTypeException, ValidationException {
                if (value instanceof List) {
                    List<T> listValue = (List<T>) value;
                    List<T> newList = new ArrayList<>(listValue.size());
                    for (Object element : listValue)
                        newList.add(type.toEntryType(element));

                    return Collections.unmodifiableList(newList);
                }

                throw new InvalidTypeException(List.class, value.getClass());
            }

            @Override
            public @NotNull Object toWritable(@NotNull List<T> value, @NotNull Function<@NotNull Object, @NotNull Object> keyPreprocessor) throws InvalidTypeException {
                List<Object> newList = new ArrayList<>(value.size());
                for (T element : value)
                    newList.add(type.toWritable(element, keyPreprocessor));

                return Collections.unmodifiableList(newList);
            }
        };
    }

    static <K, V> @NotNull Type<Map<K, V>> map(@NotNull Type<K> keyType, @NotNull Type<V> valueType) {
        Objects.requireNonNull(keyType);
        Objects.requireNonNull(valueType);
        return new Type<Map<K, V>>() {
            @Override
            public @NotNull Map<K, V> toEntryType(@NotNull Object value) throws InvalidTypeException, ValidationException {
                if (value instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<K, V> mapValue = (Map<K, V>) value;
                    Map<K, V> newMap = new HashMap<>();
                    for (Map.Entry<K, V> entry : mapValue.entrySet())
                        newMap.put(keyType.toEntryType(entry.getKey()), valueType.toEntryType(entry.getValue()));

                    return Collections.unmodifiableMap(newMap);
                }

                throw new InvalidTypeException(Map.class, value.getClass());
            }

            @Override
            public @NotNull Object toWritable(@NotNull Map<K, V> value, @NotNull Function<@NotNull Object, @NotNull Object> keyPreprocessor) throws InvalidTypeException {
                Map<Object, Object> newMap = new HashMap<>();
                for (Map.Entry<K, V> entry : value.entrySet())
                    newMap.put(keyPreprocessor.apply(keyType.toWritable(entry.getKey(), keyPreprocessor)), valueType.toWritable(entry.getValue(), keyPreprocessor));

                return Collections.unmodifiableMap(newMap);
            }
        };
    }

    static <T> @NotNull Type<T> custom(@NotNull Class<T> clazz) throws InvalidAnnotationConfigException {
        return AnnotationManagerImpl.INSTANCE.createCustomObjectType(clazz);
    }

    @SuppressWarnings("unchecked")
    static <T> @NotNull Type<T> custom(@NotNull ObjectMapper<T> objectMapper) {
        Objects.requireNonNull(objectMapper);
        Map<String, ObjectMapper.Property<?>> properties = new HashMap<>();
        for (ObjectMapper.Property<?> property : objectMapper.getProperties())
            if (properties.put(property.getKey(), ObjectMapper.Property.of(property.getKey(), property.getType(), property.isOptional())) != null)
                throw new IllegalArgumentException("Duplicate key: " + property.getKey());
        if (properties.isEmpty())
            throw new IllegalArgumentException("Properties should not be empty");
        return new Type<T>() {
            @Override
            public @NotNull T toEntryType(@NotNull Object value) throws InvalidTypeException, ValidationException {
                if (objectMapper.getObjectClass().isAssignableFrom(value.getClass()))
                    return (T) value;
                if (!(value instanceof Map))
                    throw new InvalidTypeException(Map.class, value.getClass());
                Map<?, ?> mapValue = (Map<String, ?>) value;
                Map<String, Object> newMap = new HashMap<>();
                for (ObjectMapper.Property<?> property : properties.values()) {
                    Object val = mapValue.get(property.getKey());
                    if (val == null) {
                        if (property.isOptional())
                            continue;
                        throw new InvalidTypeException(property.getKey());
                    }
                    newMap.put(STRING.toEntryType(property.getKey()), property.getType().toEntryType(val));
                }

                return objectMapper.createInstance(Collections.unmodifiableMap(newMap));
            }

            @Override
            public @NotNull Object toWritable(@NotNull T value, @NotNull Function<@NotNull Object, @NotNull Object> keyPreprocessor) throws InvalidTypeException {
                Map<Object, Object> newMap = new HashMap<>();
                Map<String, Object> values = objectMapper.readValues(value);
                for (ObjectMapper.Property<?> property : properties.values()) {
                    Object val = values.get(property.getKey());
                    if (val == null) {
                        if (property.isOptional())
                            continue;
                        throw new InvalidTypeException(property.getKey());
                    }
                    newMap.put(keyPreprocessor.apply(STRING.toWritable(property.getKey(), keyPreprocessor)), ((ObjectMapper.Property<Object>) property).getType().toWritable(val, keyPreprocessor));
                }

                return Collections.unmodifiableMap(newMap);
            }
        };
    }
}
