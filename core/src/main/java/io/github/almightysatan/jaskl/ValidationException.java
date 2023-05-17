/*
 * JASKL - Just Another Simple Konfig Library
 * Copyright (C) 2023 UeberallGebannt, Almighty-Satan
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

public class ValidationException extends RuntimeException {

    private final String path;
    private final String errorMessage;

    public ValidationException(String errorMessage) {
        super(String.format("Config error: unknown entry %s", errorMessage));
        this.path = null;
        this.errorMessage = errorMessage;
    }

    public ValidationException(String path, String errorMessage) {
        super(String.format("Config error: %s %s", path, errorMessage));
        this.path = path;
        this.errorMessage = errorMessage;
    }

    public String getPath() {
        return this.path;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }
}
