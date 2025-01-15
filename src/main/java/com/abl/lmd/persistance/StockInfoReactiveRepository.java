package com.abl.lmd.persistance;

import com.abl.lmd.model.StockInfo;
import com.abl.lmd.model.StockSearchInfo;
import com.abl.lmd.persistance.dao.DataAccessUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class StockInfoReactiveRepository {

    private final DataAccessUnit dataAccessUnit;

    public Mono<StockInfo> findOne(StockSearchInfo info) {
        return dataAccessUnit.findOne(info);
    }

    public Mono<StockInfo> findAndReplace(StockSearchInfo searchInfo, StockInfo replacement) {
        return dataAccessUnit.findAndReplace(searchInfo, replacement);
    }
}
