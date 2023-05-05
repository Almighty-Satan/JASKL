/*
 * JASKL - Just Another Simple Konfig Library
 * Copyright (C) 2023 UeberallGebannt, Almighty-Satan
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

package io.github.almightysatan.jaskl.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import io.github.almightysatan.jaskl.impl.ConfigImpl;
import io.github.almightysatan.jaskl.impl.Util;
import io.github.almightysatan.jaskl.impl.WritableConfigEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public abstract class JacksonConfigImpl extends ConfigImpl {

    private final ObjectMapper mapper;
    private final File file;
    private ObjectNode root;

    protected JacksonConfigImpl(@NotNull ObjectMapper mapper, @NotNull File file, @Nullable String description) {
        super(description);
        this.mapper = Objects.requireNonNull(mapper);
        this.file = Objects.requireNonNull(file);
    }

    @Override
    public void load() throws IOException, IllegalStateException {
        if (this.root != null)
            throw new IllegalStateException();

        this.root = JsonNodeFactory.instance.objectNode();
        this.reload();
    }

    @Override
    public void reload() throws IOException, IllegalStateException {
        if (this.root == null)
            throw new IllegalStateException();
        if (!this.file.exists())
            return;

        try {
            this.root = (ObjectNode) this.mapper.readTree(this.file);
        } catch (ClassCastException e) {
            throw new IOException(e);
        }

        for (WritableConfigEntry<?> configEntry : this.getCastedValues()) {
            JsonNode node = this.resolveNode(configEntry.getPath());
            if (node == null)
                continue;

            Object value = this.mapper.treeToValue(node, Object.class);
            configEntry.putValue(value);
        }
    }

    @Override
    public void write() throws IOException {
        if (this.root == null)
            throw new IllegalStateException();
        Util.createFileAndPath(this.file);

        boolean shouldWrite = false;
        for (WritableConfigEntry<?> configEntry : this.getCastedValues()) {
            if (configEntry.isModified()) {
                this.putNode(configEntry.getPath(), this.mapper.valueToTree(configEntry.getValueToWrite()));
                shouldWrite = true;
            }
        }

        if (shouldWrite)
            this.mapper.writerWithDefaultPrettyPrinter().writeValue(this.file, this.root);
    }

    @Override
    public void strip() throws IOException {
        if (this.root == null)
            throw new IllegalStateException();
        Util.createFileAndPath(this.file);

        if (stripNodes("", this.root, this.getPaths()))
            this.mapper.writerWithDefaultPrettyPrinter().writeValue(this.file, this.root);
    }

    @Override
    public void close() {
        this.root = null;
    }

    protected @Nullable JsonNode resolveNode(@NotNull String path) {
        String[] pathSplit = path.split("\\.");
        JsonNode node = this.root;
        for (String s : pathSplit) {
            if (node == null)
                return null;
            node = node.get(s);
        }
        return node;
    }

    protected void putNode(@NotNull String path, @NotNull JsonNode value) {
        String[] pathSplit = path.split("\\.");
        ObjectNode node = this.root;
        for (int i = 0; i < pathSplit.length - 1; i++) {
            ObjectNode child = (ObjectNode) node.get(pathSplit[i]);
            node = child == null ? node.putObject(pathSplit[i]) : child;
        }
        node.set(pathSplit[pathSplit.length - 1], value);
    }

    protected boolean stripNodes(@NotNull String path, @NotNull ObjectNode node, @NotNull Set<String> paths) {
        boolean changed = false;
        List<String> toRemove = new ArrayList<>();
        Iterator<Entry<String, JsonNode>> it = node.fields();
        while (it.hasNext()) {
            Entry<String, JsonNode> field = it.next();
            String fieldPath = path.isEmpty() ? field.getKey() : path + "." + field.getKey();
            if (field.getValue() instanceof ObjectNode) {
                ObjectNode child = (ObjectNode) field.getValue();
                changed |= stripNodes(fieldPath, child, paths);
                if (child.isEmpty())
                    toRemove.add(field.getKey());
            } else if (field.getValue() instanceof ArrayNode || field.getValue() instanceof ValueNode) {
                if (!paths.contains(fieldPath))
                    toRemove.add(field.getKey());
            }
        }
        if (!toRemove.isEmpty()) {
            changed = true;
            for (String fieldName : toRemove) {
                node.remove(fieldName);
            }
        }
        return changed;
    }
}
