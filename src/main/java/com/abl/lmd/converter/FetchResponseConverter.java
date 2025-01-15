package com.abl.lmd.converter;

import com.abl.live.market.data.stubs.FetchResponse;
import com.abl.lmd.model.StockHistoryEntry;
import com.abl.lmd.model.StockInfo;

public class FetchResponseConverter {

    public static FetchResponse convert(StockInfo info) {
        return FetchResponse.newBuilder()
                .setName(info.name())
                .setPrice(info.price())
                .setTimestamp(info.timestamp())
                .build();
    }

    public static FetchResponse convert(StockHistoryEntry entry) {
        return FetchResponse.newBuilder()
                .setName(entry.name())
                .setPrice(entry.price())
                .setTimestamp(entry.timestamp())
                .build();
    }
}
