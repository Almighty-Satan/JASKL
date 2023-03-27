package com.github.almightysatan.jaskl.impl;

import com.github.almightysatan.jaskl.ConfigEntry;
import org.jetbrains.annotations.NotNull;

public interface WritableConfigEntry<T> extends ConfigEntry<T> {

    void putValue(@NotNull Object value);

    boolean isModified();
}
