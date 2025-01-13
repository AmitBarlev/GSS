package com.abl.lmd.persistance;

import com.abl.lmd.model.StockHistoryEntry;
import com.abl.lmd.persistance.dao.DataAccessUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class StockHistoryReactiveRepository {

    private final DataAccessUnit dataAccessUnit;

    public Mono<StockHistoryEntry> save(StockHistoryEntry entry) {
        return dataAccessUnit.save(entry);
    }

    public Flux<StockHistoryEntry> find(StockHistoryEntry entry) {
        return dataAccessUnit.find(entry);
    }
}
