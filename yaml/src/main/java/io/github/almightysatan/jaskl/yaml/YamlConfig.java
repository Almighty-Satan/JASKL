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

package io.github.almightysatan.jaskl.yaml;

import io.github.almightysatan.jaskl.impl.ConfigImpl;
import io.github.almightysatan.jaskl.impl.Util;
import io.github.almightysatan.jaskl.impl.WritableConfigEntry;
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
import java.math.BigDecimal;
import java.util.*;

public class YamlConfig extends ConfigImpl {

    private static final DumperOptions DUMPER_OPTIONS;
    private static final CustomConstructor CONSTRUCTOR = new CustomConstructor();
    private static final Representer REPRESENTER;
    private static final Representer VALUE_REPRESENTER;

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
        this.yaml = new Yaml(CONSTRUCTOR, REPRESENTER, DUMPER_OPTIONS);
        this.reload();
    }

    @Override
    public void reload() throws IOException, IllegalStateException {
        if (this.yaml == null)
            throw new IllegalStateException();
        if (!this.file.exists()) {
            this.createRoot();
            return;
        }

        try (FileReader fileReader = new FileReader(this.file)) {
            this.root = (MappingNode) this.yaml.compose(fileReader);
            if (this.root == null)
                this.createRoot();
            this.loadValues("", this.root);
        }
    }

    @Override
    public void write() throws IOException {
        if (this.yaml == null)
            throw new IllegalStateException();
        Util.createFileAndPath(this.file);

        this.setComment(this.root, this.getDescription());

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

    protected void createRoot() {
        this.root = new MappingNode(Tag.MAP, new ArrayList<>(), DumperOptions.FlowStyle.BLOCK);
    }

    protected void loadValues(@NotNull String path, @NotNull MappingNode node) {
        for (NodeTuple tuple : node.getValue()) {
            Node valueNode = tuple.getValueNode();
            String fieldPath = (path.isEmpty() ? "" : path + ".") + ((ScalarNode) tuple.getKeyNode()).getValue();
            if (valueNode instanceof MappingNode) {
                if (!this.loadValueIfEntryExists(fieldPath, valueNode))
                    this.loadValues(fieldPath, (MappingNode) valueNode);
            } else if (valueNode instanceof ScalarNode || valueNode instanceof SequenceNode)
                this.loadValueIfEntryExists(fieldPath, valueNode);
        }
    }

    /**
     * Sets the value of a config entry if it exists
     *
     * @param path The path of the entry
     * @param node The value
     * @return true if the entry exists
     */
    protected boolean loadValueIfEntryExists(String path, Node node) {
        WritableConfigEntry<?> entry = (WritableConfigEntry<?>) this.getEntries().get(path);
        if (entry == null)
            return false;
        Object value;
        if (node.getTag() == Tag.FLOAT && node instanceof ScalarNode)
            value = new BigDecimal(((ScalarNode) node).getValue());
        else
            value = CONSTRUCTOR.constructObject(node);
        if (value != null)
            entry.putValue(value);
        return true;
    }

    protected void putNode(@NotNull WritableConfigEntry<?> entry) {
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
                            return this.newNodeTuple(pathSplit[pathSplit.length - 1], entry.getDescription(), entry.getValueToWrite());
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
                node.getValue().add(this.newNodeTuple(pathSplit[pathSplit.length - 1], entry.getDescription(), entry.getValueToWrite()));
                return;
            }
        }
    }

    protected @NotNull NodeTuple newNodeTuple(@NotNull String path, @Nullable String comment, @NotNull Object value) {
        Node keyNode = this.yaml.represent(path);
        this.setComment(keyNode, comment);
        Node valueNode = VALUE_REPRESENTER.represent(value);
        return new NodeTuple(keyNode, valueNode);
    }

    protected void setComment(@NotNull Node node, @Nullable String comment) {
        if (comment != null)
            node.setBlockComments(Collections.singletonList(new CommentLine(null, null, " " + comment, CommentType.BLOCK)));
        else
            node.setBlockComments(new ArrayList<>(0)); // Remove comment
    }

    protected boolean stripNodes(@NotNull String path, @NotNull MappingNode node, @NotNull Set<String> paths) {
        boolean changed = false;
        List<NodeTuple> toRemove = new ArrayList<>();
        for (NodeTuple tuple : node.getValue()) {
            Node valueNode = tuple.getValueNode();
            String fieldPath = (path.isEmpty() ? "" : path + ".") + ((ScalarNode) tuple.getKeyNode()).getValue();
            if (valueNode instanceof MappingNode) {
                if (paths.contains(fieldPath))
                    continue;
                MappingNode child = (MappingNode) valueNode;
                changed |= this.stripNodes(fieldPath, child, paths);
                if (child.getValue().isEmpty())
                    toRemove.add(tuple);
            } else if (valueNode instanceof ScalarNode || valueNode instanceof SequenceNode) {
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

    /**
     * Creates a new {@link YamlConfig} instance.
     *
     * @param file        The yaml file. The file will be created automatically if it does not already exist.
     * @param description The description (comment) of this config file.
     * @return A new {@link YamlConfig} instance.
     */
    public static @NotNull YamlConfig of(@NotNull File file, @Nullable String description) {
        return new YamlConfig(file, description);
    }

    /**
     * Creates a new {@link YamlConfig} instance.
     *
     * @param file The yaml file. The file will be created automatically if it does not already exist.
     * @return A new {@link YamlConfig} instance.
     */
    public static @NotNull YamlConfig of(@NotNull File file) {
        return new YamlConfig(file, null);
    }

    static {
        DUMPER_OPTIONS = new DumperOptions();
        DUMPER_OPTIONS.setProcessComments(true);
        DUMPER_OPTIONS.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        REPRESENTER = new Representer(DUMPER_OPTIONS);
        VALUE_REPRESENTER = new ValueRepresenter(DUMPER_OPTIONS);
    }

    private static class ValueRepresenter extends Representer {

        public ValueRepresenter(DumperOptions options) {
            super(options);
            this.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
        }

        @Override
        protected Node representScalar(Tag tag, String value, DumperOptions.ScalarStyle style) {
            return super.representScalar(tag, value, style == null && tag == Tag.STR ? DumperOptions.ScalarStyle.DOUBLE_QUOTED : style);
        }
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
