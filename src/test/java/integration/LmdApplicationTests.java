package integration;

import com.abl.live.market.data.stubs.*;
import com.abl.lmd.LmdApplication;
import io.grpc.internal.testing.StreamRecorder;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
@SpringJUnitConfig(classes = {IntegrationTestsConfiguration.class, LmdApplication.class})
@ImportAutoConfiguration({
        GrpcServerAutoConfiguration.class,
        GrpcServerFactoryAutoConfiguration.class,
        GrpcClientAutoConfiguration.class
})
@ActiveProfiles("local")
class LmdApplicationTests {

	private static final int SECONDS_TO_WAIT = 5;
	private static final TimeUnit UNIT = TimeUnit.SECONDS;

	@Autowired
	private LiveMarketDataServiceGrpc.LiveMarketDataServiceStub stub;

	@Autowired
	private ReactiveMongoTemplate template;

	@Container
	static MongoDBContainer mongoDbContainer =
			new MongoDBContainer(DockerImageName.parse("mongo:latest"));

	@DynamicPropertySource
	static void mongoDbProperties(DynamicPropertyRegistry registry) {
		//This override the application.yaml setup
		registry.add("mongo.connection-string", mongoDbContainer::getConnectionString);
	}

	@BeforeEach
	void setup() {
		template.getCollectionNames()
				.flatMap(template::dropCollection)
				.blockLast();
	}

	@Test
	void contextLoads() {
		assertNotNull(stub);
		assertNotNull(mongoDbContainer);
	}

	@Test
	@DirtiesContext
	public void get_empty_emptyResponse() throws Exception {
		StreamRecorder<FetchResponse> response = StreamRecorder.create();
		FetchRequest request = FetchRequest.newBuilder()
				.setName("name")
				.build();

		stub.get(request, response);

		assertTrue(response.awaitCompletion(SECONDS_TO_WAIT, UNIT));
		assertEquals(0, response.getValues().size());
	}

	@Test
	@DirtiesContext
	public void update_saveEntityAndQueryIt_entityMatchesExpectedValues() throws Exception {
		String name = "A";
		StreamRecorder<MarketDataResponse> marketDataResponse = StreamRecorder.create();
		MarketDataRequest request = MarketDataRequest.newBuilder()
				.setName(name)
				.setPrice(1L)
				.setTimestamp(222L)
				.build();

		stub.update(request, marketDataResponse);

		assertTrue(marketDataResponse.awaitCompletion(SECONDS_TO_WAIT, UNIT));
		assertTrue(marketDataResponse.getValues().isEmpty());

		StreamRecorder<FetchResponse> fetchResponse = StreamRecorder.create();
		FetchRequest fetchRequest = FetchRequest.newBuilder()
				.setName(name)
				.build();

		stub.get(fetchRequest, fetchResponse);

		assertTrue(fetchResponse.awaitCompletion(SECONDS_TO_WAIT, UNIT));

		assertEquals(1, fetchResponse.getValues().size());
		FetchResponse output = fetchResponse.getValues().get(0);
		assertEquals(request.getName(), output.getName());
		assertEquals(request.getPrice(), output.getPrice());
		assertEquals(request.getTimestamp(), output.getTimestamp());
	}

