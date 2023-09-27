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

package io.github.almightysatan.jaskl.annotation;

import io.github.almightysatan.jaskl.*;
import io.github.almightysatan.jaskl.impl.AnnotationManagerImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;

public interface AnnotationManager {

    <T> void addValidatorFunction(@NotNull Class<T> annotationClass, @NotNull Function<T, Validator<?>> validatorFunction);

    default void addValidator(@NotNull Class<?> annotationClass, @NotNull Validator<?> validator) {
        Objects.requireNonNull(validator);
        this.addValidatorFunction(annotationClass, annotation -> validator);
    }

    /**
     * Registers config entries for the given annotated class and returns an instance of that class.
     * The returned instance can be used to read and modify the values of these config entries. Values are validated
     * when the config is loaded/written.
     *
     * @param config      a config instance
     * @param configClass a class containing annotated fields
     * @param <T>         the type of the class
     * @return an instance of the given class
     * @throws InvalidAnnotationConfigException if the config class is misconfigured
     * @throws InvalidTypeException             if a default value fails type checking
     * @throws ValidationException              if a default value fails validation
     */
    <T> @NotNull T registerEntries(@NotNull Config config, @NotNull Class<T> configClass) throws InvalidAnnotationConfigException, InvalidTypeException, ValidationException;

    /**
     * Returns a {@link Type} for the given annotated class.
     *
     * @param typeClass a class containing annotated fields
     * @param <T>       the type of the class
     * @return the {@link Type}
     * @throws InvalidAnnotationConfigException if the type class is misconfigured
     */
    <T> @NotNull Type<T> createCustomObjectType(@NotNull Class<T> typeClass) throws InvalidAnnotationConfigException;

    /**
     * Creates a new {@link AnnotationManager}.
     *
     * @return a new instance
     */
    static @NotNull AnnotationManager create() {
        return new AnnotationManagerImpl();
    }
}
