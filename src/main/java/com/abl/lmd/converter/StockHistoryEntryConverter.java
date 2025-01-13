package com.abl.lmd.converter;

import com.abl.live.market.data.stubs.FetchRequest;
import com.abl.lmd.model.StockHistoryEntry;
import com.abl.lmd.model.StockInfo;

public class StockHistoryEntryConverter {

    public static StockHistoryEntry convert(StockInfo info) {
        return new StockHistoryEntry(
                info.name(),
                info.price(),
                info.timestamp()
        );
    }

    public static StockHistoryEntry convert(FetchRequest request) {
        return new StockHistoryEntry(
                request.getName()
        );
    }
}
