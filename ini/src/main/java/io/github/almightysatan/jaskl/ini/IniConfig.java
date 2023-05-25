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

package io.github.almightysatan.jaskl.ini;

import io.github.almightysatan.jaskl.Config;
import io.github.almightysatan.jaskl.ConfigEntry;
import io.github.almightysatan.jaskl.entries.ListConfigEntry;
import io.github.almightysatan.jaskl.entries.MapConfigEntry;
import io.github.almightysatan.jaskl.impl.ConfigImpl;
import io.github.almightysatan.jaskl.impl.Util;
import io.github.almightysatan.jaskl.impl.WritableConfigEntry;
import com.github.vincentrussell.ini.Ini;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.Objects;
import java.util.Set;

public class IniConfig extends ConfigImpl {

    private final File file;
    private Ini config;

    private IniConfig(@NotNull File file, @Nullable String description) {
        super(description);
        this.file = Objects.requireNonNull(file);
    }

    @Override
    public void load() throws IllegalStateException, IOException {
        if (this.config != null)
            throw new IllegalStateException();
        this.config = new Ini();
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
                String[] path = getSectionAndKey(configEntry);
                this.config.putValue(path[0], path[1], configEntry.getValueToWrite().toString());
                shouldWrite = true;
            }
        }

        if (shouldWrite)
            writeToFile();
    }

    @Override
    public void strip() throws IOException {
        if (this.config == null)
            throw new IllegalStateException();
        Util.createFileAndPath(this.file);

        boolean shouldWrite = false;
        Ini stripped = new Ini();
        Set<String> paths = this.getPaths();
        for (String section : this.config.getSections()) {
            for (String key : this.config.getSection(section).keySet()) {
                if (!paths.contains(key)) {
                    shouldWrite = true;
                    continue;
                }
                stripped.putValue(section, key, this.config.getValue(section, key));
            }
        }

        if (shouldWrite) {
            this.config = stripped;
            writeToFile();
        }
    }

    @Override
    public void close() {
        if (this.config != null)
            this.config = null;
    }

    /**
     * Creates a new {@link IniConfig} instance.
     *
     * @param file The ini file. The file will be created automatically if it does not already exist.
     * @param description The description (comment) of this config file.
     * @return A new {@link IniConfig} instance.
     */
    @Deprecated
    public static Config of(@NotNull File file, @Nullable String description) {
        return new IniConfig(file, description);
    }

    /**
     * Creates a new {@link IniConfig} instance.
     *
     * @param file The ini file. The file will be created automatically if it does not already exist.
     * @return A new {@link IniConfig} instance.
     */
    public static Config of(@NotNull File file) {
        return new IniConfig(file, null);
    }

    /**
     * Takes the current property instance and saves it to the file
     * @throws IOException if an IO error occurs
     */
    private void writeToFile() throws IOException {
        try (FileWriter writer = new FileWriter(file)){
            this.config.store(writer, this.getDescription() == null ? "" : this.getDescription());
        }
    }

    /**
     * Populates the property instance with the values from the file
     * @throws IOException if an IO error occurs
     */
    private void readFromFile() throws IOException {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            config.load(inputStream);
        }
    }

    /**
     * Initializes all config entries by setting the value based on the property instance
     */
    private void populateEntries() {
        for (WritableConfigEntry<?> configEntry : this.getCastedValues()) {
            if (configEntry instanceof ListConfigEntry | configEntry instanceof MapConfigEntry)
                throw new UnsupportedOperationException("Lists are not supported in Property Configs.");
            String[] path = getSectionAndKey(configEntry);
            Object value = this.config.getValue(path[0], path[1]);
            if (value != null)
                configEntry.putValue(value);
        }
    }

    private String[] getSectionAndKey(@NotNull ConfigEntry<?> entry) {
        String path = entry.getPath();
        String section = path.contains(".") ? path.substring(0, path.lastIndexOf('.')) : "section";
        String key = !path.contains(".") ? path : path.substring(path.lastIndexOf('.') + 1);
        return new String[] { section, key };
    }
}
