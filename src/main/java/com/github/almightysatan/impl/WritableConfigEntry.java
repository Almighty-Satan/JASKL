package com.github.almightysatan.impl;

import com.github.almightysatan.ConfigEntry;
import org.jetbrains.annotations.NotNull;

public interface WritableConfigEntry<T> extends ConfigEntry<T> {

    void putValue(@NotNull Object value);

    boolean isModified();
}
