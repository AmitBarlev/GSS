package com.abl.gss.converter;

import com.abl.gss.model.StockHistoryEntry;
import com.abl.gss.model.StockInfo;

public class StockHistoryEntryConverter {

    public static StockHistoryEntry convert(StockInfo info) {
        return new StockHistoryEntry(
                info.name(),
                info.price(),
                info.timestamp()
        );
    }
}
