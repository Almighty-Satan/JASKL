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

package io.github.almightysatan.jaskl.entries;

import io.github.almightysatan.jaskl.*;
import io.github.almightysatan.jaskl.impl.WritableConfigEntryImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface FloatConfigEntry extends ConfigEntry<Float> {

    @SafeVarargs
    static FloatConfigEntry of(@NotNull Config config, @NotNull String path, @Nullable String description, float defaultValue, @NotNull Validator<Float>... validators) throws InvalidTypeException, ValidationException {
        class FloatConfigEntryImpl extends WritableConfigEntryImpl<Float> implements FloatConfigEntry {
            FloatConfigEntryImpl() {
                super(Type.validated(Type.FLOAT, validators), path, description, defaultValue);
                this.register(config);
            }
        }

        return new FloatConfigEntryImpl();
    }

    @SafeVarargs
    static FloatConfigEntry of(@NotNull Config config, @NotNull String path, float defaultValue, @NotNull Validator<Float>... validators) throws InvalidTypeException, ValidationException {
        return of(config, path, null, defaultValue, validators);
    }
}
