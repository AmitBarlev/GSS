package com.abl.lmd.persistance.dao;

import com.abl.lmd.model.StockHistoryEntry;
import com.abl.lmd.model.StockInfo;
import com.abl.lmd.model.StockSearchInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.FindAndReplaceOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Profile("local")
@RequiredArgsConstructor
public class MongoDataAccessUnit implements DataAccessUnit {

    private final ReactiveMongoTemplate template;

    @Override
    public Mono<StockInfo> findOne(StockSearchInfo info) {
        Criteria criteria = Criteria
                .where(StockSearchInfo.NAME_FIELD).is(info.name());

        return template.findOne(new Query(criteria), StockInfo.class, StockInfo.COLLECTION);
    }

    @Override
    public Mono<StockInfo> findAndReplace(StockSearchInfo searchInfo,
                                          StockInfo replacement) {
        Criteria criteria = Criteria
                .where(StockSearchInfo.NAME_FIELD).is(searchInfo.name());

        FindAndReplaceOptions options = FindAndReplaceOptions.empty().upsert();
        return template.findAndReplace(new Query(criteria), replacement, options,
                StockInfo.class, StockInfo.COLLECTION);
    }

    @Override
    public Mono<StockHistoryEntry> save(StockHistoryEntry entry) {
        return template.save(entry);
    }

    @Override
    public Flux<StockHistoryEntry> find(StockSearchInfo entry) {
        Criteria criteria = Criteria
                .where(StockSearchInfo.NAME_FIELD).is(entry.name());

        return template.find(new Query(criteria), StockHistoryEntry.class, StockHistoryEntry.COLLECTION);
    }

}
