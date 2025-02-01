package com.abl.gss.persistence;

import com.abl.gss.model.StockInfo;
import com.abl.gss.model.StockSearchInfo;
import com.abl.gss.persistence.dao.DataAccessObject;
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
