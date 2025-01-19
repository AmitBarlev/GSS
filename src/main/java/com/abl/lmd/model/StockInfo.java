package com.abl.lmd.model;

import lombok.Generated;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Generated

@Document(collation = StockInfo.COLLECTION)
public record StockInfo(@Id String name,
                        long price,
                        long timestamp,
                        long triggeredAt,
                        String description) {
    public static final String COLLECTION = "stock";
}

