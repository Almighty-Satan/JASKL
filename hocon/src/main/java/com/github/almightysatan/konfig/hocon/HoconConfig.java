package com.github.almightysatan.konfig.hocon;

import com.github.almightysatan.konfig.ConfigEntry;
import com.github.almightysatan.konfig.impl.ConfigImpl;
import com.github.almightysatan.konfig.impl.WritableConfigEntry;
import com.typesafe.config.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

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

    private HoconConfig(File file, String description) {
        super(description);
        this.file = file;
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
    public void populate() {
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

    public static com.github.almightysatan.konfig.Config of(@NotNull File file, @Nullable String description) {
        return new HoconConfig(file, description);
    }
}
