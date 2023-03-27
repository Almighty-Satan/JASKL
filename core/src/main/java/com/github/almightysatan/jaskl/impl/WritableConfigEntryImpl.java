package com.github.almightysatan.jaskl.impl;

import com.github.almightysatan.jaskl.Config;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class WritableConfigEntryImpl<T> extends ConfigEntryImpl<T> implements WritableConfigEntry<T> {

    private T value;
    private boolean modified = true; // true by default because Config#write should write the entry to the config if it does not exist

    public WritableConfigEntryImpl(@NotNull Config config, @NotNull String path, @Nullable String description, @NotNull T defaultValue) {
        super(path, description, defaultValue);
        Objects.requireNonNull(config);
        this.value = defaultValue;
        ((ConfigImpl) config).registerEntry(this);
    }

    @Override
    public @NotNull T getValue() {
        return this.value;
    }

    @Override
    public void setValue(@NotNull T value) {
        Objects.requireNonNull(value);
        T parsedValue = this.checkType(value);
        if (parsedValue.equals(this.getValue()))
            return;
        this.value = parsedValue;
        this.modified = true;
    }

    @Override
    public void putValue(@NotNull Object value) {
        Objects.requireNonNull(value);
        this.value = this.checkType(value);
        this.modified = false;
    }

    @Override
    public boolean isModified() {
        return this.modified;
    }
}
