package com.github.almightysatan.jaskl.toml;

import com.fasterxml.jackson.dataformat.toml.TomlMapper;
import com.fasterxml.jackson.dataformat.toml.TomlWriteFeature;
import com.github.almightysatan.jaskl.Config;
import com.github.almightysatan.jaskl.jackson.JacksonConfigImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class TomlConfig extends JacksonConfigImpl {

    private TomlConfig(@NotNull File file, @Nullable String description) {
        super(TomlMapper.builder().enable(TomlWriteFeature.FAIL_ON_NULL_WRITE).build(), file, description);
    }

    public static Config of(@NotNull File file, @Nullable String description) {
        return new TomlConfig(file, description);
    }
}
