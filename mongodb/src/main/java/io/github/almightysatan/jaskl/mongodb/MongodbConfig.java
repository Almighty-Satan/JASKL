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

package io.github.almightysatan.jaskl.mongodb;

import io.github.almightysatan.jaskl.impl.ConfigImpl;
import io.github.almightysatan.jaskl.impl.WritableConfigEntry;
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

    private final String address;
    private final String database;
    private final String collection;
    private MongoClient mongoClient;
    private MongoCollection<Document> mongoCollection;


    private MongodbConfig(@NotNull String address, @NotNull String database, @NotNull String collection) {
        super(null);
        this.address = Objects.requireNonNull(address);
        this.database = Objects.requireNonNull(database);
        this.collection = Objects.requireNonNull(collection);
    }

    @Override
    public void load() throws IOException, IllegalStateException {
        if (this.mongoClient != null) {
            throw new IllegalStateException();
        }
        this.mongoClient = MongoClients.create("mongodb://" + address);
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
                document.put("value", configEntry.getValueToWrite());

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

    /**
     * Creates a new {@link MongodbConfig} instance.
     *
     * @param address The address of the database. Example: {@code username:password@localhost:27017}
     * @param database The name of the database
     * @param collection The name of the collection
     * @return A new {@link MongodbConfig} instance
     */
    public static MongodbConfig of(@NotNull String address, @NotNull String database, @NotNull String collection) {
        return new MongodbConfig(address, database, collection);
    }
}
