package com.github.almightysatan.impl.toml;

import com.github.almightysatan.ConfigEntry;
import com.github.almightysatan.impl.ConfigImpl;
import com.github.almightysatan.impl.WritableConfigEntry;
import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class TomlConfig extends ConfigImpl {

    private final File file;
    private Toml config;


    private final static Method GET_VALUE_METHOD;

    static {
        try {
            GET_VALUE_METHOD = Toml.class.getDeclaredMethod("get", String.class);
            GET_VALUE_METHOD.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public TomlConfig(File file, String description) {
        super(description);
        this.file = file;
    }

    @Override
    public void load() throws IOException, IllegalStateException {
        if (this.config != null)
            throw new IllegalStateException();
        if (!file.exists()) {
            this.config = new Toml();
            return;
        }
        config = new Toml().read(file);
        reload();
    }

    @Override
    public void reload() throws IOException, IllegalStateException {
        if (this.config == null)
            throw new IllegalStateException();
        for (ConfigEntry<?> uncastedConfigEntry : this.getValues()) {
            WritableConfigEntry<?> configEntry = (WritableConfigEntry<?>) uncastedConfigEntry;
            try {
                Object value = GET_VALUE_METHOD.invoke(this.config, configEntry.getPath());
                if (value != null)
                    configEntry.putValue(value);
            } catch (InvocationTargetException | IllegalAccessException ignored) {
                System.out.println(ignored.getMessage());
            }
        }

    }

    @Override
    public void write() throws IOException {
        Toml write = new Toml();
        if (config != null)
            write.read(config);
        for (ConfigEntry<?> uncastedConfigEntry : this.getValues()) {
            Map map = new HashMap();
            map.put(uncastedConfigEntry.getPath(), uncastedConfigEntry.getValue());
            write.read(new TomlWriter().write(map));
        }
        new TomlWriter().write(write, file);
    }

    @Override
    public void populate() throws IOException {
        Toml write = new Toml();
        if (config != null)
            write.read(config);
        for (ConfigEntry<?> uncastedConfigEntry : this.getValues()) {
            if (write.contains(uncastedConfigEntry.getPath()))
                continue;
            Map map = new HashMap();
            map.put(uncastedConfigEntry.getPath(), uncastedConfigEntry.getValue());
            write.read(new TomlWriter().write(map));
        }
        new TomlWriter().write(write, file);
    }

    @Override
    public void strip() throws IOException {
        Toml toml = new Toml();
        for (Map.Entry<String, ?> entry : this.config.entrySet()) {
            if (this.getEntries().containsKey(entry.getKey())) {
                ConfigEntry configEntry = this.getEntries().get(entry.getKey());
                Map map = new HashMap();
                map.put(configEntry.getPath(), configEntry.getValue());
                toml.read(new TomlWriter().write(map));
            }
        }
        new TomlWriter().write(toml, file);
    }

    @Override
    public void close() {

    }
}
