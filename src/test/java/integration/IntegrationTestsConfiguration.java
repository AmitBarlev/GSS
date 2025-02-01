package integration;

import com.abl.live.market.data.stubs.LiveMarketDataServiceGrpc;
import com.abl.gss.service.grpc.LiveMarketDataService;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class IntegrationTestsConfiguration {

    @Autowired
    private LiveMarketDataService service;

    private static final String PROCESS_NAME = "GSS";

    @Bean
    public LiveMarketDataServiceGrpc.LiveMarketDataServiceStub stub(ManagedChannel channel) {
        return LiveMarketDataServiceGrpc.newStub(channel);
    }

    @Bean
    public Server inProcessServer() throws IOException {
        return InProcessServerBuilder.forName(PROCESS_NAME)
                .directExecutor()
                .addService(service)
                .build()
                .start();
    }

    @Bean
    public ManagedChannel channel() {
        return InProcessChannelBuilder.forName(PROCESS_NAME)
                .directExecutor()
                .build();
    }

}
