package com.github.almightysatan.impl;

import com.github.almightysatan.Config;
import com.github.almightysatan.ConfigEntry;
import com.github.almightysatan.InvalidTypeException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class GenericConfigEntry<T> implements ConfigEntry<T> {

    private final String path;
    private final String description;
    private final T defaultValue;
    private T value;
    private boolean modified;

    public GenericConfigEntry(Config config, String path, String description, T defaultValue) {
        Objects.requireNonNull(config);
        Objects.requireNonNull(path);
        Objects.requireNonNull(defaultValue);
        this.path = path;
        this.description = description;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        ((ConfigImpl) config).registerEntry(this);
    }

    @Override
    public @NotNull String getPath() {
        return this.path;
    }

    @Override
    public @Nullable String getDescription() {
        return this.description;
    }

    @Override
    public @NotNull T getValue() {
        return this.value;
    }

    @Override
    public @NotNull T getDefaultValue() {
        return this.defaultValue;
    }

    @Override
    public void setValue(@NotNull T value) {
        Objects.requireNonNull(value);
        this.value = checkType(value);
        this.modified = true;
    }

    @SuppressWarnings("unchecked")
    public void putValue(Object value) {
        Objects.requireNonNull(value);
        this.value = checkType((T) value);
        this.modified = false;
    }

    protected @NotNull T checkType(@NotNull T type) {
        if (this.defaultValue.getClass() != this.value.getClass())
            throw new InvalidTypeException(this.path, this.defaultValue.getClass(), this.value.getClass());
        return type;
    }

    public boolean isModified() {
        return this.modified;
    }
}
