package com.abl.lmd.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collation = StockInfo.COLLECTION)
public record StockInfo(@Id String name,
                        long price,
                        long timestamp,
                        long triggeredAt,
                        String description,
                        List<String> sources) {
    public static final String COLLECTION = "stock";
}

