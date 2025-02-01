package com.abl.gss.persistence;

import com.abl.gss.model.StockHistoryEntry;
import com.abl.gss.model.StockSearchInfo;
import com.abl.gss.persistence.dao.DataAccessObject;
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
