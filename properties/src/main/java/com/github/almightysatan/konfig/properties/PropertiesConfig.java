package com.github.almightysatan.konfig.properties;

import com.github.almightysatan.konfig.entries.ListConfigEntry;
import com.github.almightysatan.konfig.impl.ConfigImpl;
import com.github.almightysatan.konfig.impl.WritableConfigEntry;

import java.io.*;
import java.util.Properties;

public class PropertiesConfig extends ConfigImpl {

    private final File file;
    private Properties config;

    public PropertiesConfig(File file, String description) {
        super(description);
        this.file = file;
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

        FileReader reader = new FileReader(file);
        config.load(reader);

        for (WritableConfigEntry<?> configEntry : this.getCastedValues()) {
            if (configEntry instanceof ListConfigEntry)
                throw new UnsupportedOperationException("Lists are not supported in Property Configs.");
            Object value = this.config.get(configEntry.getPath());
            configEntry.putValue(value == null ? configEntry.getDefaultValue() : value);
        }
    }

    @Override
    public void write() throws IOException {
        if (this.config == null)
            throw new IllegalStateException();
        if (!this.file.exists()) {
            if (!this.file.getParentFile().exists())
                this.file.getParentFile().mkdirs();
            this.file.createNewFile();
        }
        for (WritableConfigEntry<?> configEntry : this.getCastedValues()) {
            this.config.setProperty(configEntry.getPath(), configEntry.getValue().toString());
        }
        this.config.store(new FileWriter(file), this.getDescription() == null ? "" : this.getDescription());
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
}
