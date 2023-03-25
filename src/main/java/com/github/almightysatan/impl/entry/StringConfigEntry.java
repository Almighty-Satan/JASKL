package com.github.almightysatan.impl.entry;

import com.github.almightysatan.Config;
import com.github.almightysatan.GenericConfigEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StringConfigEntry extends GenericConfigEntry<String> {

    public StringConfigEntry(@NotNull Config config, @NotNull String path, @Nullable String description, @NotNull String defaultValue) {
        super(config, path, description, defaultValue);
    }

}
