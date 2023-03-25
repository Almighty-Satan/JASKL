package com.github.almightysatan;

import com.github.almightysatan.impl.AbstractConfigEntry;
import com.github.almightysatan.impl.ConfigImpl;
import com.github.almightysatan.impl.WritableConfigEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class GenericConfigEntry<T> extends AbstractConfigEntry<T> implements WritableConfigEntry<T> {

    private T value;
    private boolean modified;

    public GenericConfigEntry(@NotNull Config config, @NotNull String path, @Nullable String description, @NotNull T defaultValue) {
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
    @SuppressWarnings("unchecked")
    public void putValue(@NotNull Object value) {
        Objects.requireNonNull(value);
        this.value = this.checkType((T) value);
        this.modified = false;
    }

    @Override
    public boolean isModified() {
        return this.modified;
    }
}
