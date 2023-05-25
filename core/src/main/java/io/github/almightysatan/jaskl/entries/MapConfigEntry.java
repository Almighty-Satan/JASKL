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

package io.github.almightysatan.jaskl.entries;

import io.github.almightysatan.jaskl.*;
import io.github.almightysatan.jaskl.impl.WritableConfigEntryImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface MapConfigEntry<K, V> extends ConfigEntry<Map<K, V>> {

    @SafeVarargs
    static <K, V> MapConfigEntry<K, V> of(@NotNull Config config, @NotNull String path, @Nullable String description, @NotNull Map<K, V> defaultValue, @NotNull Type<K> keyType, @NotNull Type<V> valueType, @NotNull Validator<Map<K, V>>... validators) throws InvalidTypeException, ValidationException {
        class MapConfigEntryImpl extends WritableConfigEntryImpl<Map<K, V>> implements MapConfigEntry<K, V> {
            MapConfigEntryImpl() {
                super(Type.validated(Type.map(keyType, valueType), validators), path, description, defaultValue);
                this.register(config);
            }
        }

        return new MapConfigEntryImpl();
    }

    @SafeVarargs
    static <K, V> MapConfigEntry<K, V> of(@NotNull Config config, @NotNull String path, @NotNull Map<K, V> defaultValue, @NotNull Type<K> keyType, @NotNull Type<V> valueType, @NotNull Validator<Map<K, V>>... validators) throws InvalidTypeException, ValidationException {
        return of(config, path, null, defaultValue, keyType, valueType, validators);
    }
}
