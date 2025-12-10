/*
 * JASKL - Just Another Simple Konfig Library
 * Copyright (C) 2023 LeStegii, Almighty-Satan
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 * USA
 */

package io.github.almightysatan.jaskl.test;

import io.github.almightysatan.jaskl.annotation.Description;
import io.github.almightysatan.jaskl.annotation.Entry;

import java.util.Objects;

public class ExampleCustomObject {

    @Entry("exampleString")
    @Description("Example string description")
    public String exampleString;
    @Entry("exampleInt")
    public int exampleInt;
    @Entry("exampleEnum")
    @Description("Example enum description")
    public ExampleEnum exampleEnum;

    public ExampleCustomObject() {}

    public ExampleCustomObject(String exampleString, int exampleInt, ExampleEnum exampleEnum) {
        this.exampleString = exampleString;
        this.exampleInt = exampleInt;
        this.exampleEnum = exampleEnum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExampleCustomObject that = (ExampleCustomObject) o;
        return exampleInt == that.exampleInt && Objects.equals(exampleString, that.exampleString) && exampleEnum == that.exampleEnum;
    }

    @Override
    public int hashCode() {
        return Objects.hash(exampleString, exampleInt, exampleEnum);
    }

    @Override
    public String toString() {
        return "ExampleCustomObject{" +
                "exampleString='" + exampleString + '\'' +
                ", exampleInt=" + exampleInt +
                ", exampleEnum=" + exampleEnum +
                '}';
    }
}
