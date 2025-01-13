package com.abl.lmd.persistance;

import com.abl.lmd.model.StockInfo;
import com.abl.lmd.persistance.dao.DataAccessUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class StockInfoReactiveRepository {

    private final DataAccessUnit dataAccessUnit;

    public Mono<StockInfo> findOne(StockInfo info) {
        return dataAccessUnit.findOne(info);
    }

    public Mono<StockInfo> findAndReplace(StockInfo replacement) {
        return dataAccessUnit.findAndReplace(replacement);
    }
}
