package com.abl.gss.persistence.dao;

import com.abl.gss.model.StockHistoryEntry;
import com.abl.gss.model.StockInfo;
import com.abl.gss.model.StockSearchInfo;
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
public class MongoDataAccessObject implements DataAccessObject {

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

        FindAndReplaceOptions options = FindAndReplaceOptions
                .options()
                .upsert();
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
