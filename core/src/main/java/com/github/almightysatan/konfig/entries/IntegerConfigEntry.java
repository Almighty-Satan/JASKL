package com.github.almightysatan.konfig.entries;

import com.github.almightysatan.konfig.Config;
import com.github.almightysatan.konfig.ConfigEntry;
import com.github.almightysatan.konfig.InvalidTypeException;
import com.github.almightysatan.konfig.impl.WritableConfigEntryImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;

public class IntegerConfigEntry extends WritableConfigEntryImpl<Integer> {

    private IntegerConfigEntry(@NotNull Config config, @NotNull String path, @Nullable String description, @NotNull Integer defaultValue) {
        super(config, path, description, defaultValue);
    }

    @Override
    protected @NotNull Integer checkType(@NotNull Object type) {
        if (type instanceof Integer)
            return (Integer) type;

        if (type instanceof Long) {
            long longVal = (Long) type;
            if (longVal > Integer.MIN_VALUE && longVal < Integer.MAX_VALUE)
                return (int) longVal;
        }

        if (type instanceof BigInteger) {
            BigInteger bigInt = (BigInteger) type;
            if (bigInt.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) > 0 && bigInt.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) < 0)
                return bigInt.intValue();
        }

        if (type instanceof String) {
            try {
                return Integer.valueOf((String) type);
            } catch (NumberFormatException ignored) {}
        }

        throw new InvalidTypeException(getPath(), Integer.class, type.getClass());
    }

    public static ConfigEntry<Integer> of(@NotNull Config config, @NotNull String path, @Nullable String description, int defaultValue) {
        return new IntegerConfigEntry(config, path, description, defaultValue);
    }
}
