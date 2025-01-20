package integration;

import com.abl.live.market.data.stubs.LiveMarketDataServiceGrpc;
import io.grpc.ManagedChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.ChannelBuilderOptions;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.testcontainers.mongodb.MongoDBAtlasLocalContainer;

@Configuration
public class IntegrationTestsConfiguration {

    @Autowired
    private GrpcChannelFactory factory;

    @Bean
    public ManagedChannel channel() {
        ChannelBuilderOptions options = ChannelBuilderOptions.defaults();
        return factory.createChannel("localhost:6565", options);
    }

    @Bean
    public LiveMarketDataServiceGrpc.LiveMarketDataServiceStub stub(ManagedChannel channel) {
        return LiveMarketDataServiceGrpc.newStub(channel);
    }

}
