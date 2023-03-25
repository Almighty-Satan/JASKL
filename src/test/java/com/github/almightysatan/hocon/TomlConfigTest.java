package com.github.almightysatan.hocon;

import com.github.almightysatan.Config;
import com.github.almightysatan.ConfigEntry;
import com.github.almightysatan.GenericConfigEntry;
import com.github.almightysatan.impl.toml.TomlConfig;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class TomlConfigTest {

    @Test
    public void testFile() throws IOException {
        Config config = new TomlConfig(new File("src/test/resources/toml/basic.toml"), null);
        ConfigEntry<String> stringConfigEntry = new GenericConfigEntry<>(config, "exampleString", null, "default");
        ConfigEntry<Double> doubleConfigEntry = new GenericConfigEntry<>(config, "exampleDouble", null, 0.0D);
        ConfigEntry<Long> intConfigEntry = new GenericConfigEntry<>(config, "subCategory.exampleLong", null, 0L);

        config.load();

        assertEquals("String", stringConfigEntry.getValue());
        assertEquals(123L, intConfigEntry.getValue());
        assertEquals(4.2, doubleConfigEntry.getValue());

        config.close();
    }

    @Test
    public void testWriteFile() {
        File file = new File("src/test/resources/toml/write.toml");
        file.delete();

        Config config = new TomlConfig(file, null);
        ConfigEntry<String> stringConfigEntry = new GenericConfigEntry<>(config, "exampleString", null, "default");

        stringConfigEntry.setValue("Example");

        try {
            config.write();
        } catch (IOException e) {
            fail();
        }

        assertTrue(file.exists());

        try {
            config.load();
        } catch (IOException e) {
            fail();
        }

        assertEquals("Example", stringConfigEntry.getValue());
    }
}
