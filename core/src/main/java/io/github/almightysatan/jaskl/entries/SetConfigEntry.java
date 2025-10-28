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

import java.util.Set;

public interface SetConfigEntry<T> extends ConfigEntry<Set<T>> {

    /**
     * Creates a new config entry.
     *
     * @param config       the config
     * @param path         the case-sensitive dotted path
     * @param description  the possibly-null description
     * @param type         the {@link Type} of the elements in the set
     * @param defaultValue the default value
     * @param validators   the {@link Validator Validators}
     * @param <T>          the elements in the set
     * @return a new entry
     * @throws InvalidTypeException if the default value's type is invalid
     * @throws ValidationException  if the default value fails validation
     */
    @SafeVarargs
    static <T> @NotNull SetConfigEntry<T> of(@NotNull Config config, @NotNull String path, @Nullable String description, @NotNull Set<T> defaultValue, @NotNull Type<T> type, @NotNull Validator<? super Set<T>>... validators) throws InvalidTypeException, ValidationException {
        class SetConfigEntryImpl extends WritableConfigEntryImpl<Set<T>> implements SetConfigEntry<T> {
            SetConfigEntryImpl() {
                super(Type.validated(Type.set(type), validators), path, description, defaultValue);
                this.register(config);
            }
        }

        return new SetConfigEntryImpl();
    }

    /**
     * Creates a new config entry.
     *
     * @param config       the config
     * @param path         the case-sensitive dotted path
     * @param type         the {@link Type} of the elements in the set
     * @param defaultValue the default value
     * @param validators   the {@link Validator Validators}
     * @param <T>          the elements in the set
     * @return a new entry
     * @throws InvalidTypeException if the default value's type is invalid
     * @throws ValidationException  if the default value fails validation
     */
    @SafeVarargs
    static <T> @NotNull SetConfigEntry<T> of(@NotNull Config config, @NotNull String path, @NotNull Set<T> defaultValue, @NotNull Type<T> type, @NotNull Validator<? super Set<T>>... validators) throws InvalidTypeException, ValidationException {
        return of(config, path, null, defaultValue, type, validators);
    }
}
