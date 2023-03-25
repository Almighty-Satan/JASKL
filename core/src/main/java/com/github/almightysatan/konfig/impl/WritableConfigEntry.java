package com.github.almightysatan.konfig.impl;

import com.github.almightysatan.konfig.ConfigEntry;
import org.jetbrains.annotations.NotNull;

public interface WritableConfigEntry<T> extends ConfigEntry<T> {

    void putValue(@NotNull Object value);

    boolean isModified();
}
