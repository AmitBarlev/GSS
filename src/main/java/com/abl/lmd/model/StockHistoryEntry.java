package com.abl.lmd.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collation = StockHistoryEntry.COLLECTION)
public record StockHistoryEntry(String name,
                                long price,
                                long timestamp) {

    public static final String COLLECTION = "history";
}
