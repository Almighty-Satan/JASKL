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

package io.github.almightysatan.jaskl.hocon;

import io.github.almightysatan.jaskl.ConfigEntry;
import io.github.almightysatan.jaskl.impl.ConfigImpl;
import io.github.almightysatan.jaskl.impl.WritableConfigEntry;
import com.typesafe.config.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Objects;

/**
 * A read-only hocon config implementation.
 * Since the underlying configuration api doesn't support writing without weird hacks,
 * writing is currently unsupported.
 */
public class HoconConfig extends ConfigImpl {

    private static final ConfigParseOptions PARSE_OPTIONS = ConfigParseOptions.defaults().setSyntax(ConfigSyntax.CONF)
            .setAllowMissing(false).setIncluder(new NopIncluder());

    private final File file;
    private Config config;

    private HoconConfig(@NotNull File file, @Nullable String description) {
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
            } catch (ConfigException.Missing ignored) {}
        }
    }

    @Override
    public void write() {
        throw new UnsupportedOperationException("Hocon configs do not support writing yet.");
    }

    @Override
    public void strip() {
        throw new UnsupportedOperationException("Hocon configs do not support writing yet.");
    }

    @Override
    public void close() {
        if (this.config != null)
            this.config = null;
    }

    /**
     * Creates a new {@link HoconConfig} instance.
     *
     * @param file The hocon file. The file will be created automatically if it does not already exist.
     * @param description The description (comment) of this config file.
     * @return A new {@link HoconConfig} instance.
     */
    @Deprecated
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
