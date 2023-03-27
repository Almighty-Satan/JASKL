package com.github.almightysatan.konfig.entries;

import com.github.almightysatan.konfig.Config;
import com.github.almightysatan.konfig.ConfigEntry;
import com.github.almightysatan.konfig.impl.ConfigEntryImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EnumConfigEntry<E extends Enum<E>> extends ConfigEntryImpl<E> {

    private final Class<E> enumClass;
    private final ConfigEntry<String> internal;

    public EnumConfigEntry(@NotNull Config config, @NotNull String path, @Nullable String description, @NotNull E defaultValue) {
        super(path, description, defaultValue);
        this.enumClass = (Class<E>) defaultValue.getClass();
        this.internal = StringConfigEntry.of(config, path, description, defaultValue.toString());
    }

    public static <T extends Enum<T>> ConfigEntry<T> of(@NotNull Config config, @NotNull String path, @Nullable String description, T defaultValue) {
        return new EnumConfigEntry(config, path, description, defaultValue);
    }

    @Override
    public @NotNull E getValue() {
        return Enum.valueOf(this.enumClass, this.internal.getValue());
    }

    @Override
    public void setValue(@NotNull Enum value) {
        this.internal.setValue(value.toString());
    }
}
