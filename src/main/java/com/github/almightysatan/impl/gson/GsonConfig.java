package com.github.almightysatan.impl.gson;

import com.github.almightysatan.Config;
import com.github.almightysatan.impl.ConfigImpl;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;

public class GsonConfig extends ConfigImpl {

    private final File file;

    private Gson root;

    public GsonConfig(File file, String description) {
        super(description);
        this.file = file;
    }

    @Override
    public void load() throws IOException, IllegalStateException {

    }

    @Override
    public void reload() throws IOException, IllegalStateException {

    }

    @Override
    public void write() throws IOException {

    }

    @Override
    public void populate() throws IOException {

    }

    @Override
    public void strip() throws IOException {

    }

    @Override
    public void close() {

    }
}
