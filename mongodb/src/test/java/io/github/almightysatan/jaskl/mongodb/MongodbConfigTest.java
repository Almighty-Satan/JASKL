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

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;

import static io.github.almightysatan.test.ConfigTest.*;
import static io.github.almightysatan.test.ConfigTest.testCustom;

public class MongodbConfigTest {
    
    private static final String DATABASE = "JASKL_Test";
    private static final String COLLECTION_EXAMPLE = "example";
    private static final String COLLECTION_0 = "test0";
    private static final String COLLECTION_1 = "test1";
    private static final String COLLECTION_2 = "test2";
    private static final String COLLECTION_3 = "test3";
    private static final String COLLECTION_4 = "test4";
    private static final String COLLECTION_5 = "test5";
    private static final String COLLECTION_6 = "test6";

    private static String mongoAddress;

    @BeforeAll
    public static void checkEnv() throws IOException {
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
            documents.add(new Document().append("_id", "example.enum").append("value", "ANOTHER_EXAMPLE"));
            documents.add(new Document().append("_id", "example.list").append("value", Arrays.asList("Example3", "Example4")));
            documents.add(new Document().append("_id", "example.map").append("value", map));

            collection.insertMany(documents);
        }
    }

    @Test
    public void testLoadMongo() throws IOException {
        testLoad(() -> MongodbConfig.of(mongoAddress, DATABASE, COLLECTION_EXAMPLE));
    }

    @Test
    public void testLoadAfterClosedMongo() throws IOException {
        testLoadAfterClosed(() -> MongodbConfig.of(mongoAddress, DATABASE, COLLECTION_EXAMPLE));
    }

    @Test
    public void testAlreadyLoadedMongo() throws IOException {
        testAlreadyLoaded(() -> MongodbConfig.of(mongoAddress, DATABASE, COLLECTION_EXAMPLE));
    }

    @Test
    public void testLoadValuesMongo() throws IOException {
        testLoadValues(() -> MongodbConfig.of(mongoAddress, DATABASE, COLLECTION_EXAMPLE));
    }

    @Test
    public void testValidationMongo() throws IOException {
        testValidation(() -> MongodbConfig.of(mongoAddress, DATABASE, COLLECTION_EXAMPLE));
    }

    @Test
    public void testEnumValuesMongo() throws IOException {
        testEnumValues(() -> MongodbConfig.of(mongoAddress, DATABASE, COLLECTION_EXAMPLE));
    }

    @Test
    public void testListValuesMongo() throws IOException {
        testListValues(() -> MongodbConfig.of(mongoAddress, DATABASE, COLLECTION_EXAMPLE));
    }

    @Test
    public void testMapValuesMongo() throws IOException {
        testMapValues(() -> MongodbConfig.of(mongoAddress, DATABASE, COLLECTION_EXAMPLE));
    }

    @Test
    public void testInvalidPathsMongo() throws IOException {
        testInvalidPaths(() -> MongodbConfig.of(mongoAddress, DATABASE, COLLECTION_EXAMPLE));
    }

    @Test
    public void testWriteAndLoadMongo() throws IOException {
        testWriteAndLoad(() -> MongodbConfig.of(mongoAddress, DATABASE, COLLECTION_0), null);
    }

    @Test
    public void testWriteAndLoadListMongo() throws IOException {
        testWriteAndLoadList(() -> MongodbConfig.of(mongoAddress, DATABASE, COLLECTION_1), null);
    }

    @Test
    public void testWriteAndLoadList2Mongo() throws IOException {
        testWriteAndLoadList2(() -> MongodbConfig.of(mongoAddress, DATABASE, COLLECTION_2), null);
    }

    @Test
    public void testWriteAndLoadListEnumMongo() throws IOException {
        testWriteAndLoadListEnum(() -> MongodbConfig.of(mongoAddress, DATABASE, COLLECTION_3), null);
    }

    @Test
    public void testWriteAndLoadMapMongo() throws IOException {
        testWriteAndLoadMap(() -> MongodbConfig.of(mongoAddress, DATABASE, COLLECTION_4), null);
    }

    @Test
    public void testStripMongo() throws IOException {
        testStrip(() -> MongodbConfig.of(mongoAddress, DATABASE, COLLECTION_5), null);
    }

    @Test
    public void testCustomMongo() throws IOException {
        testCustom(() -> MongodbConfig.of(mongoAddress, DATABASE, COLLECTION_6));
    }
}
