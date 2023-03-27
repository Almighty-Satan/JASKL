package com.github.almightysatan.jaskl.impl;

import java.io.File;
import java.io.IOException;

public class Util {

    public static void createFileAndPath(File file) throws IOException {
        if (!file.exists()) {
            if (!file.getParentFile().exists())
                file.getParentFile().mkdirs();
            file.createNewFile();
        }
    }

}
