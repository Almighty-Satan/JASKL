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

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@FunctionalInterface
public interface Validator<T> {

    /**
     * Validates a config value and throws an exception if invalid.
     *
     * @param value The value
     * @throws ValidationException if the value is invalid
     */
    void validate(@NotNull T value) throws ValidationException;

    static <T> @NotNull Validator<T> nop() {
        return value -> {};
    }

    @SafeVarargs
    static <T> @NotNull Validator<T> of(@NotNull Validator<T>... validators) {
        Objects.requireNonNull(validators);
        if (validators.length == 0)
            return nop();
        return value -> {
            for (Validator<T> validator : validators)
                validator.validate(value);
        };
    }

    Validator<Double> DOUBLE_NOT_ZERO = value -> { if (value == 0) throw new ValidationException("should not be 0"); };

    Validator<Double> DOUBLE_NOT_POSITIVE = value -> { if (value > 0) throw new ValidationException("should not be positive"); };

    Validator<Double> DOUBLE_NOT_NEGATIVE = value -> { if (value < 0) throw new ValidationException("should not be negative"); };

    Validator<Double> DOUBLE_POSITIVE = value -> { if (value <= 0) throw new ValidationException("should be positive"); };

    Validator<Double> DOUBLE_NEGATIVE = value -> { if (value >= 0) throw new ValidationException("should be negative"); };

    static @NotNull Validator<Double> doubleGreater(double other) { return value -> { if (value > other) throw new ValidationException("should be greater than " + other); }; }

    static @NotNull Validator<Double> doubleGreaterOrEqual(double other) { return value -> { if (value >= other) throw new ValidationException("should be greater than or equal to " + other); }; }

    static @NotNull Validator<Double> doubleLess(double other) { return value -> { if (value < other) throw new ValidationException("should be less than " + other); }; }

    static @NotNull Validator<Double> doubleLessOrEqual(double other) { return value -> { if (value <= other) throw new ValidationException("should be less than or equal to " + other); }; }

    Validator<Float> FLOAT_NOT_ZERO = value -> { if (value == 0) throw new ValidationException("should not be 0"); };

    Validator<Float> FLOAT_NOT_POSITIVE = value -> { if (value > 0) throw new ValidationException("should not be positive"); };

    Validator<Float> FLOAT_NOT_NEGATIVE = value -> { if (value < 0) throw new ValidationException("should not be negative"); };

    Validator<Float> FLOAT_POSITIVE = value -> { if (value <= 0) throw new ValidationException("should be positive"); };

    Validator<Float> FLOAT_NEGATIVE = value -> { if (value >= 0) throw new ValidationException("should be negative"); };

    static @NotNull Validator<Float> floatGreater(float other) { return value -> { if (value > other) throw new ValidationException("should be greater than " + other); }; }

    static @NotNull Validator<Float> floatGreaterOrEqual(float other) { return value -> { if (value >= other) throw new ValidationException("should be greater than or equal to " + other); }; }

    static @NotNull Validator<Float> floatLess(float other) { return value -> { if (value < other) throw new ValidationException("should be less than " + other); }; }

    static @NotNull Validator<Float> floatLessOrEqual(float other) { return value -> { if (value <= other) throw new ValidationException("should be less than or equal to " + other); }; }

    Validator<Integer> INTEGER_NOT_ZERO = value -> { if (value == 0) throw new ValidationException("should not be 0"); };

    Validator<Integer> INTEGER_NOT_POSITIVE = value -> { if (value > 0) throw new ValidationException("should not be positive"); };

    Validator<Integer> INTEGER_NOT_NEGATIVE = value -> { if (value < 0) throw new ValidationException("should not be negative"); };

    Validator<Integer> INTEGER_POSITIVE = value -> { if (value <= 0) throw new ValidationException("should be positive"); };

    Validator<Integer> INTEGER_NEGATIVE = value -> { if (value >= 0) throw new ValidationException("should be negative"); };

