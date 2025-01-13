package com.abl.lmd.persistance.dao;

import com.abl.lmd.model.StockHistoryEntry;
import com.abl.lmd.model.StockInfo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DataAccessUnit {

    Mono<StockInfo> findOne(StockInfo info);

    Mono<StockInfo> findAndReplace(StockInfo replacement);

    Mono<StockHistoryEntry> save(StockHistoryEntry entry);

    Flux<StockHistoryEntry> find(StockHistoryEntry entry);
}
