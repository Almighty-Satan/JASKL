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

package io.github.almightysatan.jaskl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ConfigEntry<T> {

    /**
     * Returns the path leading to this ConfigEntry's value.
     *
     * @return the path of this ConfigEntry
     */
    @NotNull String getPath();

    /**
     * Returns the description of this ConfigEntry.
     *
     * @return the description of this ConfigEntry
     */
    @Nullable String getDescription();

    /**
     * Returns the value of this ConfigEntry.
     *
     * @return the value of this ConfigEntry
     */
    @NotNull T getValue();

    /**
     * Returns the value of this ConfigEntry.
     *
     * @return the value of this ConfigEntry
     */
    @NotNull T getDefaultValue();

    /**
     * Updates the value of this ConfigEntry.
     *
     * @param value The new value
     */
    void setValue(@NotNull T value) throws InvalidTypeException, ValidationException;

}
