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

package io.github.almightysatan.jaskl.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.github.almightysatan.jaskl.Config;
import io.github.almightysatan.jaskl.test.ConfigTest;
import org.bson.Document;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;

import java.util.*;

public class MongodbConfigTest extends ConfigTest {

    private static final String DATABASE = "JASKL_Test";
    private static final String COLLECTION_EMPTY = "empty";
    private static final String COLLECTION_EXAMPLE = "example";
    private static final String COLLECTION_TEST = "test";

    private static String mongoAddress;

    @BeforeAll
    public static void checkEnv() {
        Assumptions.assumeTrue((mongoAddress = System.getenv("MONGO_ADDRESS")) != null);

        try (MongoClient mongoClient = MongoClients.create("mongodb://" + mongoAddress)) {
            MongoDatabase database = mongoClient.getDatabase(DATABASE);
            database.drop();
            MongoCollection<Document> collection = database.getCollection(COLLECTION_EXAMPLE);

            Map<String, String> map = new HashMap<>();
            map.put("Hello", "there");
            map.put("abc", "xyz");
            map.put("x", "y");

            List<Document> documents = new ArrayList<>();
            documents.add(new Document().append("_id", "example.boolean").append("value", true));
            documents.add(new Document().append("_id", "example.double").append("value", 1.0));
            documents.add(new Document().append("_id", "example.float").append("value", 1.0));
            documents.add(new Document().append("_id", "example.integer").append("value", 1));
            documents.add(new Document().append("_id", "example.long").append("value", 1L));
            documents.add(new Document().append("_id", "example.string").append("value", "modified"));
            documents.add(new Document().append("_id", "example.special-char_entry").append("value", "spe-ci_al"));
            documents.add(new Document().append("_id", "example.enum").append("value", "ANOTHER_EXAMPLE"));
            documents.add(new Document().append("_id", "example.list").append("value", Arrays.asList("Example3", "Example4")));
            documents.add(new Document().append("_id", "example.map").append("value", map));

            collection.insertMany(documents);
        }
    }

    @Override
    protected Config createEmptyConfig() {
        return MongodbConfig.of(mongoAddress, DATABASE, COLLECTION_EMPTY);
    }

    @Override
    protected Config createExampleConfig() {
        return MongodbConfig.of(mongoAddress, DATABASE, COLLECTION_EXAMPLE);
    }

    @Override
    protected Config createTestConfig() {
        return MongodbConfig.of(mongoAddress, DATABASE, COLLECTION_TEST);
    }

    @Override
    protected void clearTestConfig() {
        try (MongoClient mongoClient = MongoClients.create("mongodb://" + mongoAddress)) {
            MongoDatabase database = mongoClient.getDatabase(DATABASE);
            MongoCollection<Document> collection = database.getCollection(COLLECTION_TEST);
            collection.drop();
        }
    }

    @Override
    protected boolean testConfigExists() {
        return true; // doesn't really matter
    }
}
