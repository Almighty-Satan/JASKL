package com.github.almightysatan;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigProperty {

    /**
     * The name of the path. Uses the name of the field if empty.
     *
     * @return the path name
     */
    @NotNull String value() default "";
}
