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

import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public interface Config {

    /**
     * Loads the config from a storage location.
     *
     * @throws IOException if an I/O exception occurs.
     * @throws IllegalStateException if called multiple times.
     */
    void load() throws IOException, IllegalStateException;

    /**
     * Reloads the config.
     * Assumes that {@link Config#load()} has been called already.
     * No files/data storage locations will be created or initialized.
     *
     * @throws IOException if an I/O exception occurs.
     * @throws IllegalStateException if {@link Config#load()} hasn't been called.
     */
    void reload() throws IOException, IllegalStateException;

    /**
     * Saves the configuration to it's corresponding data storage location.
     *
     * @throws IOException if an I/O exception occurs.
     */
    void write() throws IOException;

    /**
     * Cleans up dead entries from the storage location.
     * An entry is dead if no {@link ConfigEntry} references its path.
     *
     * @throws IOException if an I/O exception occurs.
     */
    void strip() throws IOException;

    /**
     * Closes the corresponding data storage location.
     */
    void close();

    /**
     * Returns the description of this config.
     *
     * @return the description of this config
     */
    @Nullable String getDescription();
}
