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

import java.math.BigDecimal;

public interface BigDecimalConfigEntry extends ConfigEntry<BigDecimal> {

    @SafeVarargs
    static @NotNull BigDecimalConfigEntry of(@NotNull Config config, @NotNull String path, @Nullable String description, BigDecimal defaultValue, @NotNull Validator<? super BigDecimal>... validators) throws InvalidTypeException, ValidationException {
        class BigDecimalConfigEntryImpl extends WritableConfigEntryImpl<BigDecimal> implements BigDecimalConfigEntry {
            BigDecimalConfigEntryImpl() {
                super(Type.validated(Type.BIG_DECIMAL, validators), path, description, defaultValue);
                this.register(config);
            }
        }

        return new BigDecimalConfigEntryImpl();
    }

    @SafeVarargs
    static @NotNull BigDecimalConfigEntry of(@NotNull Config config, @NotNull String path, BigDecimal defaultValue, @NotNull Validator<? super BigDecimal>... validators) throws InvalidTypeException, ValidationException {
        return of(config, path, null, defaultValue, validators);
    }
}
