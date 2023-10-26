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

package io.github.almightysatan.jaskl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Thrown if a config entry fails validation
 */
public class ValidationException extends RuntimeException {

    private final String path;
    private final String errorMessage;

    public ValidationException(@NotNull String errorMessage) {
        this(errorMessage, (Throwable) null);
    }

    public ValidationException(@NotNull String errorMessage, @Nullable Throwable cause) {
        super(String.format("Config error: unknown entry %s", Objects.requireNonNull(errorMessage)), cause);
        this.path = null;
        this.errorMessage = errorMessage;
    }

    public ValidationException(@NotNull String path, @NotNull ValidationException exception) {
        super(String.format("Config error: %s %s", Objects.requireNonNull(path), Objects.requireNonNull(exception.errorMessage)), exception.getCause());
        this.path = path;
        this.errorMessage = exception.errorMessage;
    }

    /**
     * Returns the path of the config entry that failed validation.
     *
     * @return The path of the config entry
     */
    public @Nullable String path() {
        return this.path;
    }

    /**
     * Returns the error message
     *
     * @return The error message
     */
    public @NotNull String errorMessage() {
        return this.errorMessage;
    }
}
