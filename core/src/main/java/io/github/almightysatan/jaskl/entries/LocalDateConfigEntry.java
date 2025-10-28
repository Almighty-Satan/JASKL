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

import java.time.LocalDate;

public interface LocalDateConfigEntry extends ConfigEntry<LocalDate> {

    /**
     * Creates a new config entry.
     *
     * @param config       the config
     * @param path         the case-sensitive dotted path
     * @param description  the possibly-null description
     * @param defaultValue the default value
     * @param validators   the {@link Validator Validators}
     * @return a new entry
     * @throws InvalidTypeException if the default value's type is invalid
     * @throws ValidationException  if the default value fails validation
     */
    @SafeVarargs
    static @NotNull LocalDateConfigEntry of(@NotNull Config config, @NotNull String path, @Nullable String description, @NotNull LocalDate defaultValue, @NotNull Validator<? super LocalDate>... validators) throws InvalidTypeException, ValidationException {
        class LocalDateConfigEntryImpl extends WritableConfigEntryImpl<LocalDate> implements LocalDateConfigEntry {
            LocalDateConfigEntryImpl() {
                super(Type.validated(Type.LOCAL_DATE, validators), path, description, defaultValue);
                this.register(config);
            }
        }

        return new LocalDateConfigEntryImpl();
    }

    /**
     * Creates a new config entry.
     *
     * @param config       the config
     * @param path         the case-sensitive dotted path
     * @param defaultValue the default value
     * @param validators   the {@link Validator Validators}
     * @return a new entry
     * @throws InvalidTypeException if the default value's type is invalid
     * @throws ValidationException  if the default value fails validation
     */
    @SafeVarargs
    static @NotNull LocalDateConfigEntry of(@NotNull Config config, @NotNull String path, LocalDate defaultValue, @NotNull Validator<? super LocalDate>... validators) throws InvalidTypeException, ValidationException {
        return of(config, path, null, defaultValue, validators);
    }
}
