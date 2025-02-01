package com.abl.gss.configuration;

import com.mongodb.ConnectionString;
import com.mongodb.reactivestreams.client.MongoClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class MongoConfigurationTests {

    @InjectMocks
    private MongoConfiguration configuration;

    @Mock
    private MongoConfigurationProperties properties;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void connectionString_sanity_matchingText() {
        String connectionString = "mongodb://_";

        doReturn(connectionString).when(properties).getConnectionString();

        ConnectionString output = configuration.connectionString();
        assertEquals(connectionString, output.getConnectionString());

        verify(properties).getConnectionString();
    }

    @Test
    public void mongoClient_sanity_notNull(){
        ConnectionString connectionString = new ConnectionString("mongodb://_");

        MongoClient client = configuration.mongoClient(connectionString);
        assertNotNull(client);
    }


    @Test
    public void reactiveMongoDatabaseFactory_sanity_notNull() {
        String databaseName = "_";
        MongoClient client = mock(MongoClient.class);

        doReturn(databaseName).when(properties).getDatabaseName();

        ReactiveMongoDatabaseFactory factory = configuration.reactiveMongoDatabaseFactory(client);

        assertNotNull(factory);
        verify(properties).getDatabaseName();
    }

    @Test
    public void reactiveMongoTemplate_sanity_notNull() {
        ReactiveMongoTemplate template = configuration.reactiveMongoTemplate(mock(ReactiveMongoDatabaseFactory.class));
        assertNotNull(template);
    }
}
