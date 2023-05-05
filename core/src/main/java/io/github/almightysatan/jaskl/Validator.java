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

package io.github.almightysatan.jaskl;

import org.jetbrains.annotations.NotNull;

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

    static <T> Validator<T> nop() {
        return value -> {};
    }

    static <T> Validator<T> of(@NotNull Validator<T>... validators) {
        Objects.requireNonNull(validators);
        if (validators.length == 0)
            return nop();
        return value -> {
            for (Validator<T> validator : validators)
                validator.validate(value);
        };
    }

    Validator<Integer> INTEGER_NOT_ZERO = value -> { if (value == 0) throw new ValidationException("should not be 0"); };

    Validator<Integer> INTEGER_NOT_POSITIVE = value -> { if (value > 0) throw new ValidationException("should not be positive"); };

    Validator<Integer> INTEGER_NOT_NEGATIVE = value -> { if (value < 0) throw new ValidationException("should not be negative"); };

    Validator<Integer> INTEGER_POSITIVE = value -> { if (value <= 0) throw new ValidationException("should be positive"); };

    Validator<Integer> INTEGER_NEGATIVE = value -> { if (value >= 0) throw new ValidationException("should be negative"); };

    Validator<String> STRING_NOT_EMPTY = value -> { if (value.isEmpty()) throw new ValidationException("should not be empty"); };

    static Validator<String> stringMinLength(int size) { return value -> { if (value.length() < size) throw new ValidationException("should be at least " + size + " characters long"); };}

    static Validator<String> stringMaxLength(int size) { return value -> { if (value.length() > size) throw new ValidationException("should not be longer than " + size + " characters"); };}

    // TODO add more validators
}
