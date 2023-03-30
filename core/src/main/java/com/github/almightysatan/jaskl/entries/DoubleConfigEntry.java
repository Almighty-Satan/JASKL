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

public class DoubleConfigEntry extends WritableConfigEntryImpl<Double> {

    DoubleConfigEntry(@NotNull String path, @Nullable String description, @NotNull Double defaultValue) {
        super(path, description, defaultValue);
    }

    @Override
    protected @NotNull Double checkType(@NotNull Object type) {
        if (type instanceof Double)
            return (Double) type;

        if (type instanceof BigDecimal) {
            BigDecimal bigDecimal = (BigDecimal) type;
            if (bigDecimal.compareTo(BigDecimal.valueOf(Double.MIN_VALUE)) > 0 && bigDecimal.compareTo(BigDecimal.valueOf(Double.MAX_VALUE)) < 0)
                return bigDecimal.doubleValue();
        }

        if (type instanceof Integer) {
            return (double) (int) type;
        }

        if (type instanceof String) {
            try {
                return Double.valueOf((String) type);
            } catch (NumberFormatException ignored) {}
        }

        throw new InvalidTypeException(getPath(), Double.class, type.getClass());
    }

    public static ConfigEntry<Double> of(@NotNull Config config, @NotNull String path, @Nullable String description, double defaultValue) {
        return new DoubleConfigEntry(path, description, defaultValue).register(config);
    }
}
