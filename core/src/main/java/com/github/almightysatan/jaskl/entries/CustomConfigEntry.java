package com.github.almightysatan.jaskl.entries;

import com.github.almightysatan.jaskl.Config;
import com.github.almightysatan.jaskl.ConfigProperty;
import com.github.almightysatan.jaskl.impl.ConfigEntryImpl;
import com.github.almightysatan.jaskl.impl.WritableConfigEntry;
import com.github.almightysatan.jaskl.impl.WritableConfigEntryImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CustomConfigEntry<T> extends ConfigEntryImpl<T> {

    private final Class<T> type;
    private final Property<?>[] properties;
    private T value;

    public CustomConfigEntry(@NotNull Config config, @NotNull String path, @Nullable String description, @NotNull T defaultValue, @NotNull Class<T> type) {
        super(path, description, defaultValue);
        Objects.requireNonNull(config);
        this.type = Objects.requireNonNull(type);

        List<Property<?>> properties = new ArrayList<>();
        try {
            for (Field field : type.getDeclaredFields()) {
                ConfigProperty annotation = field.getAnnotation(ConfigProperty.class);
                if (annotation != null) {
                    field.setAccessible(true);
                    String propertyPath = path + "." + (annotation.value().isEmpty() ? field.getName() : annotation.value());
                    properties.add(new Property<>(field, new WritableConfigEntryImpl<>(config, propertyPath, null, field.get(defaultValue))));
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e); // TODO custom exception
        }
        this.properties = properties.toArray(new Property[0]);
        this.value = defaultValue;
    }

    @Override
    public @NotNull T getValue() {
        T value = this.value;
        if (value == null) {
            try {
                value = this.type.newInstance();
                for (Property<?> property : this.properties)
                    property.getField().set(value, property.getValue());
                this.value = value;
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e); // TODO custom exception
            }
        }
        return value;
    }

    @Override
    public void setValue(@NotNull T value) {
        Objects.requireNonNull(value);
        this.checkType(value);
        try {
            for (Property<?> property : this.properties)
                property.setValueAsObject(property.field.get(value));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e); // TODO custom exception
        }
        this.value = value;
    }

    private class Property<U> implements WritableConfigEntry<U> {

        private final Field field;
        private final WritableConfigEntry<U> entry;

        Property(@NotNull Field field, @NotNull WritableConfigEntry<U> entry) {
            this.field = field;
            this.entry = entry;
        }

        @Override
        public @NotNull String getPath() {
            return this.entry.getPath();
        }

        @Override
        public @Nullable String getDescription() {
            return this.entry.getDescription();
        }

        @Override
        public @NotNull U getValue() {
            return this.entry.getValue();
        }

        @Override
        public @NotNull U getDefaultValue() {
            return this.entry.getDefaultValue();
        }

        @Override
        public void setValue(@NotNull U value) {
            this.entry.setValue(value);
        }

        @SuppressWarnings("unchecked")
        private void setValueAsObject(@NotNull Object value) {
            this.entry.setValue((U) value);
        }

        @Override
        public void putValue(@NotNull Object value) {
            this.entry.putValue(value);
            CustomConfigEntry.this.value = null;
        }

        @Override
        public boolean isModified() {
            return this.entry.isModified();
        }

        private @NotNull Field getField() {
            return field;
        }
    }
}
