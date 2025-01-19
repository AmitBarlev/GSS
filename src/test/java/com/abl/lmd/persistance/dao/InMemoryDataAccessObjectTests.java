package com.abl.lmd.persistance.dao;


import com.abl.lmd.model.StockHistoryEntry;
import com.abl.lmd.model.StockInfo;
import com.abl.lmd.model.StockSearchInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryDataAccessObjectTests {

    private final InMemoryDataAccessObject dao = new InMemoryDataAccessObject();

    @BeforeEach
    void setUp() {
        dao.init();
    }

    @Test
    public void findOne_emptyList_emptyMono() {
        StockSearchInfo info = new StockSearchInfo("_");

        StepVerifier.create(dao.findOne(info))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    public void findOne_sanity_monoWithMatchingValue() {
        String name = "n";
        StockSearchInfo searchInfo = new StockSearchInfo(name);
        StockInfo expected = new StockInfo(name, 1L, 2L, 3L, "");
        dao.setStockInfo(List.of(expected));

        StepVerifier.create(dao.findOne(searchInfo))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals(expected, result);
                })
                .verifyComplete();
    }

    @Test
    public void find_sanity_twoElementsWithMatchingValues() {
        String name = "n";
        StockHistoryEntry entry1 = new StockHistoryEntry(name, 12L, 4L);
        StockHistoryEntry entry2 = new StockHistoryEntry(name, 10L, 1L);
        dao.setHistoryEntries(List.of(entry1, entry2));
        StockSearchInfo searchInfo = new StockSearchInfo(name);


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
    }

    @Test
    public void save_sanity_sameObjectSentAndSavedToList() {
        String name = "A";
        StockSearchInfo searchInfo = new StockSearchInfo(name);
        StockHistoryEntry entry = new StockHistoryEntry(name, 12L, 4L);

        StepVerifier.create(dao.find(searchInfo))
                        .expectNextCount(0)
                                .verifyComplete();

        StepVerifier.create(dao.save(entry))
                .assertNext(result -> assertSame(result, entry))
                .verifyComplete();

        StepVerifier.create(dao.find(searchInfo))
                .expectNextCount(1)
                .verifyComplete();
    }


    @Test
    public void findAndReplace_emptyList_savedInList() {
        String name = "A";
        StockSearchInfo searchInfo = new StockSearchInfo(name);
        StockInfo info = new StockInfo(name, 1, 2, 3, "");

        StepVerifier.create(dao.findOne(searchInfo))
                .expectNextCount(0)
                .verifyComplete();

        StepVerifier.create(dao.findAndReplace(searchInfo, info))
                .verifyComplete();

        StepVerifier.create(dao.findOne(searchInfo))
                .expectNextCount(1)
                .verifyComplete();
    }


    @Test
    public void findAndReplace_sanity_replacedInList() {
        String name = "A";
        StockSearchInfo searchInfo = new StockSearchInfo(name);
        StockInfo original = new StockInfo(name, 1, 2, 3, "");
        StockInfo replacement = new StockInfo(name, 11, 2, 3, "");
        dao.setStockInfo(new ArrayList<>(Collections.singleton(original)));

        StepVerifier.create(dao.findAndReplace(searchInfo, replacement))
                .assertNext(result -> assertEquals(result.price(), original.price()))
                .verifyComplete();

        StepVerifier.create(dao.findOne(searchInfo))
                .assertNext(result -> assertEquals(replacement.price(), result.price()))
                .verifyComplete();
    }
}
