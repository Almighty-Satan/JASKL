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

package io.github.almightysatan.jaskl.properties;

import io.github.almightysatan.jaskl.Config;
import io.github.almightysatan.jaskl.entries.ListConfigEntry;
import io.github.almightysatan.jaskl.entries.MapConfigEntry;
import io.github.almightysatan.jaskl.impl.ConfigImpl;
import io.github.almightysatan.jaskl.impl.Util;
import io.github.almightysatan.jaskl.impl.WritableConfigEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class PropertiesConfig extends ConfigImpl {

    private final File file;
    private Properties config;

    private PropertiesConfig(@NotNull File file, @Nullable String description) {
        super(description);
        this.file = Objects.requireNonNull(file);
    }

    @Override
    public void load() throws IllegalStateException, IOException {
        if (this.config != null)
            throw new IllegalStateException();
        this.config = new Properties();
        this.reload();
    }

    @Override
    public void reload() throws IllegalStateException, IOException {
        if (this.config == null)
            throw new IllegalStateException();
        if (!this.file.exists())
            return;

        readFromFile();
        populateEntries();

    }

    @Override
    public void write() throws IOException {
        if (this.config == null)
            throw new IllegalStateException();
        Util.createFileAndPath(this.file);

        boolean shouldWrite = false;
        for (WritableConfigEntry<?> configEntry : this.getCastedValues()) {
            if (configEntry.isModified()) {
                this.config.setProperty(configEntry.getPath(), configEntry.getValueToWrite().toString());
                shouldWrite = true;
            }
        }

        if (shouldWrite)
            writeToFile();
    }

    @Override
    public @Unmodifiable @NotNull Set<@NotNull String> prune() throws IOException {
        if (this.config == null)
            throw new IllegalStateException();
        Util.createFileAndPath(this.file);

        Properties stripped = new Properties();
        Set<String> paths = this.getPaths();
        Set<String> pathsRemoved = new HashSet<>();
        for (Entry<Object, Object> entry : this.config.entrySet()) {
            String key = (String) entry.getKey();
            if (!paths.contains(key)) {
                pathsRemoved.add(key);
                continue;
            }
            stripped.setProperty(key, (String) entry.getValue());
        }

        if (!pathsRemoved.isEmpty()) {
            this.config = stripped;
            writeToFile();
        }
        return Collections.unmodifiableSet(pathsRemoved);
    }

    @Override
    public void close() {
        if (this.config != null)
            this.config = null;
    }

    /**
     * Takes the current property instance and saves it to the file
     *
     * @throws IOException If an I/O exception occurs.
     */
    private void writeToFile() throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            this.config.store(writer, this.getDescription());
        }
    }

    /**
     * Populates the property instance with the values from the file
     *
     * @throws IOException If an I/O exception occurs.
     */
    private void readFromFile() throws IOException {
        try (FileReader reader = new FileReader(file)) {
            config.load(reader);
        }
    }

    /**
     * Initializes all config entries by setting the value based on the property instance
     */
    private void populateEntries() {
        for (WritableConfigEntry<?> configEntry : this.getCastedValues()) {
            if (configEntry instanceof ListConfigEntry || configEntry instanceof MapConfigEntry)
                throw new UnsupportedOperationException("Lists are not supported in Property Configs.");
            Object value = this.config.get(configEntry.getPath());
            if (value != null)
                configEntry.putValue(value);
        }
    }

    /**
     * Creates a new {@link PropertiesConfig} instance.
     *
     * @param file        The properties file. The file will be created automatically if it does not already exist.
     * @param description The description (comment) of this config file.
     * @return A new {@link PropertiesConfig} instance.
     */
    public static Config of(@NotNull File file, @Nullable String description) {
        return new PropertiesConfig(file, description);
    }

    /**
     * Creates a new {@link PropertiesConfig} instance.
     *
     * @param file The properties file. The file will be created automatically if it does not already exist.
     * @return A new {@link PropertiesConfig} instance.
     */
    public static Config of(@NotNull File file) {
        return new PropertiesConfig(file, null);
    }
}
