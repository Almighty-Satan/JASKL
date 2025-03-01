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
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;
import java.util.Objects;

/**
 * An {@link ObjectMapper} is used to create an object from a map of properties. This allows the user to save custom
 * objects to a config without using annotations.
 *
 * @param <T> the type of the object
 */
public interface ObjectMapper<T> {

    /**
     * Creates a new instance of the object from the given values.
     *
     * @param values A {@link Map} containing the values
     * @return a new instance of the object
     * @throws InvalidTypeException if a value does not match its expected {@link Type}
     * @throws ValidationException  if a value fails validation
     */
    @NotNull T createInstance(@Unmodifiable @NotNull Map<@NotNull String, @NotNull Object> values)
            throws InvalidTypeException, ValidationException;

    /**
     * Creates a {@link Map} containing the values from a given instance of the object.
     *
     * @param instance the object
     * @return {@link Map} containing the values
     * @throws InvalidTypeException if a value does not match its expected {@link Type}
     */
    @Unmodifiable
    @NotNull Map<@NotNull String, @NotNull Object> readValues(@NotNull T instance)
            throws InvalidTypeException;

    /**
     * Returns the class of the object.
     *
     * @return the class of the object
     */
    @NotNull Class<T> getObjectClass();

    /**
     * Returns an array containing all {@link Property Properties}.
     *
     * @return an array containing all {@link Property Properties}
     */
    @NotNull Property<?> @NotNull [] getProperties();

    /**
     * Represents a property (field) of an object
     *
     * @param <T> the type
     */
    interface Property<T> {

        /**
         * Returns the name of this {@link Property}.
         *
         * @return the name
         */
        @NotNull String getKey();

        /**
         * Returns the {@link Type} of this {@link Property}.
         *
         * @return the {@link Type} of this {@link Property}
         */
        @NotNull Type<T> getType();

        /**
         * Returns {@code true} if this {@link Property} may not always be present.
         *
         * @return {@code true} if this {@link Property} may not always be present
         */
        boolean isOptional();

        /**
         * Returns a new {@link Property}.
         *
         * @param key      the name
         * @param type     the {@link Type}
         * @param optional {@code true} if this {@link Property} may not always be present
         * @param <T>      the type
         * @return a new {@link Property}
         */
        static <T> Property<T> of(@NotNull String key, @NotNull Type<T> type, boolean optional) {
            Objects.requireNonNull(key);
            Objects.requireNonNull(type);
            if (key.isEmpty())
                throw new IllegalArgumentException("Empty key");
            return new Property<T>() {
                @Override
                public @NotNull String getKey() {
                    return key;
                }

                @Override
                public @NotNull Type<T> getType() {
                    return type;
                }

                @Override
                public boolean isOptional() {
                    return optional;
                }
            };
        }

        /**
         * Returns a new {@link Property}.
         *
         * @param key  the name
         * @param type the {@link Type}
         * @param <T>  the type
         * @return a new {@link Property}
         */
        static <T> Property<T> of(@NotNull String key, @NotNull Type<T> type) {
            return of(key, type, false);
        }
    }
}
