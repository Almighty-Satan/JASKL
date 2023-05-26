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

package io.github.almightysatan.jaskl.impl;

import io.github.almightysatan.jaskl.*;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;

public interface WritableConfigEntry<T> extends ConfigEntry<T> {

    Type<T> getType();

    void putValue(@NotNull Object value) throws InvalidTypeException, ValidationException;

    @NotNull Object getValueToWrite(@NotNull Function<@NotNull Object, @NotNull Object> keyPreprocessor)  throws InvalidTypeException;

    default @NotNull Object getValueToWrite() {
        return this.getValueToWrite(Function.identity());
    }

    boolean isModified();

    default WritableConfigEntry<T> register(@NotNull Config config) {
        Objects.requireNonNull(config);
        ((ConfigImpl) config).registerEntry(this);
        return this;
    }
}
