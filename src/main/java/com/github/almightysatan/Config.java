package com.github.almightysatan;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface Config {

    /**
     * Loads the config from a storage location.
     *
     * @throws IllegalStateException if called multiple times.
     */
    void load() throws IOException, IllegalStateException;

    /**
     * Reloads the config.
     * Assumes that {@link Config#load()} has been called already.
     * No files/data storage locations will be created or initialized.
     *
     * @throws IllegalStateException if {@link Config#load()} hasn't been called.
     */
    void reload() throws IOException, IllegalStateException;

    /**
     * Saves the configuration to it's corresponding data storage location.
     */
    void write() throws IOException;

    /**
     *
     */
    void populate() throws IOException;

    /**
     * Cleans up dead entries from the storage location.
     * An entry is dead if no {@link ConfigEntry} references its path.
     */
    void strip() throws IOException;

    /**
     * Closes the corresponding data storage location.
     */
    void close();

    /**
     * Returns the description of this config.
     *
     * @return the description of this config
     */
    @Nullable
    String getDescription();

    /**
     * Returns a map of all paths with their config entries.
     *
     * @return a map of all paths with their config entries
     */
    @NotNull
    Map<String, ConfigEntry<?>> getEntries();

    /**
     * Returns a set of all the paths of the config entries.
     *
     * @return a set of all the paths of the config entries
     */
    @NotNull
    default Set<String> getPaths() {
        return getEntries().keySet();
    }

    /**
     * Returns a collection of all config entries.
     *
     * @return a collection of all config entries
     */
    @NotNull
    default Collection<ConfigEntry<?>> getValues() {
        return getEntries().values();
    }

}
