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

package io.github.almightysatan.jaskl.json;

import com.fasterxml.jackson.databind.json.JsonMapper;
import io.github.almightysatan.jaskl.Resource;
import io.github.almightysatan.jaskl.jackson.JacksonConfigImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class JsonConfig extends JacksonConfigImpl {

    private JsonConfig(@NotNull Resource resource, @Nullable String description) {
        super(new JsonMapper(), resource, description);
    }

    /**
     * Creates a new {@link JsonConfig} instance.
     *
     * @param resource A resource containing a json configuration. The resource will be created automatically if it does
     *                 not already exist and {@link #isReadOnly()} is {@code false}.
     * @return A new {@link JsonConfig} instance.
     */
    public static JsonConfig of(@NotNull Resource resource) {
        return new JsonConfig(resource, null);
    }

    /**
     * Creates a new {@link JsonConfig} instance.
     *
     * @param file               The json file. The file will be created automatically if it does not already exist.
     * @param ignoredDescription Unsupported
     * @return A new {@link JsonConfig} instance.
     * @deprecated This config implementation does not support comments. Use {@link #of(File)} instead.
     */
    @Deprecated
    public static JsonConfig of(@NotNull File file, @Nullable String ignoredDescription) {
        return of(file);
    }

    /**
     * Creates a new {@link JsonConfig} instance.
     *
     * @param file The json file. The file will be created automatically if it does not already exist.
     * @return A new {@link JsonConfig} instance.
     */
    public static JsonConfig of(@NotNull File file) {
        return of(Resource.of(file));
    }
}
