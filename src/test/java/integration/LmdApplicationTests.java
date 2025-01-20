package integration;

import com.abl.live.market.data.stubs.LiveMarketDataServiceGrpc;
import com.abl.lmd.LmdApplication;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.grpc.autoconfigure.client.GrpcClientAutoConfiguration;
import org.springframework.grpc.autoconfigure.server.GrpcServerAutoConfiguration;
import org.springframework.grpc.autoconfigure.server.GrpcServerFactoryAutoConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mongodb.MongoDBAtlasLocalContainer;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers
@SpringBootTest
@DirtiesContext
@SpringJUnitConfig(classes = {IntegrationTestsConfiguration.class, LmdApplication.class})
@ImportAutoConfiguration({
        GrpcServerAutoConfiguration.class,
        GrpcServerFactoryAutoConfiguration.class,
        GrpcClientAutoConfiguration.class
})
@ActiveProfiles("local")
class LmdApplicationTests {

    @Autowired
    private LiveMarketDataServiceGrpc.LiveMarketDataServiceStub stub;

	@Container
	static MongoDBContainer mongoDbContainer =
			new MongoDBContainer(DockerImageName.parse("mongo:latest"))
						.withExposedPorts(27017);

	@DynamicPropertySource
	static void mongoDbProperties(DynamicPropertyRegistry registry) {
		//This override the application.yaml setup
		registry.add("mongo.connection-string", mongoDbContainer::getConnectionString);
	}

	@Test
	void contextLoads() {
		assertNotNull(stub);
		assertNotNull(mongoDbContainer);
	}


}
