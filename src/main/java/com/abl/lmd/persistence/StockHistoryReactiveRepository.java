package com.abl.lmd.persistence;

import com.abl.lmd.model.StockHistoryEntry;
import com.abl.lmd.model.StockSearchInfo;
import com.abl.lmd.persistence.dao.DataAccessObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class StockHistoryReactiveRepository {

    private final DataAccessObject dataAccessObject;

    public Mono<StockHistoryEntry> save(StockHistoryEntry entry) {
        return dataAccessObject.save(entry);
    }

    public Flux<StockHistoryEntry> find(StockSearchInfo searchInfo) {
        return dataAccessObject.find(searchInfo);
    }
}
