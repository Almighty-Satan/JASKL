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

import io.github.almightysatan.jaskl.impl.Util;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URL;
import java.net.UnknownServiceException;
import java.util.Objects;

/**
 * Represents a resource of some sort, for example a file or a network resource
 */
public interface Resource {

    /**
     * Returns {@code true} if the resource exists
     *
     * @return {@code true} if the resource exists
     * @throws IOException if an I/O exception occurs
     */
    boolean exists() throws IOException;

    /**
     * Creates the resource if it does not exist (if possible)
     *
     * @throws IOException                   if an I/O exception occurs
     * @throws UnsupportedOperationException if {@link Resource#isReadOnly} is {@code true}
     */
    void createIfNotExists() throws IOException;

    /**
     * Returns a {@link Reader} for this resource
     *
     * @return a {@link Reader}
     * @throws IOException if an I/O exception occurs
     */
    @NotNull Reader getReader() throws IOException;

    /**
     * Returns a {@link Writer} for this resource
     *
     * @return a {@link Writer}
     * @throws IOException                   if an I/O exception occurs
     * @throws UnsupportedOperationException if {@link Resource#isReadOnly} is {@code true}
     */
    @NotNull Writer getWriter() throws IOException;

    /**
     * Returns {@code true} if this resource is read-only
     *
     * @return {@code true} if this resource is read-only
     * @throws IOException if an I/O exception occurs
     */
    boolean isReadOnly() throws IOException;

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
            public boolean exists() throws IOException {
                return file.exists();
            }

            @Override
            public void createIfNotExists() throws IOException {
                if (this.isReadOnly())
                    throw new UnsupportedOperationException();
                Util.createFileAndPath(file);
            }

            @Override
            public @NotNull Reader getReader() throws IOException {
                return new FileReader(file);
            }

            @Override
            public @NotNull Writer getWriter() throws IOException {
                if (this.isReadOnly())
                    throw new UnsupportedOperationException();
                file.setWritable(true); // TODO I have no idea why this is necessary, but the tests fail without this
                return new FileWriter(file);
            }

            @Override
            public boolean isReadOnly() throws IOException {
                return file.exists() && !file.setReadOnly();
            }
        };
    }

    /**
     * Returns a new {@link Resource} from the given {@link URL}
     *
     * @param url the {@link URL}
     * @return a new {@link Resource} from the given {@link URL}
     */
    static Resource of(URL url) {
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
                return new InputStreamReader(url.openStream());
            }

            @Override
            public @NotNull Writer getWriter() throws IOException {
                try {
                    return new OutputStreamWriter(url.openConnection().getOutputStream());
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
