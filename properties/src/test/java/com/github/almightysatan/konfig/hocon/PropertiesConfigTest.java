package com.github.almightysatan.konfig.hocon;

import com.github.almightysatan.konfig.Config;
import com.github.almightysatan.konfig.ConfigEntry;
import com.github.almightysatan.konfig.entries.*;
import com.github.almightysatan.konfig.impl.WritableConfigEntryImpl;
import com.github.almightysatan.konfig.properties.PropertiesConfig;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PropertiesConfigTest {

    @Test
    public void testFile() throws IOException {
        Config config = new PropertiesConfig(new File("src/test/resources/basic.properties"), null);
        ConfigEntry<String> stringConfigEntry = new StringConfigEntry(config, "exampleString", null, "default");
        ConfigEntry<Integer> intConfigEntry = new IntegerConfigEntry(config, "exampleInt", null, 0);
        ConfigEntry<Boolean> boolConfigEntry = new BooleanConfigEntry(config, "exampleBool", null, false);
        ConfigEntry<Double> doubleConfigEntry = new DoubleConfigEntry(config, "exampleDouble", null, 0.0d);

        config.load();

        assertEquals("String", stringConfigEntry.getValue());
        assertEquals(42, intConfigEntry.getValue());
        assertEquals(true, boolConfigEntry.getValue());
        assertEquals(6.9d, doubleConfigEntry.getValue());

        config.close();
    }

    @Test
    public void testLoadNonExisting() throws IOException {
        Config config = new PropertiesConfig(new File("src/test/resources/basic.properties"), null);

        ConfigEntry<String> nonExistingStringConfigEntry = new WritableConfigEntryImpl<>(config, "doesnotexist", null, "default");

        config.load();

        assertEquals("default", nonExistingStringConfigEntry.getValue()); // Doesn't exist in the file therefore the default value should be used

        config.close();
    }

    @Test
    public void testWriteFile() throws IOException {
        File file = new File("build/temp/properties/write.properties");
        file.delete();

        Config config0 = new PropertiesConfig(file, null);
        ConfigEntry<String> stringConfigEntry0 = new StringConfigEntry(config0, "exampleString", null, "default");
        ConfigEntry<Integer> intConfigEntry0 = new IntegerConfigEntry(config0, "exampleInt", null, 69);

        config0.load();

        stringConfigEntry0.setValue("Example");
        intConfigEntry0.setValue(420);

        config0.write();
        config0.close();

        assertTrue(file.exists());

        Config config1 = new PropertiesConfig(file, null);
        ConfigEntry<String> stringConfigEntry1 = new WritableConfigEntryImpl<>(config1, "exampleString", null, "default");
        ConfigEntry<Integer> intConfigEntry1 = new IntegerConfigEntry(config1, "exampleInt", null, 69);

        config1.load();

        assertEquals("Example", stringConfigEntry1.getValue());
        assertEquals(420, intConfigEntry1.getValue());
    }
}
