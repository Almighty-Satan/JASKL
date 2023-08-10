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

import io.github.almightysatan.jaskl.impl.WritableConfigEntryImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ConfigEntry<T> {

    /**
     * Returns the path leading to this ConfigEntry's value.
     *
     * @return the path of this ConfigEntry
     */
    @NotNull String getPath();

    /**
     * Returns the description of this ConfigEntry.
     *
     * @return the description of this ConfigEntry
     */
    @Nullable String getDescription();

    /**
     * Returns the value of this ConfigEntry.
     *
     * @return the value of this ConfigEntry
     * @throws InvalidTypeException if the value does not match its expected {@link Type}
     * @throws ValidationException  if the value fails validation
     */
    @NotNull T getValue() throws InvalidTypeException, ValidationException;

    /**
     * Returns the value of this ConfigEntry.
     *
     * @return the value of this ConfigEntry
     */
    @NotNull T getDefaultValue();

    /**
     * Updates the value of this ConfigEntry.
     *
     * @param value The new value
     * @throws InvalidTypeException if the given value does not match its expected {@link Type}
     * @throws ValidationException  if the given value fails validation
     */
    void setValue(@NotNull T value) throws InvalidTypeException, ValidationException;

    @SafeVarargs
    static <T> @NotNull ConfigEntry<T> of(@NotNull Config config, @NotNull String path, @Nullable String description, T defaultValue, Type<T> type, @NotNull Validator<T>... validators) throws InvalidTypeException, ValidationException {
        ConfigEntry<T> entry = new WritableConfigEntryImpl<>(Type.validated(type, validators), path, description, defaultValue);
        config.registerEntry(entry);
        return entry;
    }

    @SafeVarargs
    static <T> @NotNull ConfigEntry<T> of(@NotNull Config config, @NotNull String path, T defaultValue, Type<T> type, @NotNull Validator<T>... validators) throws InvalidTypeException, ValidationException {
        return of(config, path, null, defaultValue, type, validators);
    }

    @SafeVarargs
    static <T> @NotNull ConfigEntry<T> of(@NotNull String path, @Nullable String description, T defaultValue, Type<T> type, @NotNull Validator<T>... validators) throws InvalidTypeException, ValidationException {
        return new WritableConfigEntryImpl<>(Type.validated(type, validators), path, description, defaultValue);
    }

    @SafeVarargs
    static <T> @NotNull ConfigEntry<T> of(@NotNull String path, T defaultValue, Type<T> type, @NotNull Validator<T>... validators) throws InvalidTypeException, ValidationException {
        return of(path, null, defaultValue, type, validators);
    }
}
