package com.github.almightysatan.konfig.entries;

import com.github.almightysatan.konfig.Config;
import com.github.almightysatan.konfig.ConfigEntry;
import com.github.almightysatan.konfig.InvalidTypeException;
import com.github.almightysatan.konfig.impl.WritableConfigEntryImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;

public class LongConfigEntry extends WritableConfigEntryImpl<Long> {

    private LongConfigEntry(@NotNull Config config, @NotNull String path, @Nullable String description, @NotNull Long defaultValue) {
        super(config, path, description, defaultValue);
    }

    @Override
    protected @NotNull Long checkType(@NotNull Object type) {
        if (type instanceof Long)
            return (Long) type;

        if (type instanceof Integer)
            return ((Integer) type).longValue();

        if (type instanceof BigInteger) {
            BigInteger bigInt = (BigInteger) type;
            if (bigInt.compareTo(BigInteger.valueOf(Long.MIN_VALUE)) > 0 && bigInt.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) < 0)
                return bigInt.longValue();
        }

        if (type instanceof String) {
            try {
                return Long.valueOf((String) type);
            } catch (NumberFormatException ignored) {}
        }

        throw new InvalidTypeException(getPath(), Long.class, type.getClass());

    }

    public static ConfigEntry<Long> of(@NotNull Config config, @NotNull String path, @Nullable String description, long defaultValue) {
        return new LongConfigEntry(config, path, description, defaultValue);
    }
}
