package com.github.almightysatan.jaskl.entries;

import com.github.almightysatan.jaskl.Config;
import com.github.almightysatan.jaskl.ConfigEntry;
import com.github.almightysatan.jaskl.InvalidTypeException;
import com.github.almightysatan.jaskl.impl.WritableConfigEntryImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;

public class DoubleConfigEntry extends WritableConfigEntryImpl<Double> {

    private DoubleConfigEntry(@NotNull Config config, @NotNull String path, @Nullable String description, @NotNull Double defaultValue) {
        super(config, path, description, defaultValue);
    }

    @Override
    protected @NotNull Double checkType(@NotNull Object type) {
        if (type instanceof Double)
            return (Double) type;

        if (type instanceof BigDecimal) {
            BigDecimal bigDecimal = (BigDecimal) type;
            if (bigDecimal.compareTo(BigDecimal.valueOf(Double.MIN_VALUE)) > 0 && bigDecimal.compareTo(BigDecimal.valueOf(Double.MAX_VALUE)) < 0)
                return bigDecimal.doubleValue();
        }

        if (type instanceof String) {
            try {
                return Double.valueOf((String) type);
            } catch (NumberFormatException ignored) {}
        }

        throw new InvalidTypeException(getPath(), Double.class, type.getClass());
    }

    public static ConfigEntry<Double> of(@NotNull Config config, @NotNull String path, @Nullable String description, double defaultValue) {
        return new DoubleConfigEntry(config, path, description, defaultValue);
    }
}
