package com.abl.lmd.persistence.dao;


import com.abl.lmd.model.StockHistoryEntry;
import com.abl.lmd.model.StockInfo;
import com.abl.lmd.model.StockSearchInfo;
import jakarta.annotation.PostConstruct;
import lombok.Setter;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Profile("dev")
public class InMemoryDataAccessObject implements DataAccessObject {

    @Setter
    private List<StockInfo> stockInfo;

    @Setter
    private List<StockHistoryEntry> historyEntries;

    @PostConstruct
    public void init() {
        stockInfo = new ArrayList<>();
        historyEntries = new ArrayList<>();
    }

    @Override
    public Mono<StockInfo> findOne(StockSearchInfo info) {
        return Mono.justOrEmpty(stockInfo.stream()
                .filter(element -> info.name().equals(element.name()))
                .findFirst());
    }

    @Override
    public Mono<StockInfo> findAndReplace(StockSearchInfo searchInfo, StockInfo replacement) {
        //Not very efficient, but since it's mostly for debugging, it's fine.

        StockInfo output = stockInfo.stream()
                .filter(info -> info.name().equals(replacement.name()))
                .findFirst()
                .map(info -> stockInfo.set(stockInfo.indexOf(info), replacement))
                .orElseGet(() -> {
                    stockInfo.add(replacement);
                    return null;
                });

        return Optional.ofNullable(output)
                .map(Mono::just)
                .orElseGet(Mono::empty);
    }

    @Override
    public Mono<StockHistoryEntry> save(StockHistoryEntry entry) {
        return Mono.just(historyEntries.add(entry))
                .thenReturn(entry);
    }

    @Override
    public Flux<StockHistoryEntry> find(StockSearchInfo entry) {
        return Flux.fromStream(historyEntries.stream())
                .filter(element -> entry.name().equals(element.name()));
    }

}
