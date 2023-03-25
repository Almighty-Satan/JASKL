package com.github.almightysatan.toml;

import com.github.almightysatan.Config;
import com.github.almightysatan.ConfigEntry;
import com.github.almightysatan.GenericConfigEntry;
import com.github.almightysatan.impl.entry.DoubleConfigEntry;
import com.github.almightysatan.impl.entry.IntegerConfigEntry;
import com.github.almightysatan.impl.entry.LongConfigEntry;
import com.github.almightysatan.impl.entry.StringConfigEntry;
import com.github.almightysatan.impl.toml.TomlConfig;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class TomlConfigTest {

    @Test
    public void testLoadSimple() throws IOException {
        Config config = new TomlConfig(new File("src/test/resources/toml/basic.toml"), null);
        ConfigEntry<String> stringConfigEntry = new StringConfigEntry(config, "exampleString", null, "default");
        ConfigEntry<Double> doubleConfigEntry = new DoubleConfigEntry(config, "exampleDouble", null, 0.0D);
        ConfigEntry<Integer> integerConfigEntry = new IntegerConfigEntry(config, "exampleInteger", null, 0);
        ConfigEntry<Long> longConfigEntry = new LongConfigEntry(config, "exampleLong", null, 0L);

        config.load();

        assertEquals("String", stringConfigEntry.getValue());
        assertEquals(4.2, doubleConfigEntry.getValue());
        assertEquals(123, integerConfigEntry.getValue());
        assertEquals(123L, longConfigEntry.getValue());

        config.close();
    }

    @Test
    public void testLoadComplex() throws IOException {
        Config config = new TomlConfig(new File("src/test/resources/toml/complex.toml"), null);
        ConfigEntry<String> subEntry = new StringConfigEntry(config, "subCategory.subEntry", null, "default");
        ConfigEntry<String> subSubEntry = new StringConfigEntry(config, "subCategory.subSubCategory.subSubEntry", null, "default");

        config.load();

        assertEquals("Sub", subEntry.getValue());
        assertEquals("SubSub", subSubEntry.getValue());

        config.close();
    }

    @Test
    public void testLoadNonExisting() throws IOException {
        Config config = new TomlConfig(new File("src/test/resources/toml/simple.toml"), null);

        ConfigEntry<String> nonExistingStringConfigEntry = new StringConfigEntry(config, "doesnotexist", null, "default");

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
    public void testWriteFile() throws IOException {
        File file = new File("build/temp/toml/write.toml");
        file.delete();

        Config config0 = new TomlConfig(file, null);
        ConfigEntry<String> stringConfigEntry0 = new GenericConfigEntry<>(config0, "exampleString", null, "default");

        config0.load();

        stringConfigEntry0.setValue("Example");

        config0.write();
        config0.close();

        assertTrue(file.exists());

        Config config1 = new TomlConfig(file, null);
        ConfigEntry<String> stringConfigEntry1 = new GenericConfigEntry<>(config1, "exampleString", null, "default");

        config1.load();

        assertEquals("Example", stringConfigEntry1.getValue());
    }
}
