package com.github.almightysatan.impl.hocon;

import com.github.almightysatan.ConfigEntry;
import com.github.almightysatan.impl.GenericConfigEntry;
import com.github.almightysatan.impl.ConfigImpl;
import com.typesafe.config.*;

import java.io.File;

public class HoconConfig extends ConfigImpl {

    private static final ConfigParseOptions PARSE_OPTIONS = ConfigParseOptions.defaults().setSyntax(ConfigSyntax.CONF)
            .setAllowMissing(false).setIncluder(new NopIncluder());

    private final File file;
    private Config config;

    public HoconConfig(File file, String description) {
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
            GenericConfigEntry<?> configEntry = (GenericConfigEntry<?>) uncastedConfigEntry;
            try {
                Object value = this.config.getValue(configEntry.getPath()).unwrapped();
                configEntry.putValue(value);
            } catch (ConfigException.Missing ignored) {}
        }
    }

    @Override
    public void write() {

    }

    @Override
    public void writeMissingEntries() {

    }

    @Override
    public void cleanUp() {

    }

    @Override
    public void close() {
        if (this.config != null)
            this.config = null;
    }
}
