package com.github.almightysatan.hocon;

import static org.junit.jupiter.api.Assertions.*;

import com.github.almightysatan.Config;
import com.github.almightysatan.ConfigEntry;
import com.github.almightysatan.impl.WritableConfigEntryImpl;
import com.github.almightysatan.impl.hocon.HoconConfig;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HoconConfigTest {

    @Test
    public void testFile() throws IOException {
        Config config = new HoconConfig(new File("src/test/resources/hocon/basic.hocon"), null);
        ConfigEntry<String> stringConfigEntry = new WritableConfigEntryImpl<>(config, "hocon.exampleString", null, "default");
        ConfigEntry<Integer> intConfigEntry = new WritableConfigEntryImpl<>(config, "hocon.exampleInt", null, 0);
        ConfigEntry<Boolean> boolConfigEntry = new WritableConfigEntryImpl<>(config, "hocon.exampleBool", null, false);
        ConfigEntry<Double> floatConfigEntry = new WritableConfigEntryImpl<>(config, "hocon.exampleDouble", null, 0.0D);

        config.load();

        assertEquals("String", stringConfigEntry.getValue());
        assertEquals(42, intConfigEntry.getValue());
        assertEquals(true, boolConfigEntry.getValue());
        assertEquals(6.9D, floatConfigEntry.getValue());

        config.close();
    }

    @Test
    public void testLoadNonExisting() throws IOException {
        Config config = new HoconConfig(new File("src/test/resources/hocon/basic.hocon"), null);

        ConfigEntry<String> nonExistingStringConfigEntry = new WritableConfigEntryImpl<>(config, "hocon.doesnotexist", null, "default");

        config.load();

        assertEquals("default", nonExistingStringConfigEntry.getValue()); // Doesn't exist in the file therefore the default value should be used

        config.close();
    }

    @Test
    public void testList() throws IOException {
        Config config = new HoconConfig(new File("src/test/resources/hocon/list.hocon"), null);
        ConfigEntry<List<String>> stringConfigEntry = new WritableConfigEntryImpl<>(config, "hocon.subConf.exampleList", null, Collections.emptyList());

        config.load();

        assertEquals(Arrays.asList("ListContent1", "ListContent2"), stringConfigEntry.getValue());

        config.close();
    }
}