    static @NotNull Validator<Integer> integerGreater(int other) { return value -> { if (value > other) throw new ValidationException("should be greater than " + other); }; }

    static @NotNull Validator<Integer> integerGreaterOrEqual(int other) { return value -> { if (value >= other) throw new ValidationException("should be greater than or equal to " + other); }; }

    static @NotNull Validator<Integer> integerLess(int other) { return value -> { if (value < other) throw new ValidationException("should be less than " + other); }; }

    static @NotNull Validator<Integer> integerLessOrEqual(int other) { return value -> { if (value <= other) throw new ValidationException("should be less than or equal to " + other); }; }

    Validator<Long> LONG_NOT_ZERO = value -> { if (value == 0) throw new ValidationException("should not be 0"); };

    Validator<Long> LONG_NOT_POSITIVE = value -> { if (value > 0) throw new ValidationException("should not be positive"); };

    Validator<Long> LONG_NOT_NEGATIVE = value -> { if (value < 0) throw new ValidationException("should not be negative"); };

    Validator<Long> LONG_POSITIVE = value -> { if (value <= 0) throw new ValidationException("should be positive"); };

    Validator<Long> LONG_NEGATIVE = value -> { if (value >= 0) throw new ValidationException("should be negative"); };

    static @NotNull Validator<Long> longGreater(long other) { return value -> { if (value > other) throw new ValidationException("should be greater than " + other); }; }

    static @NotNull Validator<Long> longGreaterOrEqual(long other) { return value -> { if (value >= other) throw new ValidationException("should be greater than or equal to " + other); }; }

    static @NotNull Validator<Long> longLess(long other) { return value -> { if (value < other) throw new ValidationException("should be less than " + other); }; }

    static @NotNull Validator<Long> longLessOrEqual(long other) { return value -> { if (value <= other) throw new ValidationException("should be less than or equal to " + other); }; }

    Validator<BigInteger> BIG_INTEGER_NOT_ZERO = value -> { if (value.signum() == 0) throw new ValidationException("should not be 0"); };

    Validator<BigInteger> BIG_INTEGER_NOT_POSITIVE = value -> { if (value.signum() == 1) throw new ValidationException("should not be positive"); };

    Validator<BigInteger> BIG_INTEGER_NOT_NEGATIVE = value -> { if (value.signum() == -1) throw new ValidationException("should not be negative"); };

    Validator<BigInteger> BIG_INTEGER_POSITIVE = value -> { if (value.signum() != 1) throw new ValidationException("should be positive"); };

    Validator<BigInteger> BIG_INTEGER_NEGATIVE = value -> { if (value.signum() != -1) throw new ValidationException("should be negative"); };

    static @NotNull Validator<BigInteger> bigIntegerGreater(BigInteger other) { return value -> { if (value.compareTo(other) < 1) throw new ValidationException("should be greater than " + other); }; }

    static @NotNull Validator<BigInteger> bigIntegerGreaterOrEqual(BigInteger other) { return value -> { if (value.compareTo(other) < 0) throw new ValidationException("should be greater than or equal to " + other); }; }

    static @NotNull Validator<BigInteger> bigIntegerLess(BigInteger other) { return value -> { if (value.compareTo(other) > -1) throw new ValidationException("should be less than " + other); }; }

    static @NotNull Validator<BigInteger> bigIntegerLessOrEqual(BigInteger other) { return value -> { if (value.compareTo(other) > 0) throw new ValidationException("should be less than or equal to " + other); }; }

    Validator<BigDecimal> BIG_DECIMAL_NOT_ZERO = value -> { if (value.signum() == 0) throw new ValidationException("should not be 0"); };

    Validator<BigDecimal> BIG_DECIMAL_NOT_POSITIVE = value -> { if (value.signum() == 1) throw new ValidationException("should not be positive"); };

    Validator<BigDecimal> BIG_DECIMAL_NOT_NEGATIVE = value -> { if (value.signum() == -1) throw new ValidationException("should not be negative"); };

