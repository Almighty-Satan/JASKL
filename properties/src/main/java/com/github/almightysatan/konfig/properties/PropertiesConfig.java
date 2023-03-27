package com.github.almightysatan.konfig.properties;

import com.github.almightysatan.konfig.entries.ListConfigEntry;
import com.github.almightysatan.konfig.impl.ConfigImpl;
import com.github.almightysatan.konfig.impl.Util;
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

        try (FileReader reader = new FileReader(file)) {
            config.load(reader);
        }

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
        Util.createFileAndPath(file);

        for (WritableConfigEntry<?> configEntry : this.getCastedValues()) {
            this.config.setProperty(configEntry.getPath(), configEntry.getValue().toString());
        }

        try (FileWriter writer = new FileWriter(file)){
            this.config.store(writer, this.getDescription() == null ? "" : this.getDescription());
        }
    }

    @Override
    public void populate() throws IllegalStateException {

    }

    @Override
    public void strip() throws IOException {
        if (this.config == null)
            throw new IllegalStateException();

        Properties write = new Properties();

        for (WritableConfigEntry<?> configEntry : this.getCastedValues()) {
            write.setProperty(configEntry.getPath(), configEntry.getValue().toString());
        }

        this.config = write;

        this.write();
    }

    @Override
    public void close() {
        if (this.config != null)
            this.config = null;
    }
}
