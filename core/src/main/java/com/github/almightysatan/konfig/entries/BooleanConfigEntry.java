package com.github.almightysatan.konfig.entries;

import com.github.almightysatan.konfig.Config;
import com.github.almightysatan.konfig.InvalidTypeException;
import com.github.almightysatan.konfig.impl.WritableConfigEntryImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BooleanConfigEntry extends WritableConfigEntryImpl<Boolean> {

    public BooleanConfigEntry(@NotNull Config config, @NotNull String path, @Nullable String description, @NotNull Boolean defaultValue) {
        super(config, path, description, defaultValue);
    }

    @Override
    protected @NotNull Boolean checkType(@NotNull Object type) {
        if (type instanceof Boolean)
            return (Boolean) type;

        if (type instanceof Integer) {
            return (Integer) type > 0;
        }

        if (type instanceof String) {
            try {
                return Boolean.valueOf((String) type);
            } catch (NumberFormatException ignored) {}
        }

        throw new InvalidTypeException(getPath(), Boolean.class, type.getClass());

    }
}
