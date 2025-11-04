package com.thebiggestdata.search.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {

    @Value("${mongodb.uri:mongodb://localhost:27017/}")
    private String mongoUri;

    @Value("${mongodb.inverted-index.database:inverted_index}")
    private String invertedIndexDatabase;

    @Value("${mongodb.inverted-index.collection:words}")
    private String invertedIndexCollection;

    @Value("${mongodb.metadata.database:metadata}")
    private String metadataDatabase;

    @Value("${mongodb.metadata.collection:books}")
    private String metadataCollection;

    public String getMongoUri() {
        return mongoUri;
    }

    public String getInvertedIndexDatabase() {
        return invertedIndexDatabase;
    }

    public String getInvertedIndexCollection() {
        return invertedIndexCollection;
    }

    public String getMetadataDatabase() {
        return metadataDatabase;
    }

    public String getMetadataCollection() {
        return metadataCollection;
    }
}