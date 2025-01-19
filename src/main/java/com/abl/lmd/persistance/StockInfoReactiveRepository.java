package com.abl.lmd.persistance;

import com.abl.lmd.model.StockInfo;
import com.abl.lmd.model.StockSearchInfo;
import com.abl.lmd.persistance.dao.DataAccessObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class StockInfoReactiveRepository {

    private final DataAccessObject dataAccessObject;

    public Mono<StockInfo> findOne(StockSearchInfo info) {
        return dataAccessObject.findOne(info);
    }

    public Mono<StockInfo> findAndReplace(StockSearchInfo searchInfo, StockInfo replacement) {
        return dataAccessObject.findAndReplace(searchInfo, replacement);
    }
}
