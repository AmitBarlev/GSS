package com.abl.lmd.persistance;

import com.abl.lmd.model.StockHistoryEntry;
import com.abl.lmd.model.StockSearchInfo;
import com.abl.lmd.persistance.dao.DataAccessObject;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class StockHistoryReactiveRepositoryTests {

    @InjectMocks
    private StockHistoryReactiveRepository repository;

    @Mock
    private DataAccessObject accessUnit;

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
    public void findOne_sanity_notNullWithMatchingValues() {
        StockSearchInfo searchInfo = mock(StockSearchInfo.class);
        StockHistoryEntry stockEntry = new StockHistoryEntry("_", 1L, 1L);

        doReturn(Flux.just(stockEntry)).when(accessUnit).find(searchInfo);

        StepVerifier.create(repository.find(searchInfo))
                .assertNext(result -> {
                    assertNotNull(searchInfo);
                    assertEquals(stockEntry, result);
                })
                .verifyComplete();
    }

    @Test
    public void findAndReplace_sanity_notNullWithMatchingValues() {
        StockHistoryEntry stockEntry = new StockHistoryEntry("_", 1L, 1L);

        doReturn(Mono.just(stockEntry)).when(accessUnit).save(stockEntry);

        StepVerifier.create(repository.save(stockEntry))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals(stockEntry, result);
                })
                .verifyComplete();
    }
}
