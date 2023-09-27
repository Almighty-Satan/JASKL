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

public interface LongConfigEntry extends ConfigEntry<Long> {

    @SafeVarargs
    static @NotNull LongConfigEntry of(@NotNull Config config, @NotNull String path, @Nullable String description, long defaultValue, @NotNull Validator<Long>... validators) throws InvalidTypeException, ValidationException {
        class LongConfigEntryImpl extends WritableConfigEntryImpl<Long> implements LongConfigEntry {
            LongConfigEntryImpl() {
                super(Type.validated(Type.LONG, validators), path, description, defaultValue);
                this.register(config);
            }
        }

        return new LongConfigEntryImpl();
    }

    @SafeVarargs
    static @NotNull LongConfigEntry of(@NotNull Config config, @NotNull String path, long defaultValue, @NotNull Validator<Long>... validators) throws InvalidTypeException, ValidationException {
        return of(config, path, null, defaultValue, validators);
    }
}
