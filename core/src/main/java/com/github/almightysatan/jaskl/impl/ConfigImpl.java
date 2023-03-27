package com.github.almightysatan.jaskl.impl;

import com.github.almightysatan.jaskl.Config;
import com.github.almightysatan.jaskl.ConfigEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class ConfigImpl implements Config {

    private final String description;
    private final Map<String, ConfigEntry<?>> entries = new HashMap<>();

    public ConfigImpl(@Nullable String description) {
        this.description = description;
    }

    public void registerEntry(@NotNull ConfigEntry<?> entry) {
        Objects.requireNonNull(entry);
        if (this.entries.containsKey(entry.getPath()))
            throw new IllegalArgumentException("Path already registered!");
        this.entries.put(entry.getPath(), entry);
    }

    @Nullable
    @Override
    public String getDescription() {
        return description;
    }

    @NotNull
    @Override
    public Map<String, ConfigEntry<?>> getEntries() {
        return entries;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected Collection<WritableConfigEntry<?>> getCastedValues() {
        return (Collection<WritableConfigEntry<?>>) (Collection) getEntries().values();
    }
}