	@Test
	@DirtiesContext
	public void update_saveTwoEntitiesUnderTheSameName_getTwoEntities() throws Exception {
		String name = "A";
		MarketDataRequest request1 = MarketDataRequest.newBuilder()
				.setName(name)
				.setPrice(1L)
				.setTimestamp(222L)
				.build();

		MarketDataRequest request2 = MarketDataRequest.newBuilder()
				.setName(name)
				.setPrice(3L)
				.setTimestamp(999L)
				.build();

		StreamRecorder<MarketDataResponse> mdResponse = StreamRecorder.create();
		stub.update(request1, mdResponse);
		assertTrue(mdResponse.awaitCompletion(SECONDS_TO_WAIT, UNIT));

		mdResponse = StreamRecorder.create();
		stub.update(request2, mdResponse);
		assertTrue(mdResponse.awaitCompletion(SECONDS_TO_WAIT, UNIT));

		assertEquals(1, mdResponse.getValues().size());
		MarketDataResponse marketData = mdResponse.getValues().get(0);
		assertEquals(MarketDataResponse.Status.APPROVED, marketData.getStatus());

		StreamRecorder<FetchResponse> fetchResponse = StreamRecorder.create();
		FetchRequest fetchRequest = FetchRequest.newBuilder()
				.setName(name)
				.build();

		stub.get(fetchRequest, fetchResponse);

		assertTrue(fetchResponse.awaitCompletion(SECONDS_TO_WAIT, UNIT));
		assertEquals(2, fetchResponse.getValues().size());

		FetchResponse output = fetchResponse.getValues().get(0);
		assertEquals(request2.getName(), output.getName());
		assertEquals(request2.getPrice(), output.getPrice());
		assertEquals(request2.getTimestamp(), output.getTimestamp());

		output = fetchResponse.getValues().get(1);
		assertEquals(request1.getName(), output.getName());
		assertEquals(request1.getPrice(), output.getPrice());
		assertEquals(request1.getTimestamp(), output.getTimestamp());
	}

	@Test
	@DirtiesContext
	public void update_saveTwoEntitiesUnderTheDifferentName_getOneEntities() throws Exception {
		String a = "A";
		String b = "B";
		MarketDataRequest request1 = MarketDataRequest.newBuilder()
				.setName(a)
				.setPrice(1L)
				.setTimestamp(222L)
				.build();

		MarketDataRequest request2 = MarketDataRequest.newBuilder()
				.setName(b)
				.setPrice(3L)
				.setTimestamp(999L)
				.build();

		StreamRecorder<MarketDataResponse> mdResponse = StreamRecorder.create();
		stub.update(request1, mdResponse);
		assertTrue(mdResponse.awaitCompletion(SECONDS_TO_WAIT, UNIT));

		mdResponse = StreamRecorder.create();
		stub.update(request2, mdResponse);
		assertTrue(mdResponse.awaitCompletion(SECONDS_TO_WAIT, UNIT));

		assertTrue(mdResponse.getValues().isEmpty());

		StreamRecorder<FetchResponse> fetchResponse = StreamRecorder.create();
		FetchRequest fetchRequest = FetchRequest.newBuilder()
				.setName(a)
				.build();

		stub.get(fetchRequest, fetchResponse);

		assertTrue(fetchResponse.awaitCompletion(SECONDS_TO_WAIT, UNIT));
		assertEquals(1, fetchResponse.getValues().size());

		FetchResponse output = fetchResponse.getValues().get(0);
		assertEquals(request1.getName(), output.getName());
		assertEquals(request1.getPrice(), output.getPrice());
		assertEquals(request1.getTimestamp(), output.getTimestamp());

		fetchResponse = StreamRecorder.create();
		fetchRequest = FetchRequest.newBuilder()
				.setName(b)
				.build();

		stub.get(fetchRequest, fetchResponse);

		assertTrue(fetchResponse.awaitCompletion(SECONDS_TO_WAIT, UNIT));
		assertEquals(1, fetchResponse.getValues().size());

		output = fetchResponse.getValues().get(0);
		assertEquals(request2.getName(), output.getName());
		assertEquals(request2.getPrice(), output.getPrice());
		assertEquals(request2.getTimestamp(), output.getTimestamp());
	}

	@Test
	@DirtiesContext
	public void getMultiple_empty_emptyList() throws Exception {

		StreamRecorder<FetchMultipleResponse> response = StreamRecorder.create();
		StreamObserver<FetchRequest> requestObserver = stub
				.getMultiple(response);

		requestObserver.onNext(FetchRequest.newBuilder()
				.setName("A")
				.build());

		requestObserver.onNext(FetchRequest.newBuilder()
				.setName("B")
				.build());

		requestObserver.onCompleted();
		assertTrue(response.awaitCompletion(SECONDS_TO_WAIT, UNIT));
		assertFalse(response.getValues().isEmpty());
		assertTrue(response.getValues().get(0).getDataList().isEmpty());
	}

