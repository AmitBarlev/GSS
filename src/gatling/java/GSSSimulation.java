import com.abl.live.market.data.stubs.*;
import io.gatling.javaapi.core.FeederBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.grpc.GrpcBidirectionalStreamingServiceBuilder;
import io.gatling.javaapi.grpc.GrpcClientStreamingServiceBuilder;
import io.gatling.javaapi.grpc.GrpcProtocolBuilder;
import io.gatling.javaapi.grpc.GrpcServerStreamingServiceBuilder;
import io.grpc.Status;

import java.security.SecureRandom;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.grpc.GrpcDsl.*;

public class GSSSimulation extends Simulation {

    GrpcProtocolBuilder baseGrpcProtocol = grpc.forAddress("localhost", 9090)
            .usePlaintext();
    private static final ScenarioBuilder update = updateScenario();
    private static final ScenarioBuilder get = getScenario();
    private static final ScenarioBuilder getMultiple = getMultipleScenario();
    private static final ScenarioBuilder getAll = getAllScenario();

    private static ScenarioBuilder updateScenario() {
        FeederBuilder.Batchable<String> stockNames = csv("stock_names.csv").circular();
        SecureRandom secureRandom = new SecureRandom();

        return scenario("Update")
                .feed(stockNames)
                .exec(grpc("update")
                        .unary(GrpcStockServiceGrpc.getUpdateMethod())
                        .send(session -> MarketDataRequest.newBuilder()
                                .setName(session.getString("name"))
                                .setPrice(secureRandom.nextInt())
                                .setTimestamp(secureRandom.nextInt())
                                .build())
                        .check(
                                statusCode().is(Status.Code.OK),
                                response(MarketDataResponse::getStatus).is(MarketDataResponse.Status.APPROVED)
                        ));
    }

    private static ScenarioBuilder getScenario() {
        FeederBuilder.Batchable<String> stockNames = csv("stock_names.csv").circular();

        GrpcServerStreamingServiceBuilder<FetchRequest, FetchResponse> getServerStream =
                grpc("Get Server Stream")
                        .serverStream(GrpcStockServiceGrpc.getGetMethod())
                        .check(
                                statusCode().is(Status.Code.OK)
                        );


        return scenario("Get")
                .feed(stockNames)
                .exec(getServerStream.send(session ->
                                FetchRequest.newBuilder()
                                        .setName(session.getString("name"))
                                        .build()),
                        getServerStream.awaitStreamEnd()
                );
    }

    private static ScenarioBuilder getMultipleScenario() {
        FeederBuilder.Batchable<String> stockNames = csv("stock_names.csv").circular();

        GrpcClientStreamingServiceBuilder<FetchRequest, FetchMultipleResponse> getMultipleClientStream =
                grpc("Get Multiple")
                        .clientStream(GrpcStockServiceGrpc.getGetMultipleMethod())
                        .check(
                                statusCode().is(Status.Code.OK)
                        );

        return scenario("GetMultiple")
                .feed(stockNames)
                .exec(
                        getMultipleClientStream.start(),
                        repeat(10).on(getMultipleClientStream.send(session ->
                                FetchRequest.newBuilder()
                                        .setName(session.getString("name"))
                                        .build()
                        )),
                        getMultipleClientStream.halfClose(),
                        getMultipleClientStream.awaitStreamEnd()
                );
    }

    private static ScenarioBuilder getAllScenario() {
        FeederBuilder.Batchable<String> stockNames = csv("stock_names.csv").circular();

        GrpcBidirectionalStreamingServiceBuilder<FetchRequest, FetchResponse> bidirectionalStream =
                grpc("Get All")
                        .bidiStream(GrpcStockServiceGrpc.getGetAllMethod())
                        .check(
                                statusCode().is(Status.Code.OK)
                        );

        return scenario("Get All")
                .feed(stockNames)
                .exec(
                        bidirectionalStream.start(),
                        repeat(1).on(bidirectionalStream.send(session ->
                                FetchRequest.newBuilder()
                                        .setName(session.getString("name"))
                                        .build())),
                        bidirectionalStream.halfClose(),
                        bidirectionalStream.awaitStreamEnd()
                );
    }


    {
        setUp(
                update.injectOpen(atOnceUsers(1)),
                get.injectOpen(atOnceUsers(1)),
                getMultiple.injectOpen(atOnceUsers(1)),
                getAll.injectOpen(atOnceUsers(1))
        ).protocols(baseGrpcProtocol)
                .assertions(
                        global().successfulRequests().percent().gt(98.0),
                        global().responseTime().max().lt(1500)
                );
    }
}
