package com.abl.lmd.persistance.dao;

import com.abl.lmd.model.StockHistoryEntry;
import com.abl.lmd.model.StockInfo;
import com.abl.lmd.model.StockSearchInfo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DataAccessUnit {

    Mono<StockInfo> findOne(StockSearchInfo info);

    Mono<StockInfo> findAndReplace(StockSearchInfo searchInfo, StockInfo replacement);

    Mono<StockHistoryEntry> save(StockHistoryEntry entry);

    Flux<StockHistoryEntry> find(StockSearchInfo entry);
}
