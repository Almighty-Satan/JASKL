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

package com.github.almightysatan.jaskl;

import com.github.almightysatan.jaskl.impl.SimpleType;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public interface Type<T> {

    @NotNull T castToType(@NotNull Object value) throws InvalidTypeException;

    @NotNull Object castToWritable(@NotNull T value) throws InvalidTypeException;

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

    @SuppressWarnings("unchecked")
    static <T extends Enum<T>> @NotNull Type<T> enumType(@NotNull Class<T> clazz) {
        Objects.requireNonNull(clazz);
        return new Type<T>() {
            @Override
            public @NotNull T castToType(@NotNull Object value) throws InvalidTypeException {
                if (value.getClass() == clazz)
                    return (T) value;

                String stringValue = Type.STRING.castToType(value);
                try {
                    return Enum.valueOf(clazz, stringValue);
                } catch (IllegalArgumentException e) {
                    throw new InvalidTypeException(clazz, stringValue);
                }
            }

            @Override
            public @NotNull Object castToWritable(@NotNull T value) throws InvalidTypeException {
                return value.name();
            }
        };
    }

    @SuppressWarnings("unchecked")
    static <T> @NotNull Type<List<T>> list(@NotNull Type<T> type) {
        Objects.requireNonNull(type);
        return new Type<List<T>>() {
            @Override
            public @NotNull List<T> castToType(@NotNull Object value) throws InvalidTypeException {
                if (value instanceof List) {
                    List<T> listValue = (List<T>) value;
                    List<T> newList = new ArrayList<>(listValue.size());
                    for (Object element : listValue)
                        newList.add(type.castToType(element));

                    return Collections.unmodifiableList(newList);
                }

                throw new InvalidTypeException(List.class, value.getClass());
            }

            @Override
            public @NotNull Object castToWritable(@NotNull List<T> value) throws InvalidTypeException {
                List<Object> newList = new ArrayList<>(value.size());
                for (T element : value)
                    newList.add(type.castToWritable(element));

                return Collections.unmodifiableList(newList);
            }
        };
    }
}
