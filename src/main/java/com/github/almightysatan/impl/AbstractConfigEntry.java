package com.github.almightysatan.impl;

import com.github.almightysatan.ConfigEntry;
import com.github.almightysatan.InvalidTypeException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class AbstractConfigEntry<T> implements ConfigEntry<T> {

    private final String path;
    private final String description;
    private final T defaultValue;

    public AbstractConfigEntry(@NotNull String path, @Nullable String description, @NotNull T defaultValue) {
        this.path = Objects.requireNonNull(path);
        if (path.isEmpty())
            throw new IllegalArgumentException("path cannot be empty!");
        this.description = description;
        this.defaultValue = Objects.requireNonNull(defaultValue);
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
    public @NotNull T getDefaultValue() {
        return this.defaultValue;
    }

    protected @NotNull T checkType(@NotNull Object type) {
        if (this.getDefaultValue().getClass() != type.getClass())
            throw new InvalidTypeException(this.getPath(), this.getDefaultValue().getClass(), type.getClass());
        return (T) type;
    }
}
