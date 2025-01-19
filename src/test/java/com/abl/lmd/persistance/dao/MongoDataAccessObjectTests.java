package com.abl.lmd.persistance.dao;

import com.abl.lmd.model.StockHistoryEntry;
import com.abl.lmd.model.StockInfo;
import com.abl.lmd.model.StockSearchInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.FindAndReplaceOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

public class MongoDataAccessObjectTests {

    @InjectMocks
    private MongoDataAccessObject dao;

    @Mock
    private ReactiveMongoTemplate reactiveMongoTemplate;

    private AutoCloseable closeable;

    @BeforeEach
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void findOne_notFound_emptyMono() {
        StockSearchInfo info = new StockSearchInfo("_");

        doReturn(Mono.empty())
                .when(reactiveMongoTemplate)
                .findOne(any(Query.class), eq(StockInfo.class), eq(StockInfo.COLLECTION));

        StepVerifier.create(dao.findOne(info))
                .expectNextCount(0)
                .verifyComplete();

        verify(reactiveMongoTemplate).findOne(any(Query.class), eq(StockInfo.class), eq(StockInfo.COLLECTION));
    }

    @Test
    public void findOne_sanity_monoWithMatchingValue() {
        String name = "n";
        StockSearchInfo searchInfo = new StockSearchInfo(name);
        StockInfo expected = new StockInfo(name, 1L, 2L, 3L, "");

        doReturn(Mono.just(expected))
                .when(reactiveMongoTemplate)
                .findOne(any(Query.class), eq(StockInfo.class), eq(StockInfo.COLLECTION));

        StepVerifier.create(dao.findOne(searchInfo))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals(expected, result);
                })
                .verifyComplete();

        verify(reactiveMongoTemplate).findOne(any(Query.class), eq(StockInfo.class), eq(StockInfo.COLLECTION));
    }

    @Test
    public void find_sanity_twoElementsWithMatchingValues() {
        String name = "n";
        StockHistoryEntry entry1 = new StockHistoryEntry(name, 12L, 4L);
        StockHistoryEntry entry2 = new StockHistoryEntry(name, 10L, 1L);
        StockSearchInfo searchInfo = new StockSearchInfo(name);

        doReturn(Flux.fromStream(Stream.of(entry1, entry2))).when(reactiveMongoTemplate).find(any(Query.class), eq(StockHistoryEntry.class), eq(StockHistoryEntry.COLLECTION));

        StepVerifier.create(dao.find(searchInfo))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals(entry1, result);
                })
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals(entry2, result);
                })
                .verifyComplete();

        verify(reactiveMongoTemplate).find(any(Query.class), eq(StockHistoryEntry.class), eq(StockHistoryEntry.COLLECTION));
        verify(reactiveMongoTemplate).find(any(Query.class), eq(StockHistoryEntry.class), eq(StockHistoryEntry.COLLECTION));
    }

    @Test
    public void save_sanity_sameObjectSentAndSavedToList() {
        String name = "A";
        StockHistoryEntry entry = new StockHistoryEntry(name, 12L, 4L);

        doReturn(Mono.just(entry)).when(reactiveMongoTemplate).save(entry);

        StepVerifier.create(dao.save(entry))
                .assertNext(result -> assertSame(result, entry))
                .verifyComplete();
    }


    @Test
    public void findAndReplace_emptyList_savedInList() {
        String name = "A";
        StockSearchInfo searchInfo = new StockSearchInfo(name);
        StockInfo info = new StockInfo(name, 1, 2, 3, "");

        doReturn(Mono.just(info)).when(reactiveMongoTemplate)
                .findAndReplace(any(Query.class), eq(info), any(FindAndReplaceOptions.class), eq(StockInfo.class), eq(StockInfo.COLLECTION));

        StepVerifier.create(dao.findAndReplace(searchInfo, info))
                .assertNext(result -> assertSame(result, info))
                .verifyComplete();
    }


    @Test
    public void findAndReplace_sanity_replacedInList() {
        String name = "A";
        StockSearchInfo searchInfo = new StockSearchInfo(name);
        StockInfo original = new StockInfo(name, 1, 2, 3, "");
        StockInfo replacement = new StockInfo(name, 11, 2, 3, "");

        doReturn(Mono.just(original)).when(reactiveMongoTemplate)
                .findAndReplace(any(Query.class), eq(replacement), any(FindAndReplaceOptions.class), eq(StockInfo.class), eq(StockInfo.COLLECTION));

        StepVerifier.create(dao.findAndReplace(searchInfo, replacement))
                .assertNext(result -> assertEquals(result.price(), original.price()))
                .verifyComplete();

    }
}
