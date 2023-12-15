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

package io.github.almightysatan.jaskl.hocon;

import com.typesafe.config.*;
import io.github.almightysatan.jaskl.impl.Util;
import io.github.almightysatan.jaskl.impl.WritableConfigEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * A hocon config implementation.
 */
public class HoconLegacyConfig extends HoconConfig {

    protected HoconLegacyConfig(@NotNull File file, @Nullable String description) {
        super(file, description);
    }

    @Override
    public void write() throws IOException {
        Config config = super.config;
        if (config == null)
            throw new IllegalStateException();
        Util.createFileAndPath(this.file);

        for (WritableConfigEntry<?> configEntry : this.getCastedValues()) {
            if (configEntry.isModified()) {
                Object entryValue = configEntry.getValueToWrite(Object::toString);
                if (entryValue instanceof BigInteger)
                    entryValue = entryValue.toString();
                if (entryValue instanceof BigDecimal)
                    entryValue = entryValue.toString();
                ConfigValue value = ConfigValueFactory.fromAnyRef(entryValue);
                config = config.withValue(configEntry.getPath(), value);
            }
        }

        this.writeIfNecessary(config, true);
    }

    protected void writeIfNecessary(@NotNull Config config, boolean setDescription) throws IOException {
        if (config != super.config) {
            ConfigObject root = config.root();
            String output = root.render(RENDER_OPTIONS);
            try (FileWriter fileWriter = new FileWriter(this.file)) {
                fileWriter.write(output);
            }
            super.config = config;
        }
    }
}
