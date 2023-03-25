package com.github.almightysatan.impl.entry;

import com.github.almightysatan.Config;
import com.github.almightysatan.GenericConfigEntry;
import com.github.almightysatan.InvalidTypeException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;

public class IntegerConfigEntry extends GenericConfigEntry<Integer> {

    public IntegerConfigEntry(@NotNull Config config, @NotNull String path, @Nullable String description, @NotNull Integer defaultValue) {
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
}
