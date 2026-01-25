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

package io.github.almightysatan.jaskl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Formats comments of {@link Config Configs} and {@link ConfigEntry}
 */
public interface DescriptionFormatter {

    /**
     * Returns the formatted description of the given {@link Config}.
     * If {@code null} is returned no comment will be written.
     *
     * @param config The config
     * @return The formatted description
     */
    @Nullable String formatFileDescription(@NotNull Config config);

    /**
     * Returns the formatted description of the given {@link ConfigEntry}.
     * If {@code null} is returned no comment will be written.
     *
     * @param entry The entry
     * @param <T>   The type of the config entry
     * @return The formatted description
     */
    <T> @Nullable String formatEntryDescription(@NotNull ConfigEntry<T> entry);
}
