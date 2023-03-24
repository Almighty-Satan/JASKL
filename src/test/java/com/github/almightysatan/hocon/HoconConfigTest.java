package com.github.almightysatan.hocon;

import static org.junit.jupiter.api.Assertions.*;

import com.github.almightysatan.Config;
import com.github.almightysatan.ConfigEntry;
import com.github.almightysatan.impl.GenericConfigEntry;
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
        Config config = new HoconConfig(new File("src/test/resources/basic.hocon"), null);
        ConfigEntry<String> stringConfigEntry = new GenericConfigEntry<>(config, "hocon.exampleString", null, "default");
        ConfigEntry<Integer> intConfigEntry = new GenericConfigEntry<>(config, "hocon.exampleInt", null, 0);
        ConfigEntry<Boolean> boolConfigEntry = new GenericConfigEntry<>(config, "hocon.exampleBool", null, false);
        ConfigEntry<Double> floatConfigEntry = new GenericConfigEntry<>(config, "hocon.exampleDouble", null, 0.0D);
        ConfigEntry<String> nonExistingStringConfigEntry = new GenericConfigEntry<>(config, "hocon.doesnotexist", null, "default");

        config.load();

        assertEquals("String", stringConfigEntry.getValue());
        assertEquals(42, intConfigEntry.getValue());
        assertEquals(true, boolConfigEntry.getValue());
        assertEquals(6.9D, floatConfigEntry.getValue());
        assertEquals("default", nonExistingStringConfigEntry.getValue()); // Doesn't exist in the file therefore the default value should be used

        config.close();
    }

    @Test
    public void testList() throws IOException {
        Config config = new HoconConfig(new File("src/test/resources/list.hocon"), null);
        ConfigEntry<List<String>> stringConfigEntry = new GenericConfigEntry<>(config, "hocon.subConf.exampleList", null, Collections.emptyList());

        config.load();

        assertEquals(Arrays.asList("ListContent1", "ListContent2"), stringConfigEntry.getValue());

        config.close();
    }
}
