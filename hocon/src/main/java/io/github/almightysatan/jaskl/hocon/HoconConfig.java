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

package io.github.almightysatan.jaskl.hocon;

import com.typesafe.config.*;
import io.github.almightysatan.jaskl.ConfigEntry;
import io.github.almightysatan.jaskl.ExceptionHandler;
import io.github.almightysatan.jaskl.Resource;
import io.github.almightysatan.jaskl.impl.ConfigImpl;
import io.github.almightysatan.jaskl.impl.WritableConfigEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * A hocon config implementation.
 */
public class HoconConfig extends ConfigImpl {

    private static final ConfigParseOptions PARSE_OPTIONS = ConfigParseOptions.defaults().setSyntax(ConfigSyntax.CONF)
            .setAllowMissing(false).setIncluder(new NopIncluder());
    private static final ConfigRenderOptions RENDER_OPTIONS = ConfigRenderOptions.defaults().setJson(false).setOriginComments(false);

    private final Resource resource;
    private Config config;

    private HoconConfig(@NotNull Resource resource, @Nullable String description, @Nullable ExceptionHandler exceptionHandler) {
        super(description, exceptionHandler);
        this.resource = Objects.requireNonNull(resource);
    }

    @Override
    public void load() throws IllegalStateException, IOException {
        if (this.config != null)
            throw new IllegalStateException();
        if (!this.resource.exists()) {
            this.config = ConfigFactory.empty();
            return;
        }
        this.config = ConfigFactory.parseReader(this.resource.getReader(), PARSE_OPTIONS);
        this.reload();
    }

    @Override
    public void reload() throws IllegalStateException {
        if (this.config == null)
            throw new IllegalStateException();
        for (ConfigEntry<?> uncastedConfigEntry : this.getValues()) {
            WritableConfigEntry<?> configEntry = (WritableConfigEntry<?>) uncastedConfigEntry;
            try {
                Object value = this.config.getValue(configEntry.getPath()).unwrapped();
                configEntry.putValue(value, this.getExceptionHandler());
            } catch (ConfigException.Missing ignored) {
            }
        }
    }

    @Override
    public void write() throws IOException {
        Config config = this.config;
        if (config == null)
            throw new IllegalStateException();
        resource.createIfNotExists();

        for (WritableConfigEntry<?> configEntry : this.getCastedValues()) {
            if (configEntry.isModified()) {
                Object entryValue = configEntry.getValueToWrite(Object::toString);
                if (entryValue instanceof BigInteger)
                    entryValue = entryValue.toString();
                if (entryValue instanceof BigDecimal)
                    entryValue = entryValue.toString();
                ConfigValue value = ConfigValueFactory.fromAnyRef(entryValue);
                if (configEntry.getDescription() != null)
                    value = value.withOrigin(value.origin().withComments(this.toCommentList(configEntry.getDescription())));
                config = config.withValue(configEntry.getPath(), value);
            }
        }

        this.writeIfNecessary(config, true);
    }

    @Override
    public @Unmodifiable @NotNull Set<@NotNull String> prune() throws IOException {
        Config config = this.config;
        if (config == null)
            throw new IllegalStateException();
        if (!this.resource.exists())
            return Collections.emptySet();

        Set<String> pathsToRemove = new HashSet<>();
        Set<String> valuePathsRemoved = new HashSet<>();
        this.resolvePathsToStrip("", config.root(), this.getPaths(), pathsToRemove, valuePathsRemoved);
        for (String path : pathsToRemove)
            config = config.withoutPath(path);

        this.writeIfNecessary(config, false);
        return Collections.unmodifiableSet(valuePathsRemoved);
    }

    @Override
    public void close() {
        if (this.config != null)
            this.config = null;
    }

    protected void writeIfNecessary(@NotNull Config config, boolean setDescription) throws IOException {
        if (config != this.config) {
            ConfigObject root = setDescription ? config.root().withOrigin(this.config.root().origin().withComments(this.toCommentList(this.getDescription()))) : config.root();
            String output = root.render(RENDER_OPTIONS);
            try (Writer fileWriter = this.resource.getWriter()) {
                fileWriter.write(output);
            }
            this.config = config;
        }
    }

