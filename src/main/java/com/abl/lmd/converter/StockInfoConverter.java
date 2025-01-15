package com.abl.lmd.converter;

import com.abl.live.market.data.stubs.FetchRequest;
import com.abl.live.market.data.stubs.MarketDataRequest;
import com.abl.lmd.model.StockInfo;

import java.time.Instant;

public class StockInfoConverter {

    public static StockInfo convert(MarketDataRequest request) {
        return new StockInfo(request.getName(),
                request.getPrice(),
                request.getTimestamp(),
                Instant.now().getEpochSecond(),
                request.getDescription(),
                request.getSourcesList().stream().map(Enum::name).toList());
    }
}
