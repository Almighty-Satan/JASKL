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
import io.github.almightysatan.jaskl.ExceptionHandler;
import io.github.almightysatan.jaskl.Resource;
import io.github.almightysatan.jaskl.entries.ListConfigEntry;
import io.github.almightysatan.jaskl.entries.MapConfigEntry;
import io.github.almightysatan.jaskl.impl.ConfigImpl;
import io.github.almightysatan.jaskl.impl.EntryDescriptor;
import io.github.almightysatan.jaskl.impl.WritableConfigEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.*;
import java.util.Map.Entry;

public class PropertiesConfig extends ConfigImpl {

    private final Resource resource;
    private Properties config;

    private PropertiesConfig(@NotNull Resource resource, @Nullable String description, @Nullable ExceptionHandler exceptionHandler) {
        super(description, exceptionHandler);
        this.resource = Objects.requireNonNull(resource);
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
        if (!this.resource.exists())
            return;

        readFromFile();
        populateEntries();

    }

    @Override
    public void write() throws IOException {
        if (this.config == null)
            throw new IllegalStateException();
        this.resource.createIfNotExists();

        boolean shouldWrite = false;
        for (WritableConfigEntry<?> configEntry : this.getCastedValues()) {
            if (configEntry.isModified()) {
                this.config.setProperty(configEntry.getPath(), configEntry.getValueToWrite(key -> {
                    // Ignore all comments
                    if (key instanceof EntryDescriptor)
                        return ((EntryDescriptor) key).getValue();
                    return key;
                }).toString());
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
        if (!this.resource.exists())
            return Collections.emptySet();

        Properties stripped = new Properties();
        Collection<String> paths = this.getPaths();
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
        try (Writer writer = this.resource.getWriter()) {
            this.config.store(writer, this.getDescription());
        }
    }

    /**
     * Populates the property instance with the values from the file
     *
     * @throws IOException If an I/O exception occurs.
     */
    private void readFromFile() throws IOException {
        try (Reader reader = this.resource.getReader()) {
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
                configEntry.putValue(value, this.getExceptionHandler());
        }
    }

    /**
     * Creates a new {@link PropertiesConfig} instance.
     *
     * @param resource         A resource containing a properties configuration. The resource will be created automatically
     *                         if it does not already exist and {@link #isReadOnly()} is {@code false}.
     * @param description      The description (comment) of this config file.
     * @param exceptionHandler The {@link ExceptionHandler}
     * @return A new {@link PropertiesConfig} instance.
     */
    public static Config of(@NotNull Resource resource, @Nullable String description, @Nullable ExceptionHandler exceptionHandler) {
        return new PropertiesConfig(resource, description, exceptionHandler);
    }

    /**
     * Creates a new {@link PropertiesConfig} instance.
     *
     * @param resource    A resource containing a properties configuration. The resource will be created automatically
     *                    if it does not already exist and {@link #isReadOnly()} is {@code false}.
     * @param description The description (comment) of this config file.
     * @return A new {@link PropertiesConfig} instance.
     */
    public static Config of(@NotNull Resource resource, @Nullable String description) {
        return of(resource, description, null);
    }

    /**
     * Creates a new {@link PropertiesConfig} instance.
     *
     * @param resource A resource containing a properties configuration. The resource will be created automatically
     *                 if it does not already exist and {@link #isReadOnly()} is {@code false}.
     * @return A new {@link PropertiesConfig} instance.
     */
    public static Config of(@NotNull Resource resource) {
        return of(resource, null);
    }

    /**
     * Creates a new {@link PropertiesConfig} instance.
     *
     * @param file             The properties file. The file will be created automatically if it does not already exist.
     * @param description      The description (comment) of this config file.
     * @param exceptionHandler The {@link ExceptionHandler}
     * @return A new {@link PropertiesConfig} instance.
     */
    public static Config of(@NotNull File file, @Nullable String description, @Nullable ExceptionHandler exceptionHandler) {
        return of(Resource.of(file), description, exceptionHandler);
    }

    /**
     * Creates a new {@link PropertiesConfig} instance.
     *
     * @param file        The properties file. The file will be created automatically if it does not already exist.
     * @param description The description (comment) of this config file.
     * @return A new {@link PropertiesConfig} instance.
     */
    public static Config of(@NotNull File file, @Nullable String description) {
        return of(Resource.of(file), description);
    }

    /**
     * Creates a new {@link PropertiesConfig} instance.
     *
     * @param file The properties file. The file will be created automatically if it does not already exist.
     * @return A new {@link PropertiesConfig} instance.
     */
    public static Config of(@NotNull File file) {
        return of(file, null);
    }
}
