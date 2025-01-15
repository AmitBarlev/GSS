package com.abl.lmd.converter;

import com.abl.live.market.data.stubs.MarketDataResponse;


public class MarketDataResponseConverter {

    public static MarketDataResponse convert(MarketDataResponse.Status status) {
        return MarketDataResponse.newBuilder()
                .setStatus(status)
                .build();
    }
}
