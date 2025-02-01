package com.abl.gss.converter;

import com.abl.live.market.data.stubs.MarketDataRequest;
import com.abl.gss.model.StockInfo;

import java.time.Instant;

public class StockInfoConverter {

    public static StockInfo convert(MarketDataRequest request) {
        return new StockInfo(request.getName(),
                request.getPrice(),
                request.getTimestamp(),
                Instant.now().getEpochSecond(),
                request.getDescription());
    }
}
