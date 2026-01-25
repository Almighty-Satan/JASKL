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

/**
 * A builder that can be used to create new {@link Config} instances.
 *
 * @param <T> The type of the resulting {@link Config}
 * @param <U> The type of this {@link ConfigBuilder}
 */
public interface ConfigBuilder<T, U> {

    /**
     * Returns a new {@link Config}.
     *
     * @return A new config
     */
    @NotNull T build();

    /**
     * Sets the {@link ExceptionHandler} for the resulting config.
     *
     * @param exceptionHandler An {@link ExceptionHandler}
     * @return This builder
     */
    @NotNull U withExceptionHandler(@Nullable ExceptionHandler exceptionHandler);

    /**
     * A builder that can be used to create new {@link Config} instances that support comments.
     *
     * @param <T> The type of the resulting {@link Config}
     * @param <U> The type of this {@link ConfigBuilder}
     */
    interface DescriptionConfigBuilder<T, U> extends ConfigBuilder<T, U> {

        /**
         * Sets the description for the resulting config.
         *
         * @param description The description
         * @return This builder
         */
        @NotNull U withDescription(@Nullable String description);

        /**
         * Sets the {@link DescriptionFormatter} for the resulting config.
         *
         * @param descriptionFormatter The {@link DescriptionFormatter}
         * @return This builder
         */
        @NotNull U withDescriptionFormatter(@Nullable DescriptionFormatter descriptionFormatter);
    }
}
