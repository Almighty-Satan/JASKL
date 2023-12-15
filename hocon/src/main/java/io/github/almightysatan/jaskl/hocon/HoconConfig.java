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
import io.github.almightysatan.jaskl.impl.ConfigImpl;
import io.github.almightysatan.jaskl.impl.Util;
import io.github.almightysatan.jaskl.impl.WritableConfigEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * A hocon config implementation.
 */
public class HoconConfig extends ConfigImpl {

    private static final ConfigParseOptions PARSE_OPTIONS = ConfigParseOptions.defaults().setSyntax(ConfigSyntax.CONF)
            .setAllowMissing(false).setIncluder(new NopIncluder());
    protected static final ConfigRenderOptions RENDER_OPTIONS = ConfigRenderOptions.defaults().setJson(false).setOriginComments(false);

    protected final File file;
    protected Config config;

    protected HoconConfig(@NotNull File file, @Nullable String description) {
        super(description);
        this.file = Objects.requireNonNull(file);
    }

    @Override
    public void load() throws IllegalStateException {
        if (this.config != null)
            throw new IllegalStateException();
        if (!file.exists()) {
            this.config = ConfigFactory.empty();
            return;
        }
        this.config = ConfigFactory.parseFile(file, PARSE_OPTIONS);
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
                configEntry.putValue(value);
            } catch (ConfigException.Missing ignored) {
            }
        }
    }

    @Override
    public void write() throws IOException {
        Config config = this.config;
        if (config == null)
            throw new IllegalStateException();
        Util.createFileAndPath(this.file);

        for (WritableConfigEntry<?> configEntry : this.getCastedValues()) {
            if (configEntry.isModified()) {
                Object entryValue = configEntry.getValueToWrite(Object::toString);
                if (entryValue instanceof BigInteger)
                    entryValue = entryValue.toString();
                if (entryValue instanceof BigDecimal)
                    entryValue = entryValue.toString();
                ConfigValue value = ConfigValueFactory.fromAnyRef(entryValue);
                if (configEntry.getDescription() != null)
                    value = value.withOrigin(value.origin().withComments(Collections.singletonList(configEntry.getDescription())));
                config = config.withValue(configEntry.getPath(), value);
            }
        }

        this.writeIfNecessary(config, true);
    }

    @Override
    public void strip() throws IOException {
        Config config = this.config;
        if (config == null)
            throw new IllegalStateException();
        Util.createFileAndPath(this.file);

        List<String> pathsToRemove = new ArrayList<>();
        this.resolvePathsToStrip("", config.root(), this.getPaths(), pathsToRemove);
        for (String path : pathsToRemove)
            config = config.withoutPath(path);

        this.writeIfNecessary(config, false);
    }

    @Override
    public void close() {
        if (this.config != null)
            this.config = null;
    }

    protected void writeIfNecessary(@NotNull Config config, boolean setDescription) throws IOException {
        if (config != this.config) {
            ConfigObject root = setDescription ? config.root().withOrigin(this.config.root().origin().withComments(this.getDescription() == null ? null : Collections.singletonList(this.getDescription()))) : config.root();
            String output = root.render(RENDER_OPTIONS);
            try (FileWriter fileWriter = new FileWriter(this.file)) {
                fileWriter.write(output);
            }
            this.config = config;
        }
    }

    protected int resolvePathsToStrip(@NotNull String path, @NotNull ConfigObject node, @NotNull Set<String> paths, @NotNull List<String> toRemove) {
        int numRemoved = 0;
        for (Map.Entry<String, ConfigValue> entry : node.entrySet()) {
            String fieldPath = (path.isEmpty() ? "" : path + ".") + entry.getKey();
            if (entry.getValue() instanceof ConfigObject) {
                if (paths.contains(fieldPath))
                    continue;
                ConfigObject child = (ConfigObject) entry.getValue();
                int numChildrenRemoved = this.resolvePathsToStrip(fieldPath, child, paths, toRemove);
                int numChildren = child.entrySet().size();
                if (numChildren == 0 || numChildren == numChildrenRemoved) {
                    toRemove.add(fieldPath);
                    numRemoved++;
                }
            } else if (!paths.contains(fieldPath)) {
                toRemove.add(fieldPath);
                numRemoved++;
            }
        }
        return numRemoved;
    }

    /**
     * Creates a new {@link HoconConfig} instance.
     *
     * @param file        The hocon file. The file will be created automatically if it does not already exist.
     * @param description The description (comment) of this config file.
     * @return A new {@link HoconConfig} instance.
     */
    public static io.github.almightysatan.jaskl.Config of(@NotNull File file, @Nullable String description) {
        return new HoconConfig(file, description);
    }

    /**
     * Creates a new {@link HoconConfig} instance.
     *
     * @param file The hocon file. The file will be created automatically if it does not already exist.
     * @return A new {@link HoconConfig} instance.
     */
    public static io.github.almightysatan.jaskl.Config of(@NotNull File file) {
        return new HoconConfig(file, null);
    }
}
