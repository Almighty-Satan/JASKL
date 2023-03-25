package com.github.almightysatan.entries;

import com.github.almightysatan.Config;
import com.github.almightysatan.impl.WritableConfigEntryImpl;
import com.github.almightysatan.InvalidTypeException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;

public class ListConfigEntry<T> extends WritableConfigEntryImpl<List<T>> {

    public ListConfigEntry(@NotNull Config config, @NotNull String path, @Nullable String description, @NotNull List<T> defaultValue) {
        super(config, path, description, defaultValue);
    }

    @Override
    protected @NotNull List<T> checkType(@NotNull Object type) {
        if (type instanceof List)
            return (List<T>) type;

        throw new InvalidTypeException(getPath(), List.class, type.getClass());

    }

}
