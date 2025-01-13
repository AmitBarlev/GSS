package com.abl.lmd.persistance.dao;


import com.abl.lmd.model.StockHistoryEntry;
import com.abl.lmd.model.StockInfo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class InMemoryDataAccessUnit implements DataAccessUnit {

    private List<StockInfo> stockInfo;
    private List<StockHistoryEntry> historyEntries;

    @PostConstruct
    public void init() {
        stockInfo = new ArrayList<>();
        historyEntries = new ArrayList<>();
    }

    @Override
    public Mono<StockInfo> findOne(StockInfo info) {
        return Mono.justOrEmpty(stockInfo.stream()
                .filter(element -> info.name().equals(element.name()))
                .findFirst());
    }


    @Override
    public Mono<StockInfo> findAndReplace(StockInfo replacement) {
        IntStream.range(0, stockInfo.size())
                .filter(i -> stockInfo.get(i).name().equals(replacement.name()) &&
                        stockInfo.get(i).timestamp() < replacement.timestamp())
                .findFirst()
                .ifPresentOrElse(
                        i -> stockInfo.add(i, replacement),
                        () -> stockInfo.add(replacement)
                );

        return Mono.just(replacement);
    }

    @Override
    public Mono<StockHistoryEntry> save(StockHistoryEntry entry) {
        return Mono.just(historyEntries.add(entry))
                .thenReturn(entry);
    }




}
