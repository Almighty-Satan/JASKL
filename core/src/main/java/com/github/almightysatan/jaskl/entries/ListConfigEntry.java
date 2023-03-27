package com.github.almightysatan.jaskl.entries;

import com.github.almightysatan.jaskl.Config;
import com.github.almightysatan.jaskl.ConfigEntry;
import com.github.almightysatan.jaskl.InvalidTypeException;
import com.github.almightysatan.jaskl.impl.WritableConfigEntryImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ListConfigEntry<T> extends WritableConfigEntryImpl<List<T>> {

    private ListConfigEntry(@NotNull Config config, @NotNull String path, @Nullable String description, @NotNull List<T> defaultValue) {
        super(config, path, description, defaultValue);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected @NotNull List<T> checkType(@NotNull Object type) {
        if (type instanceof List)
            return (List<T>) type;

        throw new InvalidTypeException(getPath(), List.class, type.getClass());

    }

    public static <T> ConfigEntry<List<T>> of(@NotNull Config config, @NotNull String path, @Nullable String description, @NotNull List<T> defaultValue) {
        return new ListConfigEntry<>(config, path, description, defaultValue);
    }
}
