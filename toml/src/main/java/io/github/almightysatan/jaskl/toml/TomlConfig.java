/*
 * JASKL - Just Another Simple Konfig Library
 * Copyright (C) 2023 LeStegii, Almighty-Satan
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 * USA
 */

package io.github.almightysatan.jaskl.toml;

import com.fasterxml.jackson.dataformat.toml.TomlMapper;
import com.fasterxml.jackson.dataformat.toml.TomlWriteFeature;
import io.github.almightysatan.jaskl.Config;
import io.github.almightysatan.jaskl.ExceptionHandler;
import io.github.almightysatan.jaskl.Resource;
import io.github.almightysatan.jaskl.jackson.JacksonConfigImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class TomlConfig extends JacksonConfigImpl {

    private TomlConfig(@NotNull Resource resource, @Nullable ExceptionHandler exceptionHandler) {
        super(TomlMapper.builder().enable(TomlWriteFeature.FAIL_ON_NULL_WRITE).build(), resource, null, exceptionHandler);
    }

    /**
     * Creates a new {@link TomlConfig} instance.
     *
     * @param resource         A resource containing a toml configuration. The resource will be created automatically if it does
     *                         not already exist and {@link #isReadOnly()} is {@code false}.
     * @param exceptionHandler The {@link ExceptionHandler}
     * @return A new {@link TomlConfig} instance.
     */
    public static Config of(@NotNull Resource resource, @Nullable ExceptionHandler exceptionHandler) {
        return new TomlConfig(resource, exceptionHandler);
    }

    /**
     * Creates a new {@link TomlConfig} instance.
     *
     * @param resource A resource containing a toml configuration. The resource will be created automatically if it does
     *                 not already exist and {@link #isReadOnly()} is {@code false}.
     * @return A new {@link TomlConfig} instance.
     */
    public static Config of(@NotNull Resource resource) {
        return of(resource, null);
    }

    /**
     * Creates a new {@link TomlConfig} instance.
     *
     * @param file        The toml file. The file will be created automatically if it does not already exist.
     * @param description The description (comment) of this config file.
     * @return A new {@link TomlConfig} instance.
     * @deprecated This config implementation does not support comments. Use {@link #of(File)} instead.
     */
    @Deprecated
    public static Config of(@NotNull File file, @Nullable String description) {
        return of(file);
    }

    /**
     * Creates a new {@link TomlConfig} instance.
     *
     * @param file             The toml file. The file will be created automatically if it does not already exist.
     * @param exceptionHandler The {@link ExceptionHandler}
     * @return A new {@link TomlConfig} instance.
     */
    public static Config of(@NotNull File file, @Nullable ExceptionHandler exceptionHandler) {
        return of(Resource.of(file), exceptionHandler);
    }

    /**
     * Creates a new {@link TomlConfig} instance.
     *
     * @param file The toml file. The file will be created automatically if it does not already exist.
     * @return A new {@link TomlConfig} instance.
     */
    public static Config of(@NotNull File file) {
        return of(Resource.of(file));
    }
}
