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

package io.github.almightysatan.jaskl.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class EntryDescriptor {
    
    private final Object value;
    private final String description;

    public EntryDescriptor(@NotNull Object value, @Nullable String description) {
        this.value = Objects.requireNonNull(value);
        this.description = description;
    }

    public @NotNull Object getValue() {
        return value;
    }

    public @Nullable String getDescription() {
        return this.description;
    }

    @Override
    public String toString() {
        // This ensures compatibility with config implementations that call toString on key values (Hocon and Mongodb)
        return this.value.toString();
    }
}
