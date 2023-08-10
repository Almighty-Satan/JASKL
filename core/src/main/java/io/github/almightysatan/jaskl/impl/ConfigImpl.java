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

import io.github.almightysatan.jaskl.Config;
import io.github.almightysatan.jaskl.ConfigEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class ConfigImpl implements Config {

    private final String description;
    private final Map<String, ConfigEntry<?>> entries = new HashMap<>();

    public ConfigImpl(@Nullable String description) {
        this.description = description;
    }

    @Override
    public void registerEntry(@NotNull ConfigEntry<?> entry) {
        Objects.requireNonNull(entry);
        for (String path : this.getPaths())
            if (path.startsWith(entry.getPath()) || entry.getPath().startsWith(path))
                throw new IllegalArgumentException(String.format("Paths have to be prefix-free! %s", entry.getPath()));
        this.entries.put(entry.getPath(), entry);
    }

    @Override
    public @Nullable String getDescription() {
        return description;
    }

    /**
     * Returns a map of all paths with their config entries.
     *
     * @return a map of all paths with their config entries
     */
    public @NotNull Map<String, ConfigEntry<?>> getEntries() {
        return entries;
    }

    /**
     * Returns a set of all the paths of the config entries.
     *
     * @return a set of all the paths of the config entries
     */
    public @NotNull Set<String> getPaths() {
        return this.getEntries().keySet();
    }

    /**
     * Returns a collection of all config entries.
     *
     * @return a collection of all config entries
     */
    public @NotNull Collection<ConfigEntry<?>> getValues() {
        return this.getEntries().values();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected @NotNull Collection<WritableConfigEntry<?>> getCastedValues() {
        return (Collection<WritableConfigEntry<?>>) (Collection) getEntries().values();
    }
}
