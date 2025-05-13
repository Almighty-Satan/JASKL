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

import io.github.almightysatan.jaskl.ExceptionHandler;
import io.github.almightysatan.jaskl.Resource;
import io.github.almightysatan.jaskl.impl.ConfigImpl;
import io.github.almightysatan.jaskl.impl.WritableConfigEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.comments.CommentLine;
import org.yaml.snakeyaml.comments.CommentType;
import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.ConstructorException;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.nodes.*;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;

public class YamlConfig extends ConfigImpl {

    private static final CustomConstructor CONSTRUCTOR = new CustomConstructor();

    private final Resource resource;
    private final DumperOptions dumperOptions;
    private final Representer representer;
    private final Representer valueRepresenter;
    private Yaml yaml;
    private MappingNode root;

    private YamlConfig(@NotNull Resource resource, @Nullable String description,
           @Nullable ExceptionHandler exceptionHandler, @NotNull DumperOptions dumperOptions) {
        super(description, exceptionHandler);
        this.dumperOptions = Objects.requireNonNull(dumperOptions);
        this.representer = new Representer(dumperOptions);
        this.valueRepresenter = new ValueRepresenter(dumperOptions);
        this.resource = Objects.requireNonNull(resource);
    }

    @Override
    public void load() throws IOException, IllegalStateException {
        if (this.yaml != null)
            throw new IllegalStateException();
        this.yaml = new Yaml(CONSTRUCTOR, this.representer, this.dumperOptions);
        this.reload();
    }

    @Override
    public void reload() throws IOException, IllegalStateException {
        if (this.yaml == null)
            throw new IllegalStateException();
        if (!this.resource.exists()) {
            this.createRoot();
            return;
        }

        try (Reader reader = this.resource.getReader()) {
            this.root = (MappingNode) this.yaml.compose(reader);
            if (this.root == null)
                this.createRoot();
            this.loadValues("", this.root);
        }
    }

    @Override
    public void write() throws IOException {
        if (this.yaml == null)
            throw new IllegalStateException();
        this.resource.createIfNotExists();

        this.setComment(this.root, this.getDescription());

        boolean shouldWrite = false;
        for (WritableConfigEntry<?> configEntry : this.getCastedValues()) {
            if (configEntry.isModified()) {
                this.putNode(configEntry);
                shouldWrite = true;
            }
        }

        if (shouldWrite)
            try (Writer writer = this.resource.getWriter()) {
                this.yaml.serialize(this.root, writer);
            }
    }

    @Override
    public @Unmodifiable @NotNull Set<@NotNull String> prune() throws IOException {
        if (this.yaml == null)
            throw new IllegalStateException();
        if (!this.resource.exists())
            return Collections.emptySet();

        Set<String> removedPaths = new HashSet<>();
        if (this.stripNodes("", this.root, this.getPaths(), removedPaths))
            try (Writer writer = this.resource.getWriter()) {
                this.yaml.serialize(this.root, writer);
            }
        return Collections.unmodifiableSet(removedPaths);
    }

    @Override
    public void close() {
        this.yaml = null;
        this.root = null;
    }

    @Override
    public boolean isReadOnly() throws IOException {
        return this.resource.isReadOnly();
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
        Object value = CONSTRUCTOR.constructObject(node);
        if (value != null)
            entry.putValue(value, this.getExceptionHandler());
        return true;
    }

