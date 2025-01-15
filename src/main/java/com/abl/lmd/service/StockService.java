package com.abl.lmd.service;

import com.abl.live.market.data.stubs.FetchRequest;
import com.abl.live.market.data.stubs.FetchResponse;
import com.abl.live.market.data.stubs.MarketDataRequest;
import com.abl.live.market.data.stubs.MarketDataResponse;
import com.abl.lmd.converter.*;
import com.abl.lmd.model.StockInfo;
import com.abl.lmd.model.StockSearchInfo;
import com.abl.lmd.persistance.StockHistoryReactiveRepository;
import com.abl.lmd.persistance.StockInfoReactiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockInfoReactiveRepository stockInfoRepository;
    private final StockHistoryReactiveRepository stockHistoryRepository;

    public Mono<MarketDataResponse> update(MarketDataRequest request) {
        StockSearchInfo searchInfo = StockSearchInfoConverter.convert(request);
        StockInfo info = StockInfoConverter.convert(request);
        return stockInfoRepository.findAndReplace(searchInfo, info)
                .map(StockHistoryEntryConverter::convert)
                .doOnNext(stockHistoryRepository::save)
                .map(ignored -> MarketDataResponseConverter.convert(MarketDataResponse.Status.APPROVED))
                .doOnError(ignored -> MarketDataResponseConverter.convert(MarketDataResponse.Status.REJECTED));
    }

    public Flux<FetchResponse> get(FetchRequest request) {
        Mono<FetchResponse> mostUpdatedEntry = stockInfoRepository
                .findOne(StockSearchInfoConverter.convert(request))
                .map(FetchResponseConverter::convert);

        Flux<FetchResponse> history = stockHistoryRepository.find(StockSearchInfoConverter.convert(request))
                .map(FetchResponseConverter::convert);

        return Flux.merge(mostUpdatedEntry, history);
    }
}
