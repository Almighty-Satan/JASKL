package com.github.almightysatan.jaskl.mongodb;

import com.github.almightysatan.jaskl.Config;
import com.github.almightysatan.jaskl.ConfigEntry;
import com.github.almightysatan.jaskl.entries.IntegerConfigEntry;
import com.github.almightysatan.jaskl.entries.StringConfigEntry;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MongodbConfigTest {

    @Test
    @Disabled
    public void testWrite() throws IOException {
        String mongoAddress = System.getenv("MONGO_ADDRESS");

        Config config0 = MongodbConfig.of(mongoAddress, "configTest", "abc");
        ConfigEntry<String> stringConfigEntry0 = StringConfigEntry.of(config0, "exampleString", null, "default");
        ConfigEntry<Integer> intConfigEntry0 = IntegerConfigEntry.of(config0, "exampleInt", null, 69);

        config0.load();

        stringConfigEntry0.setValue("Example");
        intConfigEntry0.setValue(420);

        config0.write();
        config0.close();

        Config config1 = MongodbConfig.of(mongoAddress, "configTest", "abc");
        ConfigEntry<String> stringConfigEntry1 = StringConfigEntry.of(config1, "exampleString", null, "default");
        ConfigEntry<Integer> intConfigEntry1 = IntegerConfigEntry.of(config1, "exampleInt", null, 69);

        config1.load();
        config1.strip();

        assertEquals("Example", stringConfigEntry1.getValue());
        assertEquals(420, intConfigEntry1.getValue());
    }
}
