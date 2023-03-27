package com.github.almightysatan.jaskl.entries;

import com.github.almightysatan.jaskl.Config;
import com.github.almightysatan.jaskl.ConfigEntry;
import com.github.almightysatan.jaskl.InvalidTypeException;
import com.github.almightysatan.jaskl.impl.WritableConfigEntryImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;

public class FloatConfigEntry extends WritableConfigEntryImpl<Float> {

    private FloatConfigEntry(@NotNull Config config, @NotNull String path, @Nullable String description, @NotNull Float defaultValue) {
        super(config, path, description, defaultValue);
    }

    @Override
    protected @NotNull Float checkType(@NotNull Object type) {
        if (type instanceof Float)
            return (Float) type;

        if (type instanceof BigDecimal) {
            BigDecimal bigDecimal = (BigDecimal) type;
            if (bigDecimal.compareTo(BigDecimal.valueOf(Float.MIN_VALUE)) > 0 && bigDecimal.compareTo(BigDecimal.valueOf(Float.MAX_VALUE)) < 0)
                return bigDecimal.floatValue();
        }

        if (type instanceof String) {
            try {
                return Float.valueOf((String) type);
            } catch (NumberFormatException ignored) {}
        }

        throw new InvalidTypeException(getPath(), Float.class, type.getClass());
    }

    public static ConfigEntry<Float> of(@NotNull Config config, @NotNull String path, @Nullable String description, float defaultValue) {
        return new FloatConfigEntry(config, path, description, defaultValue);
    }
}
