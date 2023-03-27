package com.github.almightysatan.jaskl.entries;

import com.github.almightysatan.jaskl.Config;
import com.github.almightysatan.jaskl.ConfigEntry;
import com.github.almightysatan.jaskl.InvalidTypeException;
import com.github.almightysatan.jaskl.impl.WritableConfigEntryImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BooleanConfigEntry extends WritableConfigEntryImpl<Boolean> {

    private BooleanConfigEntry(@NotNull Config config, @NotNull String path, @Nullable String description, @NotNull Boolean defaultValue) {
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

    public static ConfigEntry<Boolean> of(@NotNull Config config, @NotNull String path, @Nullable String description, boolean defaultValue) {
        return new BooleanConfigEntry(config, path, description, defaultValue);
    }
}
