package com.github.almightysatan.konfig.properties;

import com.github.almightysatan.konfig.Config;
import com.github.almightysatan.konfig.entries.ListConfigEntry;
import com.github.almightysatan.konfig.impl.ConfigImpl;
import com.github.almightysatan.konfig.impl.Util;
import com.github.almightysatan.konfig.impl.WritableConfigEntry;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class PropertiesConfig extends ConfigImpl {

    private final File file;
    private Properties config;

    private PropertiesConfig(File file, String description) {
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

        readFromFile();


    }

    @Override
    public void write() throws IOException {
        if (this.config == null)
            throw new IllegalStateException();
        Util.createFileAndPath(file);

        for (WritableConfigEntry<?> configEntry : this.getCastedValues()) {
            this.config.setProperty(configEntry.getPath(), configEntry.getValue().toString());
        }

        writeToFile();
        populateEntries();
    }

    @Override
    public void strip() throws IOException {
        if (this.config == null)
            throw new IllegalStateException();

        Properties stripped = new Properties();

        for (Object key : this.config.keySet()) {
            if (!this.getEntries().containsKey(key))
                continue;
            stripped.setProperty((String) key, this.config.getProperty((String) key));
        }

        this.config = stripped;

        writeToFile();

    }

    @Override
    public void close() {
        if (this.config != null)
            this.config = null;
    }

    public static Config of(File file, String description) {
        return new PropertiesConfig(file, description);
    }

    /**
     * Takes the current property instance and saves it to the file
     * @throws IOException
     */
    private void writeToFile() throws IOException {
        try (FileWriter writer = new FileWriter(file)){
            this.config.store(writer, this.getDescription() == null ? "" : this.getDescription());
        }
    }

    /**
     * Populates the property instance with the values from the file
     * @throws IOException
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
            if (configEntry instanceof ListConfigEntry)
                throw new UnsupportedOperationException("Lists are not supported in Property Configs.");
            Object value = this.config.get(configEntry.getPath());
            configEntry.putValue(value == null ? configEntry.getDefaultValue() : value);
        }
    }
}
