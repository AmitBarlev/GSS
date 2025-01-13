package com.abl.lmd.persistance.dao;

import com.abl.lmd.model.StockHistoryEntry;
import com.abl.lmd.model.StockInfo;
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
    public Mono<StockInfo> findOne(StockInfo info) {
        Criteria criteria = Criteria
                .where(StockInfo.NAME_FIELD).is(info.name());

        Query query = new Query(criteria);
        return template.findOne(query, StockInfo.class, StockInfo.COLLECTION);
    }

    @Override
    public Mono<StockInfo> findAndReplace(StockInfo replacement) {
        Criteria criteria = Criteria
                .where(StockInfo.NAME_FIELD).is(replacement.name())
                .and(StockInfo.TRIGGER_AT_FIELD).lt(replacement.triggeredAt());

        Query query = new Query(criteria);
        FindAndReplaceOptions options = FindAndReplaceOptions.empty().upsert();
        return template.findAndReplace(query, replacement, options,
                StockInfo.class, StockInfo.COLLECTION);
    }

    @Override
    public Mono<StockHistoryEntry> save(StockHistoryEntry entry) {
        return template.save(entry);
    }

    @Override
    public Flux<StockHistoryEntry> find(StockHistoryEntry entry) {
        Criteria criteria = Criteria
                .where(StockHistoryEntry.)

        return template.find();
    }
}
