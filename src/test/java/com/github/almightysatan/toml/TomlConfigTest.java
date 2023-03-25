package com.github.almightysatan.toml;

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
    public void testLoadSimple() throws IOException {
        Config config = new TomlConfig(new File("src/test/resources/toml/basic.toml"), null);
        ConfigEntry<String> stringConfigEntry = new GenericConfigEntry<>(config, "exampleString", null, "default");
        ConfigEntry<Double> doubleConfigEntry = new GenericConfigEntry<>(config, "exampleDouble", null, 0.0D);
        ConfigEntry<Integer> integerConfigEntry = new GenericConfigEntry<>(config, "exampleInteger", null, 0);
        ConfigEntry<Long> longConfigEntry = new GenericConfigEntry<>(config, "exampleLong", null, 0L);

        config.load();

        assertEquals("String", stringConfigEntry.getValue());
        assertEquals(123L, longConfigEntry.getValue());
        assertEquals(123, integerConfigEntry.getValue());
        assertEquals(4.2, doubleConfigEntry.getValue());

        config.close();
    }

    @Test
    public void testLoadComplex() throws IOException {
        Config config = new TomlConfig(new File("src/test/resources/toml/complex.toml"), null);
        ConfigEntry<String> subEntry = new GenericConfigEntry<>(config, "subCategory.subEntry", null, "default");
        ConfigEntry<String> subSubEntry = new GenericConfigEntry<>(config, "subCategory.subSubCategory.subSubEntry", null, "default");

        config.load();

        assertEquals("Sub", subEntry.getValue());
        assertEquals("SubSub", subSubEntry.getValue());

        config.close();
    }

    @Test
    public void testLoadNonExisting() throws IOException {
        Config config = new TomlConfig(new File("src/test/resources/toml/simple.toml"), null);

        ConfigEntry<String> nonExistingStringConfigEntry = new GenericConfigEntry<>(config, "doesnotexist", null, "default");

        config.load();

        assertEquals("default", nonExistingStringConfigEntry.getValue()); // Doesn't exist in the file therefore the default value should be used

        config.close();
    }

    // TODO: implement list entries correctly
    /*
    @Test
    public void testLoadList() throws IOException {
        Config config = new TomlConfig(new File("src/test/resources/toml/list.toml"), null);
        ConfigEntry<List<Integer>> integerListConfigEntry = new GenericConfigEntry<>(config, "subCategory.integerList", null, Collections.emptyList());

        config.load();

        assertEquals(Arrays.asList(1, 2, 3), integerListConfigEntry.getValue());

        config.close();
    }
     */

    @Test
    public void testWriteFile() {
        File file = new File("build/temp/toml/write.toml");
        file.delete();

        Config config = new TomlConfig(file, null);
        ConfigEntry<String> stringConfigEntry = new GenericConfigEntry<>(config, "exampleString", null, "default");

        stringConfigEntry.setValue("Example");

        try {
            config.write();
        } catch (IOException e) {
            e.printStackTrace();
            fail("Failed to write configuration.");
        }

        assertTrue(file.exists());

        try {
            config.load();
        } catch (IOException e) {
            e.printStackTrace();
            fail("Failed to load configuration.");
        }

        assertEquals("Example", stringConfigEntry.getValue());
    }
}
