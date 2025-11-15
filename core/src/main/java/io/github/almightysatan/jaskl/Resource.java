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

import java.io.*;
import java.net.URL;
import java.net.UnknownServiceException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;

/**
 * Represents a resource of some sort, for example a file or a network resource
 */
public interface Resource {

    /**
     * Returns {@code true} if the resource exists
     *
     * @return {@code true} if the resource exists
     * @throws IOException       if an I/O exception occurs
     * @throws SecurityException if a security manager exists and denies access to an operation
     */
    boolean exists() throws IOException;

    /**
     * Creates the resource if it does not exist (if possible)
     *
     * @throws IOException                   if an I/O exception occurs
     * @throws UnsupportedOperationException if {@link Resource#isReadOnly} is {@code true}
     * @throws SecurityException             if a security manager exists and denies access to an operation
     */
    void createIfNotExists() throws IOException, SecurityException;

    /**
     * Returns a {@link Reader} for this resource
     *
     * @return a {@link Reader}
     * @throws IOException       if an I/O exception occurs
     * @throws SecurityException if a security manager exists and denies access to an operation
     */
    @NotNull Reader getReader() throws IOException, SecurityException;

    /**
     * Returns a {@link Writer} for this resource
     *
     * @return a {@link Writer}
     * @throws IOException                   if an I/O exception occurs
     * @throws UnsupportedOperationException if {@link Resource#isReadOnly} is {@code true}
     * @throws SecurityException             if a security manager exists and denies access to an operation
     */
    @NotNull Writer getWriter() throws IOException, SecurityException;

    /**
     * Returns {@code true} if this resource is read-only
     *
     * @return {@code true} if this resource is read-only
     * @throws IOException       if an I/O exception occurs
     * @throws SecurityException if a security manager exists and denies access to an operation
     */
    boolean isReadOnly() throws IOException, SecurityException;

    /**
     * Returns a new {@link Resource} from the given {@link File}
     *
     * @param file the {@link File}
     * @return a new {@link Resource} from the given {@link File}
     */
    static Resource of(@NotNull File file) {
        Objects.requireNonNull(file);
        return new Resource() {
            @Override
            public boolean exists() throws IOException, SecurityException {
                return file.exists();
            }

            @Override
            public void createIfNotExists() throws IOException, SecurityException {
                if (this.isReadOnly())
                    throw new UnsupportedOperationException();
                if (file.exists())
                    return;
                if (!file.getParentFile().exists() && !file.getParentFile().mkdirs())
                    throw new IOException("Unable to create directory");
                file.createNewFile();
            }

            @Override
            public @NotNull Reader getReader() throws IOException, SecurityException {
                return Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8);
            }

            @Override
            public @NotNull Writer getWriter() throws IOException, SecurityException {
                if (this.isReadOnly())
                    throw new UnsupportedOperationException();
                return Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8);
            }

            @Override
            public boolean isReadOnly() throws IOException, SecurityException {
                return file.exists() && !file.canRead();
            }
        };
    }

    /**
     * Returns a new {@link Resource} from the given {@link URL}
     *
     * @param url the {@link URL}
     * @return a new {@link Resource} from the given {@link URL}
     */
    static Resource of(@NotNull URL url) {
        Objects.requireNonNull(url);
        return new Resource() {
            @Override
            public boolean exists() throws IOException {
                return true;
            }

            @Override
            public void createIfNotExists() throws IOException {
                // nop
            }

            @Override
            public @NotNull Reader getReader() throws IOException {
                return new InputStreamReader(url.openStream(), StandardCharsets.UTF_8);
            }

            @Override
            public @NotNull Writer getWriter() throws IOException {
                try {
                    return new OutputStreamWriter(url.openConnection().getOutputStream(), StandardCharsets.UTF_8);
                } catch (UnknownServiceException e) {
                    throw new UnsupportedOperationException();
                }
            }

            @Override
            public boolean isReadOnly() throws IOException {
                try (OutputStream ignored = url.openConnection().getOutputStream()) {
                    return false;
                } catch (UnknownServiceException e) {
                    return true;
                }
            }
        };
    }
}
