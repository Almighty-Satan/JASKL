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

import io.github.almightysatan.jaskl.InvalidTypeException;
import io.github.almightysatan.jaskl.Type;
import io.github.almightysatan.jaskl.ValidationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

public class WritableConfigEntryImpl<T> extends ConfigEntryImpl<T> implements WritableConfigEntry<T> {

    private final Type<T> type;
    private T value;
    private boolean modified = true; // true by default because Config#write should write the entry to the config if it does not exist

    public WritableConfigEntryImpl(@NotNull Type<T> type, @NotNull String path, @Nullable String description, @NotNull T defaultValue) throws InvalidTypeException, ValidationException {
        super(path, description, defaultValue);
        this.type = Objects.requireNonNull(type);
        this.value = type.toEntryType(defaultValue);
    }

    @Override
    public Type<T> getType() {
        return this.type;
    }

    @Override
    public @NotNull T getValue() throws InvalidTypeException, ValidationException {
        return this.value;
    }

    @Override
    public void setValue(@NotNull T value) throws InvalidTypeException, ValidationException {
        T parsedValue = this.toType(value);
        if (parsedValue.equals(this.getValue()))
            return;
        this.value = parsedValue;
        this.modified = true;
    }

    @Override
    public void putValue(@NotNull Object value) throws InvalidTypeException, ValidationException {
        this.value = this.toType(value);
        this.modified = false;
    }

    private T toType(@Nullable Object value) throws InvalidTypeException, ValidationException {
        if (value == null)
            throw new InvalidTypeException(this.getPath());
        try {
            return this.getType().toEntryType(value);
        } catch (InvalidTypeException e) {
            throw new InvalidTypeException(this.getPath(), e);
        } catch (ValidationException e) {
            throw new ValidationException(this.getPath(), e);
        }
    }

    @Override
    public @NotNull Object getValueToWrite(@NotNull Function<@NotNull Object, @NotNull Object> keyPreprocessor)  throws InvalidTypeException, ValidationException {
        try {
            return this.getType().toWritable(this.getValue(), keyPreprocessor);
        } catch (InvalidTypeException e) {
            throw new InvalidTypeException(this.getPath(), e);
        }
    }

    @Override
    public boolean isModified() {
        return this.modified;
    }
}
