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

public interface ObjectMapper<T> {

    @NotNull T createInstance(@Unmodifiable @NotNull Map<@NotNull String, @NotNull Object> values) throws InvalidTypeException, ValidationException;

    @Unmodifiable @NotNull Map<@NotNull String, @NotNull Object> readValues(@NotNull T instance) throws InvalidTypeException;

    @NotNull Class<T> getObjectClass();

    @NotNull Property<?> @NotNull [] getProperties();

    interface Property<T> {

        @NotNull String getKey();

        @NotNull Type<T> getType();

        boolean isOptional();

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

        static <T> Property<T> of(@NotNull String key, @NotNull Type<T> type) {
            return of(key, type, false);
        }
    }
}
