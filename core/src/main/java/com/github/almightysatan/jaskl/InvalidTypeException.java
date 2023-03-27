package com.github.almightysatan.jaskl;

public class InvalidTypeException extends RuntimeException {

    public InvalidTypeException(String path, Class<?> expected, Class<?> actual) {
        super(String.format("Invalid type path=%s, expected=%s, actual=%s", path, expected.getName(), actual.getName()));
    }
}
