package com.abl.lmd.client;

import com.abl.live.market.data.stubs.LiveMarketDataServiceGrpc;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LiveMarketDataClient {

    private final LiveMarketDataServiceGrpc.LiveMarketDataServiceStub stub;


}
