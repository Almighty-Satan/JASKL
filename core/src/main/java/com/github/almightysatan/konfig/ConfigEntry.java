package com.github.almightysatan.konfig;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ConfigEntry<T> {

    /**
     * Returns the path leading to this ConfigEntry's value.
     * @return the path of this ConfigEntry
     */
    @NotNull
    String getPath();

    /**
     * Returns the description of this ConfigEntry.
     *
     * @return the description of this ConfigEntry
     */
    @Nullable
    String getDescription();

    /**
     * Returns the value of this ConfigEntry.
     *
     * @return the value of this ConfigEntry
     */
    @NotNull
    T getValue();

    /**
     * Returns the value of this ConfigEntry.
     *
     * @return the value of this ConfigEntry
     */
    @NotNull
    T getDefaultValue();

    /**
     * Updates the value of this ConfigEntry.
     */
    void setValue(@NotNull T value);

}
