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

import java.math.BigInteger;

public class LongConfigEntry extends WritableConfigEntryImpl<Long> {

    private LongConfigEntry(@NotNull Config config, @NotNull String path, @Nullable String description, @NotNull Long defaultValue) {
        super(config, path, description, defaultValue);
    }

    @Override
    protected @NotNull Long checkType(@NotNull Object type) {
        if (type instanceof Long)
            return (Long) type;

        if (type instanceof Integer)
            return ((Integer) type).longValue();

        if (type instanceof BigInteger) {
            BigInteger bigInt = (BigInteger) type;
            if (bigInt.compareTo(BigInteger.valueOf(Long.MIN_VALUE)) > 0 && bigInt.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) < 0)
                return bigInt.longValue();
        }

        if (type instanceof String) {
            try {
                return Long.valueOf((String) type);
            } catch (NumberFormatException ignored) {
                System.out.println("Found String but couldnt transform");
            }
        }

        throw new InvalidTypeException(getPath(), Long.class, type.getClass());

    }

    public static ConfigEntry<Long> of(@NotNull Config config, @NotNull String path, @Nullable String description, long defaultValue) {
        return new LongConfigEntry(config, path, description, defaultValue);
    }
}
