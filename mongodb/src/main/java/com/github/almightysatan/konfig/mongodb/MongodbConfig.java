package com.github.almightysatan.konfig.mongodb;

import com.github.almightysatan.konfig.impl.ConfigImpl;
import com.github.almightysatan.konfig.impl.WritableConfigEntry;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.*;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

public class MongodbConfig extends ConfigImpl {

    private final static UpdateOptions UPDATE_OPTIONS = new UpdateOptions().upsert(true);

    private final String ip;
    private final String database;
    private final String collection;
    private MongoClient mongoClient;
    private MongoCollection<Document> mongoCollection;


    private MongodbConfig(@NotNull String ip, @NotNull String database, @NotNull String collection) {
        super(null);
        this.ip = Objects.requireNonNull(ip);
        this.database = Objects.requireNonNull(database);
        this.collection = Objects.requireNonNull(collection);
    }

    @Override
    public void load() throws IOException, IllegalStateException {
        if (this.mongoClient != null) {
            throw new IllegalStateException();
        }
        this.mongoClient = MongoClients.create("mongodb://" + ip);
        this.mongoCollection = this.mongoClient.getDatabase(database).getCollection(this.collection);

        this.reload();
    }

    @Override
    public void reload() throws IOException, IllegalStateException {
        if (this.mongoCollection == null)
            throw new IllegalStateException();

        Map<String, Document> entries = new HashMap<>();
        try {
            FindIterable<Document> documents = this.mongoCollection.find();
            for (Document document : documents)
                entries.put(document.getString("_id"), document);
        } catch (MongoException e) {
            throw new IOException(e);
        }

        for (WritableConfigEntry<?> configEntry : this.getCastedValues()) {
            Document document = entries.get(configEntry.getPath());

            if (document != null) {
                Object value = document.get("value");
                if (value != null)
                    configEntry.putValue(value);
            }
        }
    }

    @Override
    public void write() throws IOException {
        if (this.mongoCollection == null)
            throw new IllegalStateException();

        List<WriteModel<? extends Document>> writeModels = new ArrayList<>();
        for (WritableConfigEntry<?> configEntry : this.getCastedValues())
            if (configEntry.isModified()) {
                Document document = new Document();
                document.put("value", configEntry.getValue());

                Document updateDocument = new Document();
                updateDocument.put("$set", document);

                writeModels.add(new UpdateOneModel<>(Filters.eq("_id", configEntry.getPath()), updateDocument, UPDATE_OPTIONS));
            }
        if (!writeModels.isEmpty())
            try {
                this.mongoCollection.bulkWrite(writeModels);
            } catch (MongoException e) {
                throw new IOException(e);
            }
    }

    @Override
    public void strip() throws IOException {
        if (this.mongoCollection == null)
            throw new IllegalStateException();

        try {
            Set<String> paths = this.getPaths();
            List<WriteModel<? extends Document>> writeModels = new ArrayList<>();
            FindIterable<Document> documents = this.mongoCollection.find();
            for (Document document : documents) {
                String path = document.getString("_id");
                if (!paths.contains(path))
                    writeModels.add(new DeleteOneModel<>(Filters.eq("_id", path)));
            }
            if (!writeModels.isEmpty())
                this.mongoCollection.bulkWrite(writeModels);
        } catch (MongoException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void close() {
        if (this.mongoClient != null) {
            this.mongoClient.close();
            this.mongoClient = null;
            this.mongoCollection = null;
        }
    }

    public static MongodbConfig of(@NotNull String ip, @NotNull String database, @NotNull String collection) {
        return new MongodbConfig(ip, database, collection);
    }
}
