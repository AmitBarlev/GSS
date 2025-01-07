package com.abl.lmd.configuration;

import com.abl.live.market.data.stubs.LiveMarketDataServiceGrpc;
import io.grpc.ManagedChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.ChannelBuilderOptions;
import org.springframework.grpc.client.GrpcChannelFactory;

import java.nio.channels.Channel;

@Configuration
@RequiredArgsConstructor
public class LiveMarketDataClientConfiguration {

    private final LiveMarketDataClientProperties properties;

    @Bean
    public ManagedChannel channel(GrpcChannelFactory channelFactory) {
        ChannelBuilderOptions options = ChannelBuilderOptions.defaults();

        return channelFactory
                .createChannel(properties.getTarget(), options);
    }

    @Bean
    public LiveMarketDataServiceGrpc.LiveMarketDataServiceStub nonBlockingStub(ManagedChannel channel) {
        return LiveMarketDataServiceGrpc.newStub(channel);
    }
}
