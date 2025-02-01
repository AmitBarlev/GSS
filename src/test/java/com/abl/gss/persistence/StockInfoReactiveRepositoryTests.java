package com.abl.gss.persistence;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.abl.gss.model.StockInfo;
import com.abl.gss.model.StockSearchInfo;
import com.abl.gss.persistence.dao.DataAccessObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class StockInfoReactiveRepositoryTests {

    @InjectMocks
    private StockInfoReactiveRepository repository;

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
        StockInfo expectedStockInfo = new StockInfo("_", 150, 164L
        , 1L, "");

        doReturn(Mono.just(expectedStockInfo)).when(accessUnit).findOne(searchInfo);

        StepVerifier.create(repository.findOne(searchInfo))
                .assertNext(result -> {
                    assertNotNull(searchInfo);
                    assertEquals(expectedStockInfo, result);
                })
                .verifyComplete();
    }

    @Test
    public void findAndReplace_sanity_notNullWithMatchingValues() {
        StockSearchInfo searchInfo = mock(StockSearchInfo.class);
        StockInfo replacementStockInfo = new StockInfo("_", 150, 164L
                , 1L, "");

        doReturn(Mono.just(replacementStockInfo)).when(accessUnit).findAndReplace(searchInfo, replacementStockInfo);

        StepVerifier.create(repository.findAndReplace(searchInfo, replacementStockInfo))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals(replacementStockInfo, result);
                })
                .verifyComplete();
    }
}