    Validator<BigDecimal> BIG_DECIMAL_POSITIVE = value -> { if (value.signum() != 1) throw new ValidationException("should be positive"); };

    Validator<BigDecimal> BIG_DECIMAL_NEGATIVE = value -> { if (value.signum() != -1) throw new ValidationException("should be negative"); };

    static @NotNull Validator<BigDecimal> bigDecimalGreater(BigDecimal other) { return value -> { if (value.compareTo(other) < 1) throw new ValidationException("should be greater than " + other); }; }

    static @NotNull Validator<BigDecimal> bigDecimalGreaterOrEqual(BigDecimal other) { return value -> { if (value.compareTo(other) < 0) throw new ValidationException("should be greater than or equal to " + other); }; }

    static @NotNull Validator<BigDecimal> bigDecimalLess(BigDecimal other) { return value -> { if (value.compareTo(other) > -1) throw new ValidationException("should be less than " + other); }; }

    static @NotNull Validator<BigDecimal> bigDecimalLessOrEqual(BigDecimal other) { return value -> { if (value.compareTo(other) > 0) throw new ValidationException("should be less than or equal to " + other); }; }

    Validator<String> STRING_NOT_EMPTY = value -> { if (value.isEmpty()) throw new ValidationException("should not be empty"); };

    Validator<String> STRING_ALPHABETIC = value -> {
        for (char c : value.toCharArray())
            if ((c < 0x41 || c > 0x5A) && (c < 0x61 || c > 0x7A))
                throw new ValidationException("should only contain alphabetic characters");
    };

    Validator<String> STRING_NUMERIC = value -> {
        for (char c : value.toCharArray())
            if (c < 0x30 || c > 0x39)
                throw new ValidationException("should only contain numbers");
    };

    Validator<String> STRING_ALPHANUMERIC = value -> {
        for (char c : value.toCharArray())
            if ((c < 0x41 || c > 0x5A) && (c < 0x61 || c > 0x7A) && (c < 0x30 || c > 0x39))
                throw new ValidationException("should only contain alphanumeric characters");
    };

    static @NotNull Validator<String> stringMinLength(int size) { return value -> { if (value.length() < size) throw new ValidationException("should be at least " + size + " characters long"); };}

    static @NotNull Validator<String> stringMaxLength(int size) { return value -> { if (value.length() > size) throw new ValidationException("should not be longer than " + size + " characters"); };}

    static <T> @NotNull Validator<List<T>> listNotEmpty() { return value -> { if (value.isEmpty()) throw new ValidationException("should not be empty"); };}

    static <T> @NotNull Validator<List<T>> listMinSize(int size) { return value -> { if (value.size() < size) throw new ValidationException("should have at least " + size + " entries"); };}

    static <T> @NotNull Validator<List<T>> listMaxSize(int size) { return value -> { if (value.size() > size) throw new ValidationException("should not have more than " + size + " entries"); };}

    static <T> @NotNull Validator<List<T>> listForEach(Validator<T> validator) { return value -> value.forEach(validator::validate);}

    static <K, V> @NotNull Validator<Map<K, V>> mapNotEmpty() { return value -> { if (value.isEmpty()) throw new ValidationException("should not be empty"); };}

    static <K, V> @NotNull Validator<Map<K, V>> mapMinSize(int size) { return value -> { if (value.size() < size) throw new ValidationException("should have at least " + size + " entries"); };}

    static <K, V> @NotNull Validator<Map<K, V>> mapMaxSize(int size) { return value -> { if (value.size() > size) throw new ValidationException("should not have more than " + size + " entries"); };}

    static <K, V> @NotNull Validator<Map<K, V>> mapForEachKey(Validator<K> validator) { return value -> value.keySet().forEach(validator::validate);}

    static <K, V> @NotNull Validator<Map<K, V>> mapForEachValue(Validator<V> validator) { return value -> value.values().forEach(validator::validate);}
}
