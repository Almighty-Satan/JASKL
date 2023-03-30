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

package com.github.almightysatan.jaskl.impl;

import com.github.almightysatan.jaskl.Config;
import com.github.almightysatan.jaskl.ConfigEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class ConfigImpl implements Config {

    private final String description;
    private final Map<String, ConfigEntry<?>> entries = new HashMap<>();

    public ConfigImpl(@Nullable String description) {
        this.description = description;
    }

    public void registerEntry(@NotNull ConfigEntry<?> entry) {
        Objects.requireNonNull(entry);
        if (this.entries.containsKey(entry.getPath()))
            throw new IllegalArgumentException("Path already registered!");
        this.entries.put(entry.getPath(), entry);
    }

    @Nullable
    @Override
    public String getDescription() {
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
    @NotNull
    public Set<String> getPaths() {
        return this.getEntries().keySet();
    }

    /**
     * Returns a collection of all config entries.
     *
     * @return a collection of all config entries
     */
    @NotNull
    public Collection<ConfigEntry<?>> getValues() {
        return this.getEntries().values();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected Collection<WritableConfigEntry<?>> getCastedValues() {
        return (Collection<WritableConfigEntry<?>>) (Collection) getEntries().values();
    }
}
