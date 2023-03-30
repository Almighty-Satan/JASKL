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

package com.github.almightysatan.jaskl.entries;

import com.github.almightysatan.jaskl.Config;
import com.github.almightysatan.jaskl.ConfigEntry;
import com.github.almightysatan.jaskl.InvalidTypeException;
import com.github.almightysatan.jaskl.impl.WritableConfigEntryImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;

public class FloatConfigEntry extends WritableConfigEntryImpl<Float> {

    FloatConfigEntry(@NotNull String path, @Nullable String description, @NotNull Float defaultValue) {
        super(path, description, defaultValue);
    }

    @Override
    protected @NotNull Float checkType(@NotNull Object type) {
        if (type instanceof Float)
            return (Float) type;

        if (type instanceof BigDecimal) {
            BigDecimal bigDecimal = (BigDecimal) type;
            if (bigDecimal.compareTo(BigDecimal.valueOf(Float.MIN_VALUE)) > 0 && bigDecimal.compareTo(BigDecimal.valueOf(Float.MAX_VALUE)) < 0)
                return bigDecimal.floatValue();
        }

        if (type instanceof Integer) {
            return (float) (int) type;
        }

        if (type instanceof Long) {
            return (float) (long) type;
        }

        if (type instanceof Double) {
            double doubleVal = (double) type;
            if (doubleVal > Float.MIN_VALUE && doubleVal < Float.MAX_VALUE)
                return (float) doubleVal;
        }

        if (type instanceof String) {
            try {
                return Float.valueOf((String) type);
            } catch (NumberFormatException ignored) {}
        }

        throw new InvalidTypeException(getPath(), Float.class, type.getClass());
    }

    public static ConfigEntry<Float> of(@NotNull Config config, @NotNull String path, @Nullable String description, float defaultValue) {
        return new FloatConfigEntry(path, description, defaultValue).register(config);
    }
}
