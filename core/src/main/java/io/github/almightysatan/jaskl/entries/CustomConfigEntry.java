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

package io.github.almightysatan.jaskl.entries;

import io.github.almightysatan.jaskl.*;
import io.github.almightysatan.jaskl.impl.WritableConfigEntryImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface CustomConfigEntry<T> extends ConfigEntry<T> {

    /**
     * Creates a new config entry using an annotated class.
     *
     * @param config       the config
     * @param path         the case-sensitive dotted path
     * @param description  the possibly-null description
     * @param type         the type of the custom object
     * @param defaultValue the default value
     * @param validators   the {@link Validator Validators}
     * @param <T>          the custom object
     * @return a new entry
     * @throws InvalidTypeException if the default value's type is invalid
     * @throws ValidationException  if the default value fails validation
     */
    @SafeVarargs
    static <T> @NotNull CustomConfigEntry<T> of(@NotNull Config config, @NotNull String path, @Nullable String description, @NotNull T defaultValue, @NotNull Type<T> type, @NotNull Validator<? super T>... validators) throws InvalidTypeException, ValidationException {
        class CustomConfigEntryImpl extends WritableConfigEntryImpl<T> implements CustomConfigEntry<T> {
            CustomConfigEntryImpl() {
                super(Type.validated(type, validators), path, description, defaultValue);
                this.register(config);
            }
        }

        return new CustomConfigEntryImpl();
    }

    /**
     * Creates a new config entry using an annotated class.
     *
     * @param config       the config
     * @param path         the case-sensitive dotted path
     * @param type         the type of the custom object
     * @param defaultValue the default value
     * @param validators   the {@link Validator Validators}
     * @param <T>          the custom object
     * @return a new entry
     * @throws InvalidTypeException if the default value's type is invalid
     * @throws ValidationException  if the default value fails validation
     */
    @SafeVarargs
    static <T> @NotNull CustomConfigEntry<T> of(@NotNull Config config, @NotNull String path, @NotNull T defaultValue, @NotNull Type<T> type, @NotNull Validator<? super T>... validators) throws InvalidTypeException, ValidationException {
        return CustomConfigEntry.of(config, path, null, defaultValue, type, validators);
    }

    /**
     * Creates a new config entry using an annotated class.
     *
     * @param config       the config
     * @param path         the case-sensitive dotted path
     * @param description  the possibly-null description
     * @param clazz        the class of the custom object
     * @param defaultValue the default value
     * @param validators   the {@link Validator Validators}
     * @param <T>          the custom object
     * @return a new entry
     * @throws InvalidTypeException if the default value's type is invalid
     * @throws ValidationException  if the default value fails validation
     */
    @SafeVarargs
    static <T> @NotNull CustomConfigEntry<T> of(@NotNull Config config, @NotNull String path, @Nullable String description, @NotNull T defaultValue, @NotNull Class<T> clazz, @NotNull Validator<? super T>... validators) throws InvalidTypeException, ValidationException {
        return CustomConfigEntry.of(config, path, description, defaultValue, Type.custom(clazz), validators);
    }

    /**
     * Creates a new config entry using an annotated class.
     *
     * @param config       the config
     * @param path         the case-sensitive dotted path
     * @param clazz        the class of the custom object
     * @param defaultValue the default value
     * @param validators   the {@link Validator Validators}
     * @param <T>          the custom object
     * @return a new entry
     * @throws InvalidTypeException if the default value's type is invalid
     * @throws ValidationException  if the default value fails validation
     */
    @SafeVarargs
    static <T> @NotNull CustomConfigEntry<T> of(@NotNull Config config, @NotNull String path, @NotNull T defaultValue, @NotNull Class<T> clazz, @NotNull Validator<? super T>... validators) throws InvalidTypeException, ValidationException {
        return of(config, path, null, defaultValue, clazz, validators);
    }

    /**
     * Creates a new config entry using an annotated class.
     *
     * @param config       the config
     * @param path         the case-sensitive dotted path
     * @param description  the possibly-null description
     * @param defaultValue the default value
     * @param validators   the {@link Validator Validators}
     * @param <T>          the custom object
     * @return a new entry
     * @throws InvalidTypeException if the default value's type is invalid
     * @throws ValidationException  if the default value fails validation
     */
    @SuppressWarnings("unchecked")
    @SafeVarargs
    static <T> @NotNull CustomConfigEntry<T> of(@NotNull Config config, @NotNull String path, @Nullable String description, @NotNull T defaultValue, @NotNull Validator<? super T>... validators) throws InvalidTypeException, ValidationException {
        return of(config, path, description, defaultValue, (Class<T>) defaultValue.getClass(), validators);
    }


    /**
     * Creates a new config entry using an annotated class.
     *
     * @param config       the config
     * @param path         the case-sensitive dotted path
     * @param defaultValue the default value
     * @param validators   the {@link Validator Validators}
     * @param <T>          the custom object
     * @return a new entry
     * @throws InvalidTypeException if the default value's type is invalid
     * @throws ValidationException  if the default value fails validation
     */
    @SuppressWarnings("unchecked")
    @SafeVarargs
    static <T> @NotNull CustomConfigEntry<T> of(@NotNull Config config, @NotNull String path, @NotNull T defaultValue, @NotNull Validator<? super T>... validators) throws InvalidTypeException, ValidationException {
        return of(config, path, defaultValue, (Class<T>) defaultValue.getClass(), validators);
    }

    /**
     * Creates a new config entry using an {@link ObjectMapper}.
     *
     * @param config       the config
     * @param path         the case-sensitive dotted path
     * @param description  the possibly-null description
     * @param objectMapper an {@link ObjectMapper} instance
     * @param defaultValue the default value
     * @param validators   the {@link Validator Validators}
     * @param <T>          the custom object
     * @return a new entry
     * @throws InvalidTypeException if the default value's type is invalid
     * @throws ValidationException  if the default value fails validation
     */
    @SafeVarargs
    static <T> @NotNull CustomConfigEntry<T> of(@NotNull Config config, @NotNull String path, @Nullable String description,
                                                @NotNull T defaultValue, @NotNull ObjectMapper<T> objectMapper,
                                                @NotNull Validator<? super T>... validators) throws InvalidTypeException, ValidationException {
        class CustomConfigEntryImpl extends WritableConfigEntryImpl<T> implements CustomConfigEntry<T> {
            CustomConfigEntryImpl() {
                super(Type.validated(Type.custom(objectMapper), validators), path, description, defaultValue);
                this.register(config);
            }
        }

        return new CustomConfigEntryImpl();
    }

    /**
     * Creates a new config entry using an {@link ObjectMapper}.
     *
     * @param config       the config
     * @param path         the case-sensitive dotted path
     * @param objectMapper an {@link ObjectMapper} instance
     * @param defaultValue the default value
     * @param validators   the {@link Validator Validators}
     * @param <T>          the custom object
     * @return a new entry
     * @throws InvalidTypeException if the default value's type is invalid
     * @throws ValidationException  if the default value fails validation
     */
    @SafeVarargs
    static <T> @NotNull CustomConfigEntry<T> of(@NotNull Config config, @NotNull String path, @NotNull T defaultValue,
                                                @NotNull ObjectMapper<T> objectMapper, @NotNull Validator<? super T>... validators)
            throws InvalidTypeException, ValidationException {
        return of(config, path, null, defaultValue, objectMapper, validators);
    }
}
