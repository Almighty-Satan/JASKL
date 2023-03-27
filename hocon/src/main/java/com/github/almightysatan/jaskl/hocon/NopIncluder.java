package com.github.almightysatan.jaskl.hocon;

import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigIncludeContext;
import com.typesafe.config.ConfigIncluder;
import com.typesafe.config.ConfigObject;

public class NopIncluder implements ConfigIncluder {

    @Override
    public ConfigIncluder withFallback(ConfigIncluder fallback) {
        return new NopIncluder();
    }

    @Override
    public ConfigObject include(ConfigIncludeContext context, String what) {
        return ConfigFactory.empty().root();
    }
}