    protected void putNode(@NotNull WritableConfigEntry<?> entry) {
        String[] pathSplit = entry.getPath().split("\\.");
        MappingNode node = this.root;
        pathLoop:
        for (int i = 0; i < pathSplit.length; i++) {
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
        Node valueNode = this.valueRepresenter.represent(value);
        return new NodeTuple(keyNode, valueNode);
    }

    protected void setComment(@NotNull Node node, @Nullable String comment) {
        if (comment != null)
            node.setBlockComments(Collections.singletonList(new CommentLine(null, null, " " + comment, CommentType.BLOCK)));
        else
            node.setBlockComments(new ArrayList<>(0)); // Remove comment
    }

    protected boolean stripNodes(@NotNull String path, @NotNull MappingNode node, @NotNull Set<String> paths, @NotNull Set<String> removedPaths) {
        boolean changed = false;
        List<NodeTuple> toRemove = new ArrayList<>();
        for (NodeTuple tuple : node.getValue()) {
            Node valueNode = tuple.getValueNode();
            String fieldPath = (path.isEmpty() ? "" : path + ".") + ((ScalarNode) tuple.getKeyNode()).getValue();
            if (valueNode instanceof MappingNode) {
                if (paths.contains(fieldPath))
                    continue;
                MappingNode child = (MappingNode) valueNode;
                changed |= this.stripNodes(fieldPath, child, paths, removedPaths);
                if (child.getValue().isEmpty())
                    toRemove.add(tuple);
            } else if (valueNode instanceof ScalarNode || valueNode instanceof SequenceNode) {
                if (!paths.contains(fieldPath)) {
                    toRemove.add(tuple);
                    removedPaths.add(fieldPath);
                }
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
     * @param resource         A resource containing a yaml configuration. The resource will be created automatically if it
     *                         does not already exist and {@link #isReadOnly()} is {@code false}.
     * @param description      The description (comment) of this config file.
     * @param exceptionHandler The {@link ExceptionHandler}
     * @param dumperOptions    The {@link DumperOptions}
     * @return A new {@link YamlConfig} instance.
     */
    public static @NotNull YamlConfig of(@NotNull Resource resource, @Nullable String description,
             @Nullable ExceptionHandler exceptionHandler, @NotNull DumperOptions dumperOptions) {
        return new YamlConfig(resource, description, exceptionHandler, dumperOptions);
    }

    /**
     * Creates a new {@link YamlConfig} instance.
     *
     * @param resource         A resource containing a yaml configuration. The resource will be created automatically if it
     *                         does not already exist and {@link #isReadOnly()} is {@code false}.
     * @param description      The description (comment) of this config file.
     * @param exceptionHandler The {@link ExceptionHandler}
     * @return A new {@link YamlConfig} instance.
     */
    public static @NotNull YamlConfig of(@NotNull Resource resource, @Nullable String description,
             @Nullable ExceptionHandler exceptionHandler) {
        return of(resource, description, exceptionHandler, getDefaultDumperOptions());
    }

    /**
     * Creates a new {@link YamlConfig} instance.
     *
     * @param resource    A resource containing a yaml configuration. The resource will be created automatically if it
     *                    does not already exist and {@link #isReadOnly()} is {@code false}.
     * @param description The description (comment) of this config file.
     * @return A new {@link YamlConfig} instance.
     */
    public static @NotNull YamlConfig of(@NotNull Resource resource, @Nullable String description) {
        return of(resource, description, null);
    }

    /**
     * Creates a new {@link YamlConfig} instance.
     *
     * @param resource A resource containing a yaml configuration. The resource will be created automatically if it does
     *                 not already exist and {@link #isReadOnly()} is {@code false}.
     * @return A new {@link YamlConfig} instance.
     */
    public static @NotNull YamlConfig of(@NotNull Resource resource) {
        return of(resource, null);
    }

    /**
     * Creates a new {@link YamlConfig} instance.
     *
     * @param file             The yaml file. The file will be created automatically if it does not already exist.
     * @param description      The description (comment) of this config file.
     * @param exceptionHandler The {@link ExceptionHandler}
     * @return A new {@link YamlConfig} instance.
     */
    public static @NotNull YamlConfig of(@NotNull File file, @Nullable String description, @Nullable ExceptionHandler exceptionHandler) {
        return of(Resource.of(file), description, exceptionHandler);
    }

    /**
     * Creates a new {@link YamlConfig} instance.
     *
     * @param file        The yaml file. The file will be created automatically if it does not already exist.
     * @param description The description (comment) of this config file.
     * @return A new {@link YamlConfig} instance.
     */
    public static @NotNull YamlConfig of(@NotNull File file, @Nullable String description) {
        return of(Resource.of(file), description);
    }

    /**
     * Creates a new {@link YamlConfig} instance.
     *
     * @param file The yaml file. The file will be created automatically if it does not already exist.
     * @return A new {@link YamlConfig} instance.
     */
    public static @NotNull YamlConfig of(@NotNull File file) {
        return of(file, null);
    }

    /**
     * Returns a new instance of {@link DumperOptions}
     *
     * @return a new instance of {@link DumperOptions}
     */
    public static DumperOptions getDefaultDumperOptions() {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setProcessComments(true);
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        dumperOptions.setSplitLines(false);
        return dumperOptions;
    }

    private static class ValueRepresenter extends Representer {

        public ValueRepresenter(DumperOptions options) {
            super(options);
            this.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
        }

        @Override
        protected Node representScalar(Tag tag, String value, DumperOptions.ScalarStyle style) {
            // Force double quotes instead of plain or single quotes
            return super.representScalar(tag, value, tag == Tag.STR && (style == null
                    || style == DumperOptions.ScalarStyle.PLAIN || style == DumperOptions.ScalarStyle.SINGLE_QUOTED)
                    ? DumperOptions.ScalarStyle.DOUBLE_QUOTED : style);
        }
    }

    private static class CustomConstructor extends SafeConstructor {

        public CustomConstructor() {
            super(new LoaderOptions().setProcessComments(true));
            this.yamlConstructors.put(Tag.FLOAT, new ConstructYamlFloat());
        }

        @Override
        protected Object constructObject(Node node) {
            return super.constructObject(node);
        }

        private class ConstructYamlFloat extends AbstractConstruct {

            @Override
            public Object construct(Node node) {
                String value = constructScalar((ScalarNode) node).replace("_", "").toLowerCase();
                if (value.isEmpty())
                    throw new JasklConstructorException("while constructing a BigDecimal", node.getStartMark(), "found empty value", node.getStartMark());
                switch (value) {
                    case ".inf":
                    case "+.inf":
                        return Double.POSITIVE_INFINITY;
                    case "-.inf":
                        return Double.NEGATIVE_INFINITY;
                    case ".nan":
                    case "+.nan":
                    case "-.nan":
                        return Double.NaN;
                }
                return new BigDecimal(value);
            }
        }

        private static class JasklConstructorException extends ConstructorException {
            protected JasklConstructorException(String context, Mark contextMark, String problem, Mark problemMark) {
                super(context, contextMark, problem, problemMark);
            }
        }
    }
}
