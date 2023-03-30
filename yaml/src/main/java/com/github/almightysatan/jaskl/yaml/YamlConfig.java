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

package com.github.almightysatan.jaskl.yaml;

import com.github.almightysatan.jaskl.ConfigEntry;
import com.github.almightysatan.jaskl.impl.ConfigImpl;
import com.github.almightysatan.jaskl.impl.Util;
import com.github.almightysatan.jaskl.impl.WritableConfigEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.comments.CommentLine;
import org.yaml.snakeyaml.comments.CommentType;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.nodes.*;
import org.yaml.snakeyaml.representer.Representer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class YamlConfig extends ConfigImpl {

    private static final CustomConstructor CONSTRUCTOR = new CustomConstructor();

    private final File file;
    private Yaml yaml;
    private MappingNode root;

    private YamlConfig(@NotNull File file, @Nullable String description) {
        super(description);
        this.file = Objects.requireNonNull(file);
    }

    @Override
    public void load() throws IOException, IllegalStateException {
        if (this.yaml != null)
            throw new IllegalStateException();

        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setProcessComments(true);
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        this.yaml = new Yaml(CONSTRUCTOR, new Representer(dumperOptions), dumperOptions);
        this.reload();
    }

    @Override
    public void reload() throws IOException, IllegalStateException {
        if (this.yaml == null)
            throw new IllegalStateException();
        if (!this.file.exists()) {
            this.root = new MappingNode(Tag.MAP, new ArrayList<>(), DumperOptions.FlowStyle.BLOCK);
            return;
        }

        try (FileReader fileReader = new FileReader(this.file)) {
            this.root = (MappingNode) this.yaml.compose(fileReader);
            this.loadValues("", this.root);
        }
    }

    @Override
    public void write() throws IOException {
        if (this.yaml == null)
            throw new IllegalStateException();
        Util.createFileAndPath(this.file);

        boolean shouldWrite = false;
        for (WritableConfigEntry<?> configEntry : this.getCastedValues()) {
            if (configEntry.isModified()) {
                this.putNode(configEntry);
                shouldWrite = true;
            }
        }

        if (shouldWrite)
            try (FileWriter fileWriter = new FileWriter(this.file)) {
                this.yaml.serialize(this.root, fileWriter);
            }
    }

    @Override
    public void strip() throws IOException {
        if (this.yaml == null)
            throw new IllegalStateException();
        Util.createFileAndPath(this.file);

        if (this.stripNodes("", this.root, this.getPaths()))
            try (FileWriter fileWriter = new FileWriter(this.file)) {
                this.yaml.serialize(this.root, fileWriter);
            }
    }

    @Override
    public void close() {
        this.yaml = null;
        this.root = null;
    }

    protected void loadValues(@NotNull String path, @NotNull MappingNode node) {
        for (NodeTuple tuple : node.getValue()) {
            String fieldPath = (path.isEmpty() ? "" : path + ".") + ((ScalarNode) tuple.getKeyNode()).getValue();
            if (tuple.getValueNode() instanceof MappingNode) {
                loadValues(fieldPath, (MappingNode) tuple.getValueNode());
            } else if (tuple.getValueNode() instanceof ScalarNode) {
                Object value = CONSTRUCTOR.constructObject(tuple.getValueNode());
                if (value == null)
                    continue;
                WritableConfigEntry<?> entry = (WritableConfigEntry<?>) this.getEntries().get(fieldPath);
                if (entry != null)
                    entry.putValue(value);
            }
        }
    }

    private void putNode(@NotNull ConfigEntry<?> entry) {
        String[] pathSplit = entry.getPath().split("\\.");
        MappingNode node = this.root;
        pathLoop: for (int i = 0; i < pathSplit.length; i++) {
            for (NodeTuple tuple : node.getValue()) {
                if (((ScalarNode) tuple.getKeyNode()).getValue().equals(pathSplit[i])) {
                    // Node exists
                    if (i != pathSplit.length - 1) {
                        node = (MappingNode) tuple.getValueNode();
                        continue pathLoop;
                    } else {
                        node.getValue().replaceAll(nodeTuple -> {
                            if (nodeTuple != tuple)
                                return nodeTuple;
                            return this.newNodeTuple(pathSplit[pathSplit.length - 1], entry.getDescription(), entry.getValue());
                        });
                        return;
                    }
                }
            }

            // Node does not exist
            if (i != pathSplit.length - 1) {
                MappingNode newNode = new MappingNode(Tag.MAP, new ArrayList<>(), DumperOptions.FlowStyle.BLOCK);
                node.getValue().add(new NodeTuple(this.yaml.represent(pathSplit[i]), newNode));
                node = newNode;
            } else {
                node.getValue().add(this.newNodeTuple(pathSplit[pathSplit.length - 1], entry.getDescription(), entry.getValue()));
                return;
            }
        }
    }

    protected @NotNull NodeTuple newNodeTuple(@NotNull String path, @Nullable String comment, @NotNull Object value) {
        Node keyNode = this.yaml.represent(path);
        if (comment != null)
            keyNode.setBlockComments(Collections.singletonList(new CommentLine(null, null, " " + comment, CommentType.BLOCK)));
        Node valueNode = this.yaml.represent(value);
        return new NodeTuple(keyNode, valueNode);
    }

    protected boolean stripNodes(@NotNull String path, @NotNull MappingNode node, @NotNull Set<String> paths) {
        boolean changed = false;
        List<NodeTuple> toRemove = new ArrayList<>();
        for (NodeTuple tuple : node.getValue()) {
            String fieldPath = (path.isEmpty() ? "" : path + ".") + ((ScalarNode) tuple.getKeyNode()).getValue();
            if (tuple.getValueNode() instanceof MappingNode) {
                MappingNode child = (MappingNode) tuple.getValueNode();
                changed |= this.stripNodes(fieldPath, child, paths);
                if (child.getValue().isEmpty())
                    toRemove.add(tuple);
            } else if (tuple.getValueNode() instanceof ScalarNode) {
                if (!paths.contains(fieldPath))
                    toRemove.add(tuple);
            }
        }
        if (!toRemove.isEmpty()) {
            changed = true;
            for (NodeTuple tuple : toRemove) {
                node.getValue().remove(tuple);
            }
        }
        return changed;
    }

    public static @NotNull YamlConfig of(@NotNull File file, @Nullable String description) {
        return new YamlConfig(file, description);
    }

    private static class CustomConstructor extends SafeConstructor {

        public CustomConstructor() {
            super(new LoaderOptions().setProcessComments(true));
        }

        @Override
        protected Object constructObject(Node node) {
            return super.constructObject(node);
        }
    }
}
