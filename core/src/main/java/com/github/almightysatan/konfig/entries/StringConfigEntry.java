package com.github.almightysatan.konfig.entries;

import com.github.almightysatan.konfig.Config;
import com.github.almightysatan.konfig.ConfigEntry;
import com.github.almightysatan.konfig.impl.WritableConfigEntryImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StringConfigEntry extends WritableConfigEntryImpl<String> {

    private StringConfigEntry(@NotNull Config config, @NotNull String path, @Nullable String description, @NotNull String defaultValue) {
        super(config, path, description, defaultValue);
    }

    public static ConfigEntry<String> of(@NotNull Config config, @NotNull String path, @Nullable String description, @NotNull String defaultValue) {
        return new StringConfigEntry(config, path, description, defaultValue);
    }
}