    protected void resolvePathsToStrip(@NotNull String path, @NotNull ConfigObject node, @NotNull Set<String> paths, @NotNull Set<String> toRemove, @NotNull Set<String> valuePathsRemoved) {
        for (Map.Entry<String, ConfigValue> entry : node.entrySet()) {
            String fieldPath = (path.isEmpty() ? "" : path + ".") + entry.getKey();
            if (entry.getValue() instanceof ConfigObject) {
                if (paths.contains(fieldPath))
                    continue;
                ConfigObject child = (ConfigObject) entry.getValue();
                int numRemoved = toRemove.size();
                this.resolvePathsToStrip(fieldPath, child, paths, toRemove, valuePathsRemoved);
                int numChildren = child.size();
                if (numChildren == 0 || numChildren == (toRemove.size() - numRemoved))
                    toRemove.add(fieldPath);
            } else if (!paths.contains(fieldPath)) {
                toRemove.add(fieldPath);
                valuePathsRemoved.add(fieldPath);
            }
        }
    }

    protected @Nullable List<String> toCommentList(String description) {
        if (description == null)
            return null;
        return Arrays.asList(description.split("\n"));
    }

    /**
     * Creates a new {@link HoconConfig} instance.
     *
     * @param resource         A resource containing a hocon configuration. The resource will be created automatically if it
     *                         does not already exist and {@link #isReadOnly()} is {@code false}.
     * @param description      The description (comment) of this config file.
     * @param exceptionHandler The {@link ExceptionHandler}
     * @return A new {@link HoconConfig} instance.
     */
    public static io.github.almightysatan.jaskl.Config of(@NotNull Resource resource, @Nullable String description, @Nullable ExceptionHandler exceptionHandler) {
        return new HoconConfig(resource, description, exceptionHandler);
    }

    /**
     * Creates a new {@link HoconConfig} instance.
     *
     * @param resource    A resource containing a hocon configuration. The resource will be created automatically if it
     *                    does not already exist and {@link #isReadOnly()} is {@code false}.
     * @param description The description (comment) of this config file.
     * @return A new {@link HoconConfig} instance.
     */
    public static io.github.almightysatan.jaskl.Config of(@NotNull Resource resource, @Nullable String description) {
        return of(resource, description, null);
    }

    /**
     * Creates a new {@link HoconConfig} instance.
     *
     * @param resource A resource containing a hocon configuration. The resource will be created automatically if it
     *                 does not already exist and {@link #isReadOnly()} is {@code false}.
     * @return A new {@link HoconConfig} instance.
     */
    public static io.github.almightysatan.jaskl.Config of(@NotNull Resource resource) {
        return of(resource, null);
    }

    /**
     * Creates a new {@link HoconConfig} instance.
     *
     * @param file             The hocon file. The file will be created automatically if it does not already exist.
     * @param description      The description (comment) of this config file.
     * @param exceptionHandler The {@link ExceptionHandler}
     * @return A new {@link HoconConfig} instance.
     */
    public static io.github.almightysatan.jaskl.Config of(@NotNull File file, @Nullable String description, @Nullable ExceptionHandler exceptionHandler) {
        return of(Resource.of(file), description, exceptionHandler);
    }

    /**
     * Creates a new {@link HoconConfig} instance.
     *
     * @param file        The hocon file. The file will be created automatically if it does not already exist.
     * @param description The description (comment) of this config file.
     * @return A new {@link HoconConfig} instance.
     */
    public static io.github.almightysatan.jaskl.Config of(@NotNull File file, @Nullable String description) {
        return of(Resource.of(file), description);
    }

    /**
     * Creates a new {@link HoconConfig} instance.
     *
     * @param file The hocon file. The file will be created automatically if it does not already exist.
     * @return A new {@link HoconConfig} instance.
     */
    public static io.github.almightysatan.jaskl.Config of(@NotNull File file) {
        return of(file, null);
    }
}
