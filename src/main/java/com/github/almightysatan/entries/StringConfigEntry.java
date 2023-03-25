package com.github.almightysatan.entries;

import com.github.almightysatan.Config;
import com.github.almightysatan.impl.WritableConfigEntryImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StringConfigEntry extends WritableConfigEntryImpl<String> {

    public StringConfigEntry(@NotNull Config config, @NotNull String path, @Nullable String description, @NotNull String defaultValue) {
        super(config, path, description, defaultValue);
    }

}
