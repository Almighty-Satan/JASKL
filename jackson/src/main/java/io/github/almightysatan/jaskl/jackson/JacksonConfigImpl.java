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

package io.github.almightysatan.jaskl.jackson;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import io.github.almightysatan.jaskl.ExceptionHandler;
import io.github.almightysatan.jaskl.Resource;
import io.github.almightysatan.jaskl.impl.ConfigImpl;
import io.github.almightysatan.jaskl.impl.WritableConfigEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public abstract class JacksonConfigImpl extends ConfigImpl {

    private final ObjectMapper mapper;
    private final Resource resource;
    private ObjectNode root;

    protected JacksonConfigImpl(@NotNull ObjectMapper mapper, @NotNull Resource resource, @Nullable String description, @Nullable ExceptionHandler exceptionHandler) {
        super(description, exceptionHandler);
        mapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
        this.mapper = Objects.requireNonNull(mapper);
        this.resource = Objects.requireNonNull(resource);
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
        if (!this.resource.exists())
            return;

        JsonNode root = this.mapper.readTree(resource.getReader());
        if (root instanceof MissingNode)
            return;
        try {
            this.root = (ObjectNode) root;
        } catch (ClassCastException e) {
            throw new IOException(e);
        }

        for (WritableConfigEntry<?> configEntry : this.getCastedValues()) {
            JsonNode node = this.resolveNode(configEntry.getPath());
            if (node == null)
                continue;

            Object value = this.mapper.treeToValue(node, Object.class);
            configEntry.putValue(value, this.getExceptionHandler());
        }
    }

    @Override
    public void write() throws IOException {
        if (this.root == null)
            throw new IllegalStateException();
        this.resource.createIfNotExists();

        boolean shouldWrite = false;
        for (WritableConfigEntry<?> configEntry : this.getCastedValues()) {
            if (configEntry.isModified()) {
                this.putNode(configEntry.getPath(), this.mapper.valueToTree(configEntry.getValueToWrite()));
                shouldWrite = true;
            }
        }

        if (shouldWrite)
            this.mapper.writerWithDefaultPrettyPrinter().writeValue(this.resource.getWriter(), this.root);
    }

    @Override
    public @Unmodifiable @NotNull Set<@NotNull String> prune() throws IOException {
        if (this.root == null)
            throw new IllegalStateException();
        if (!this.resource.exists())
            return Collections.emptySet();

        Set<String> pathsRemoved = new HashSet<>();
        if (stripNodes("", this.root, this.getPaths(), pathsRemoved))
            this.mapper.writerWithDefaultPrettyPrinter().writeValue(this.resource.getWriter(), this.root);
        return Collections.unmodifiableSet(pathsRemoved);
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

    protected boolean stripNodes(@NotNull String path, @NotNull ObjectNode node, @NotNull Collection<String> paths, @NotNull Set<String> pathsRemoved) {
        boolean changed = false;
        List<String> toRemove = new ArrayList<>();
        for (Entry<String, JsonNode> field : node.properties()) {
            String fieldPath = path.isEmpty() ? field.getKey() : path + "." + field.getKey();
            if (field.getValue() instanceof ObjectNode) {
                if (paths.contains(fieldPath))
                    continue;
                ObjectNode child = (ObjectNode) field.getValue();
                changed |= stripNodes(fieldPath, child, paths, pathsRemoved);
                if (child.isEmpty())
                    toRemove.add(field.getKey());
            } else if (field.getValue() instanceof ArrayNode || field.getValue() instanceof ValueNode) {
                if (!paths.contains(fieldPath)) {
                    pathsRemoved.add(fieldPath);
                    toRemove.add(field.getKey());
                }
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
