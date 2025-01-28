package com.abl.lmd.service;

import com.abl.live.market.data.stubs.FetchRequest;
import com.abl.live.market.data.stubs.FetchResponse;
import com.abl.live.market.data.stubs.MarketDataRequest;
import com.abl.live.market.data.stubs.MarketDataResponse;
import com.abl.lmd.model.StockHistoryEntry;
import com.abl.lmd.model.StockInfo;
import com.abl.lmd.model.StockSearchInfo;
import com.abl.lmd.persistence.StockHistoryReactiveRepository;
import com.abl.lmd.persistence.StockInfoReactiveRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class StockServiceTests {

    @InjectMocks
    private StockService stockService;

    @Mock
    private StockInfoReactiveRepository stockInfoRepository;

    @Mock
    private StockHistoryReactiveRepository stockHistoryRepository;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void get_sanity_stockInfoEntries() {
        String name = "A";
        StockInfo stockInfo = new StockInfo(name, 1, 2, 3, "");
        StockHistoryEntry entry1 = new StockHistoryEntry(name, 1, 2);
        StockHistoryEntry entry2 = new StockHistoryEntry(name, 3, 4);
        FetchRequest request = FetchRequest.newBuilder()
                .setName(name)
                .build();

        FetchResponse response1 = FetchResponse.newBuilder()
                .setName(stockInfo.name())
                .setPrice(stockInfo.price())
                .setTimestamp(stockInfo.timestamp())
                .build();

        FetchResponse response2 = FetchResponse.newBuilder()
                .setName(entry1.name())
                .setPrice(entry1.price())
                .setTimestamp(entry1.timestamp())
                .build();

        FetchResponse response3 = FetchResponse.newBuilder()
                .setName(entry2.name())
                .setPrice(entry2.price())
                .setTimestamp(entry2.timestamp())
                .build();

        doReturn(Mono.just(stockInfo)).when(stockInfoRepository).findOne(any(StockSearchInfo.class));
        doReturn(Flux.just(entry1, entry2)).when(stockHistoryRepository).find(any(StockSearchInfo.class));

        StepVerifier.create(stockService.get(request))
                .assertNext(result -> assertEquals(response1, result))
                .assertNext(result -> assertEquals(response2, result))
                .assertNext(result -> assertEquals(response3, result))
                .verifyComplete();

        verify(stockInfoRepository).findOne(any(StockSearchInfo.class));
        verify(stockHistoryRepository).find(any(StockSearchInfo.class));
    }

    @Test
    public void update_sanity_approvedStatus() {
        String name = "A";
        MarketDataRequest request = MarketDataRequest.newBuilder()
                .setName(name)
                .build();

        StockInfo last = new StockInfo(name, 1, 2, 3, "");
        doReturn(Mono.just(last)).when(stockInfoRepository).findAndReplace(any(StockSearchInfo.class), any(StockInfo.class));
        doReturn(Mono.just(mock(StockHistoryEntry.class))).when(stockHistoryRepository).save(any(StockHistoryEntry.class));

        StepVerifier.create(stockService.update(request))
                .assertNext(response -> assertEquals(MarketDataResponse.Status.APPROVED, response.getStatus()))
                .verifyComplete();
    }

    @Test
    public void update_exceptionThrown_rejectedStatus() {
        String name = "A";
        MarketDataRequest request = MarketDataRequest.newBuilder()
                .setName(name)
                .build();

        StockInfo last = new StockInfo(name, 1, 2, 3, "");
        doReturn(Mono.just(last)).when(stockInfoRepository).findAndReplace(any(StockSearchInfo.class), any(StockInfo.class));
        doThrow(new RuntimeException()).when(stockHistoryRepository).save(any(StockHistoryEntry.class));


        StepVerifier.create(stockService.update(request))
                .expectNextMatches(response -> MarketDataResponse.Status.REJECTED.equals(response.getStatus()))
                .verifyComplete();
    }
}