	@Test
	@DirtiesContext
	public void getMultiple_twoUpdatesOfSameNamePlusAnotherOfOtherName_listOf3MatchingValues() throws Exception {
		String a = "A";
		String b = "B";
		MarketDataRequest request1 = MarketDataRequest.newBuilder()
				.setName(a)
				.setPrice(1L)
				.setTimestamp(222L)
				.build();

		MarketDataRequest request2 = MarketDataRequest.newBuilder()
				.setName(b)
				.setPrice(3L)
				.setTimestamp(999L)
				.build();

		MarketDataRequest request3 = MarketDataRequest.newBuilder()
				.setName(a)
				.setPrice(47L)
				.setTimestamp(123567L)
				.build();

		StreamRecorder<MarketDataResponse> mdResponse = StreamRecorder.create();
		stub.update(request1, mdResponse);
		assertTrue(mdResponse.awaitCompletion(SECONDS_TO_WAIT, UNIT));

		mdResponse = StreamRecorder.create();
		stub.update(request2, mdResponse);
		assertTrue(mdResponse.awaitCompletion(SECONDS_TO_WAIT, UNIT));

		mdResponse = StreamRecorder.create();
		stub.update(request3, mdResponse);
		assertTrue(mdResponse.awaitCompletion(SECONDS_TO_WAIT, UNIT));

		StreamRecorder<FetchMultipleResponse> response = StreamRecorder.create();
		StreamObserver<FetchRequest> requestObserver = stub
				.getMultiple(response);

		requestObserver.onNext(FetchRequest.newBuilder()
				.setName(a)
				.build());

		requestObserver.onNext(FetchRequest.newBuilder()
				.setName(b)
				.build());

		requestObserver.onCompleted();
		assertTrue(response.awaitCompletion(SECONDS_TO_WAIT, UNIT));

		assertEquals(1, response.getValues().size());

		FetchMultipleResponse output = response.getValues().get(0);
		assertEquals(3, output.getDataCount());

		assertTrue(output.getDataList().stream().anyMatch(e -> e.getName().equals(request1.getName())));
		assertTrue(output.getDataList().stream().anyMatch(e -> e.getPrice() == request1.getPrice()));
		assertTrue(output.getDataList().stream().anyMatch(e -> e.getTimestamp() == request1.getTimestamp()));

		assertTrue(output.getDataList().stream().anyMatch(e -> e.getName().equals(request2.getName())));
		assertTrue(output.getDataList().stream().anyMatch(e -> e.getPrice() == request2.getPrice()));
		assertTrue(output.getDataList().stream().anyMatch(e -> e.getTimestamp() == request2.getTimestamp()));

		assertTrue(output.getDataList().stream().anyMatch(e -> e.getName().equals(request3.getName())));
		assertTrue(output.getDataList().stream().anyMatch(e -> e.getPrice() == request3.getPrice()));
		assertTrue(output.getDataList().stream().anyMatch(e -> e.getTimestamp() == request3.getTimestamp()));
	}

	@Test
	@DirtiesContext
	public void getMultiple_oneEntryButQueryOtherName_emptyList() throws Exception {
		String a = "A";
		MarketDataRequest request1 = MarketDataRequest.newBuilder()
				.setName(a)
				.setPrice(1L)
				.setTimestamp(222L)
				.build();


		StreamRecorder<MarketDataResponse> mdResponse = StreamRecorder.create();
		stub.update(request1, mdResponse);
		assertTrue(mdResponse.awaitCompletion(SECONDS_TO_WAIT, UNIT));

		StreamRecorder<FetchMultipleResponse> response = StreamRecorder.create();
		StreamObserver<FetchRequest> requestObserver = stub
				.getMultiple(response);

		requestObserver.onNext(FetchRequest.newBuilder()
				.setName("b")
				.build());

		requestObserver.onCompleted();

		assertTrue(response.awaitCompletion(SECONDS_TO_WAIT, UNIT));
		assertEquals(1, response.getValues().size());
		assertTrue(response.getValues().get(0).getDataList().isEmpty());
	}

}
