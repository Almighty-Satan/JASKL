package com.github.almightysatan.impl.jackson.toml;

import com.fasterxml.jackson.dataformat.toml.TomlMapper;
import com.fasterxml.jackson.dataformat.toml.TomlWriteFeature;
import com.github.almightysatan.impl.jackson.JacksonConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class TomlConfig extends JacksonConfig {

    public TomlConfig(@NotNull File file, @Nullable String description) {
        super(TomlMapper.builder().enable(TomlWriteFeature.FAIL_ON_NULL_WRITE).build(), file, description);
    }
}
