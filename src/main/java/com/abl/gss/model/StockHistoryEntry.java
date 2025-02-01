package com.abl.gss.model;

import lombok.Generated;
import org.springframework.data.mongodb.core.mapping.Document;

@Generated
@Document(collection = StockHistoryEntry.COLLECTION)
public record StockHistoryEntry(String name,
                                long price,
                                long timestamp) {

    public static final String COLLECTION = "history";
}
