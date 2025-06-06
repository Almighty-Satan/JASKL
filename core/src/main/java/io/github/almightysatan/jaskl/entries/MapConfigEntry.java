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

package io.github.almightysatan.jaskl.entries;

import io.github.almightysatan.jaskl.*;
import io.github.almightysatan.jaskl.impl.WritableConfigEntryImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface MapConfigEntry<K, V> extends ConfigEntry<Map<K, V>> {

    /**
     * Creates a new config entry.
     *
     * @param config       the config
     * @param path         the case-sensitive dotted path
     * @param description  the possibly-null description
     * @param keyType      the {@link Type} of the elements in the key
     * @param valueType    the {@link Type} of the elements in the value
     * @param defaultValue the default value
     * @param validators   the {@link Validator Validators}
     * @param <K>          the key elements
     * @param <V>          the value elements
     * @return a new entry
     * @throws InvalidTypeException if the default value's type is invalid
     * @throws ValidationException  if the default value fails validation
     */
    @SafeVarargs
    static <K, V> @NotNull MapConfigEntry<K, V> of(@NotNull Config config, @NotNull String path, @Nullable String description, @NotNull Map<K, V> defaultValue, @NotNull Type<K> keyType, @NotNull Type<V> valueType, @NotNull Validator<? super Map<K, V>>... validators) throws InvalidTypeException, ValidationException {
        class MapConfigEntryImpl extends WritableConfigEntryImpl<Map<K, V>> implements MapConfigEntry<K, V> {
            MapConfigEntryImpl() {
                super(Type.validated(Type.map(keyType, valueType), validators), path, description, defaultValue);
                this.register(config);
            }
        }

        return new MapConfigEntryImpl();
    }

    /**
     * Creates a new config entry.
     *
     * @param config       the config
     * @param path         the case-sensitive dotted path
     * @param keyType      the {@link Type} of the elements in the key
     * @param valueType    the {@link Type} of the elements in the value
     * @param defaultValue the default value
     * @param validators   the {@link Validator Validators}
     * @param <K>          the key elements
     * @param <V>          the value elements
     * @return a new entry
     * @throws InvalidTypeException if the default value's type is invalid
     * @throws ValidationException  if the default value fails validation
     */
    @SafeVarargs
    static <K, V> @NotNull MapConfigEntry<K, V> of(@NotNull Config config, @NotNull String path, @NotNull Map<K, V> defaultValue, @NotNull Type<K> keyType, @NotNull Type<V> valueType, @NotNull Validator<? super Map<K,V>>... validators) throws InvalidTypeException, ValidationException {
        return of(config, path, null, defaultValue, keyType, valueType, validators);
    }
}
