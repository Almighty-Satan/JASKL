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

package io.github.almightysatan.jaskl.impl;

import io.github.almightysatan.jaskl.ConfigBuilder;
import io.github.almightysatan.jaskl.DescriptionFormatter;
import io.github.almightysatan.jaskl.ExceptionHandler;
import io.github.almightysatan.jaskl.Resource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.URL;
import java.util.Objects;

public abstract class ConfigBuilderImpl<T, U> implements ConfigBuilder<T, U> {

    protected ExceptionHandler exceptionHandler;

    @SuppressWarnings("unchecked")
    @Override
    public @NotNull U withExceptionHandler(@Nullable ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        return (U) this;
    }

    public abstract static class ResourceConfigBuilderImpl<T, U> extends ConfigBuilderImpl<T, U> {

        protected final Resource resource;

        public ResourceConfigBuilderImpl(@NotNull Resource resource) {
            Objects.requireNonNull(resource);
            this.resource = resource;
        }

        public ResourceConfigBuilderImpl(@NotNull File file) {
            this(Resource.of(file));
        }

        public ResourceConfigBuilderImpl(@NotNull URL url) {
            this(Resource.of(url));
        }
    }

    public abstract static class DescriptionConfigBuilderImpl<T, U> extends ResourceConfigBuilderImpl<T, U> implements DescriptionConfigBuilder<T, U> {

        protected String description;
        protected DescriptionFormatter descriptionFormatter;

        public DescriptionConfigBuilderImpl(@NotNull Resource resource) {
            super(resource);
        }

        public DescriptionConfigBuilderImpl(@NotNull File file) {
            super(file);
        }

        public DescriptionConfigBuilderImpl(@NotNull URL url) {
            super(url);
        }

        @SuppressWarnings("unchecked")
        @Override
        public @NotNull U withDescription(@Nullable String description) {
            this.description = description;
            return (U) this;
        }

        @SuppressWarnings("unchecked")
        @Override
        public @NotNull U withDescriptionFormatter(@Nullable DescriptionFormatter descriptionFormatter) {
            this.descriptionFormatter = descriptionFormatter;
            return (U) this;
        }
    }
}
