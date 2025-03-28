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

package io.github.almightysatan.jaskl.test;

import io.github.almightysatan.jaskl.impl.ConfigImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.Set;

public class NopConfigImpl extends ConfigImpl {

    public NopConfigImpl() {
        super(null, null);
    }

    @Override
    public void load() {
    }

    @Override
    public void reload() {
    }

    @Override
    public void write() {
    }

    @Override
    public @Unmodifiable @NotNull Set<@NotNull String> prune() {
        return Collections.emptySet();
    }

    @Override
    public void close() {
    }
}
