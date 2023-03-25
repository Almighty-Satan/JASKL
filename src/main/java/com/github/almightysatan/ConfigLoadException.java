package com.github.almightysatan;

public class ConfigLoadException extends RuntimeException {

    public ConfigLoadException(String config) {
        super(String.format("Couldn't load config %s", config));
    }
}
