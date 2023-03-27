package com.github.almightysatan.konfig.impl;

import com.github.almightysatan.konfig.Config;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class WritableConfigEntryImpl<T> extends ConfigEntryImpl<T> implements WritableConfigEntry<T> {

    private T value;
    private boolean modified;

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
        this.value = this.checkType(value);
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
