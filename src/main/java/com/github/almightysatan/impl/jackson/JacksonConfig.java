package com.github.almightysatan.impl.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.almightysatan.impl.ConfigImpl;
import com.github.almightysatan.impl.WritableConfigEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public abstract class JacksonConfig extends ConfigImpl {

    private final ObjectMapper mapper;
    private final File file;
    private JsonNode root;

    public JacksonConfig(@NotNull ObjectMapper mapper, @NotNull File file, @Nullable String description) {
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

        this.root = this.mapper.readTree(this.file);
        for (WritableConfigEntry<?> configEntry : this.getCastedValues()) {
            JsonNode node = this.resolveNode(configEntry.getPath());
            if (node == null)
                return;

            Object value = this.mapper.treeToValue(node, Object.class);
            configEntry.putValue(value);
        }
    }

    @Override
    public void write() throws IOException {
        if (this.root == null)
            throw new IllegalStateException();
        if (!this.file.exists()) {
            if (!this.file.getParentFile().exists())
                this.file.getParentFile().mkdirs();
            this.file.createNewFile();
        }

        for (WritableConfigEntry<?> configEntry : this.getCastedValues()) {
            this.putNode(configEntry.getPath(), this.mapper.valueToTree(configEntry.getValue()));
        }
        this.mapper.writeValue(this.file, this.root);
    }

    @Override
    public void populate() throws IOException {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void strip() throws IOException {
        throw new UnsupportedOperationException("TODO");
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

    protected void putNode(@NotNull String path, JsonNode value) {
        String[] pathSplit = path.split("\\.");
        ObjectNode node = (ObjectNode) this.root;
        for (int i = 0; i < pathSplit.length - 1; i++) {
            ObjectNode child = (ObjectNode) node.get(pathSplit[i]);
            node = child == null ? node.putObject(pathSplit[i]) : child;
        }
        node.set(pathSplit[pathSplit.length - 1], value);
    }
}
