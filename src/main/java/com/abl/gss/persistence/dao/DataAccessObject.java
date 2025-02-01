package com.abl.gss.persistence.dao;

import com.abl.gss.model.StockHistoryEntry;
import com.abl.gss.model.StockInfo;
import com.abl.gss.model.StockSearchInfo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DataAccessObject {

    Mono<StockInfo> findOne(StockSearchInfo info);

    Mono<StockInfo> findAndReplace(StockSearchInfo searchInfo, StockInfo replacement);

    Mono<StockHistoryEntry> save(StockHistoryEntry entry);

    Flux<StockHistoryEntry> find(StockSearchInfo entry);
}
