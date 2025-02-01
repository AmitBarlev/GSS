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
		FetchRequest request = newFetchRequest("name");

		stub.get(request, response);

		assertTrue(response.awaitCompletion(SECONDS_TO_WAIT, UNIT));
		assertEquals(0, response.getValues().size());
	}

	@Test
	@DirtiesContext
	public void update_saveEntityAndQueryIt_entityMatchesExpectedValues() throws Exception {
		String name = "A";
		StreamRecorder<MarketDataResponse> marketDataResponse = updateStock(name, 1L, 222L);
		assertEquals(1, marketDataResponse.getValues().size());

		StreamRecorder<FetchResponse> fetchResponse = StreamRecorder.create();
		FetchRequest fetchRequest = FetchRequest.newBuilder()
				.setName(name)
				.build();

		stub.get(fetchRequest, fetchResponse);

		assertTrue(fetchResponse.awaitCompletion(SECONDS_TO_WAIT, UNIT));

		assertEquals(1, fetchResponse.getValues().size());
		FetchResponse output = fetchResponse.getValues().get(0);
		assertEquals(name, output.getName());
		assertEquals(1L, output.getPrice());
		assertEquals(222L, output.getTimestamp());
	}

	@Test
	@DirtiesContext
	public void update_saveTwoEntitiesUnderTheSameName_getTwoEntities() throws Exception {
		String name = "A";
		updateStock(name, 1L, 222L);
		StreamRecorder<MarketDataResponse> mdResponse = updateStock(name, 3L, 999L);

		assertEquals(1, mdResponse.getValues().size());
		MarketDataResponse marketData = mdResponse.getValues().get(0);
		assertEquals(MarketDataResponse.Status.APPROVED, marketData.getStatus());

		StreamRecorder<FetchResponse> fetchResponse = StreamRecorder.create();
		FetchRequest fetchRequest = newFetchRequest(name);

		stub.get(fetchRequest, fetchResponse);

		assertTrue(fetchResponse.awaitCompletion(SECONDS_TO_WAIT, UNIT));
		assertEquals(2, fetchResponse.getValues().size());

		FetchResponse output = fetchResponse.getValues().get(0);
		assertEquals(name, output.getName());
		assertEquals(3L, output.getPrice());
		assertEquals(999L, output.getTimestamp());

		output = fetchResponse.getValues().get(1);
		assertEquals(name, output.getName());
		assertEquals(1L, output.getPrice());
		assertEquals(222L, output.getTimestamp());
	}

	@Test
	@DirtiesContext
	public void update_saveTwoEntitiesUnderTheDifferentName_getOneEntities() throws Exception {
		String a = "A";
		String b = "B";
		updateStock(a, 1L, 222L);
		StreamRecorder<MarketDataResponse> mdResponse = updateStock(b, 3L, 999L);
		assertEquals(1, mdResponse.getValues().size());

		StreamRecorder<FetchResponse> fetchResponse = StreamRecorder.create();
		FetchRequest fetchRequest = newFetchRequest(a);

		stub.get(fetchRequest, fetchResponse);

		assertTrue(fetchResponse.awaitCompletion(SECONDS_TO_WAIT, UNIT));
		assertEquals(1, fetchResponse.getValues().size());

		FetchResponse output = fetchResponse.getValues().get(0);
		assertEquals(a, output.getName());
		assertEquals(1L, output.getPrice());
		assertEquals(222L, output.getTimestamp());

		fetchResponse = StreamRecorder.create();
		fetchRequest = newFetchRequest(b);

		stub.get(fetchRequest, fetchResponse);

		assertTrue(fetchResponse.awaitCompletion(SECONDS_TO_WAIT, UNIT));
		assertEquals(1, fetchResponse.getValues().size());

		output = fetchResponse.getValues().get(0);
		assertEquals(b, output.getName());
		assertEquals(3L, output.getPrice());
		assertEquals(999L, output.getTimestamp());
	}

	@Test
	@DirtiesContext
	public void getMultiple_empty_emptyList() throws Exception {

		StreamRecorder<FetchMultipleResponse> response = StreamRecorder.create();
		StreamObserver<FetchRequest> requestObserver = stub
				.getMultiple(response);

		requestObserver.onNext(newFetchRequest("A"));

		requestObserver.onNext(newFetchRequest("B"));

		requestObserver.onCompleted();
		assertTrue(response.awaitCompletion(SECONDS_TO_WAIT, UNIT));
		assertFalse(response.getValues().isEmpty());
		assertTrue(response.getValues().get(0).getDataList().isEmpty());
	}

	@Test
	@DirtiesContext
	public void getMultiple_twoUpdatesOfSameNamePlusAnotherOfOtherName_listOf3MatchingValues() throws Exception {
		//Test might be unstable
		String a = "A";
		String b = "B";
		updateStock(a, 1L, 222L);
		updateStock(b, 3L, 999L);
		updateStock(a, 47L, 1234567L);

		StreamRecorder<FetchMultipleResponse> response = StreamRecorder.create();
		StreamObserver<FetchRequest> requestObserver = stub
				.getMultiple(response);

		requestObserver.onNext(newFetchRequest(a));
		requestObserver.onNext(newFetchRequest(b));

		requestObserver.onCompleted();

		assertEquals(1, response.getValues().size());

		assertTrue(response.awaitCompletion(SECONDS_TO_WAIT, UNIT));
		FetchMultipleResponse output = response.getValues().get(0);
		assertEquals(3, output.getDataList().size());

		assertTrue(output.getDataList().stream().anyMatch(e -> e.getName().equals(a)));
		assertTrue(output.getDataList().stream().anyMatch(e -> e.getPrice() == 1L));
		assertTrue(output.getDataList().stream().anyMatch(e -> e.getTimestamp() == 222L));

		assertTrue(output.getDataList().stream().anyMatch(e -> e.getName().equals(b)));
		assertTrue(output.getDataList().stream().anyMatch(e -> e.getPrice() == 3L));
		assertTrue(output.getDataList().stream().anyMatch(e -> e.getTimestamp() == 999L));

		assertTrue(output.getDataList().stream().anyMatch(e -> e.getName().equals(a)));
		assertTrue(output.getDataList().stream().anyMatch(e -> e.getPrice() == 47L));
		assertTrue(output.getDataList().stream().anyMatch(e -> e.getTimestamp() == 1234567L));
	}

	@Test
	@DirtiesContext
	public void getMultiple_oneEntryButQueryOtherName_emptyList() throws Exception {
		String a = "A";
		updateStock(a, 1L, 222L);

		StreamRecorder<FetchMultipleResponse> response = StreamRecorder.create();
		StreamObserver<FetchRequest> requestObserver = stub
				.getMultiple(response);

		requestObserver.onNext(newFetchRequest("b"));
		requestObserver.onCompleted();

		assertTrue(response.awaitCompletion(SECONDS_TO_WAIT, UNIT));
		assertEquals(1, response.getValues().size());
		assertTrue(response.getValues().get(0).getDataList().isEmpty());
	}

	@Test
	@DirtiesContext
	public void getAll_oneEntryButQueryOtherName_emptyList() throws Exception {
		String a = "A";
		updateStock(a, 1L, 222L);

		StreamRecorder<FetchResponse> response = StreamRecorder.create();
		StreamObserver<FetchRequest> requestObserver = stub.getAll(response);

		requestObserver.onNext(newFetchRequest("b"));
		requestObserver.onCompleted();

		assertTrue(response.awaitCompletion(SECONDS_TO_WAIT, UNIT));
		assertTrue(response.getValues().isEmpty());
	}

	@Test
	@DirtiesContext
	public void getAll_sanity_3DifferentStocks() throws Exception {
		String a = "A";
		String b = "B";
		String c = "C";

		updateStock(a, 1L, 111L);
		updateStock(a, 2L, 222L);
		updateStock(a, 3L, 333L);
		updateStock(b, 4L, 444L);
		updateStock(c, 5L, 555L);
		updateStock(c, 6L, 666L);

		StreamRecorder<FetchResponse> response = StreamRecorder.create();
		StreamObserver<FetchRequest> requestObserver = stub.getAll(response);

		requestObserver.onNext(newFetchRequest(b));
		requestObserver.onNext(newFetchRequest(a));
		requestObserver.onNext(newFetchRequest(c));

		response.awaitCompletion(SECONDS_TO_WAIT, UNIT);
		assertEquals(6, response.getValues().size());

		requestObserver.onCompleted();
	}

	private StreamRecorder<MarketDataResponse> updateStock(String name, long price, long timestamp) throws Exception {
		StreamRecorder<MarketDataResponse> mdResponse = StreamRecorder.create();
		stub.update(newMarketDataRequest(name, price, timestamp), mdResponse);
		assertTrue(mdResponse.awaitCompletion(SECONDS_TO_WAIT, UNIT));
		return mdResponse;
	}

	private MarketDataRequest newMarketDataRequest(String name, long price, long timestamp) {
		return MarketDataRequest.newBuilder()
				.setName(name)
				.setPrice(price)
				.setTimestamp(timestamp)
				.build();
	}

	private FetchRequest newFetchRequest(String name) {
		return FetchRequest.newBuilder()
				.setName(name)
				.build();
	}
}
