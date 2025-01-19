package com.abl.lmd.converter;

import com.abl.live.market.data.stubs.FetchResponse;
import com.abl.lmd.model.StockHistoryEntry;
import com.abl.lmd.model.StockInfo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class FetchResponseConverterTests {

    @Test
    public void convert_sanityStockInfo_notNullWithMatchingValues() {
        StockInfo stockInfo = mock(StockInfo.class);
        String expectedName = "_";
        long expectedPrice = 150;
        long expectedTimestamp = 1640000000L;

        doReturn(expectedName).when(stockInfo).name();
        doReturn(expectedPrice).when(stockInfo).price();
        doReturn(expectedTimestamp).when(stockInfo).timestamp();

        FetchResponse result = FetchResponseConverter.convert(stockInfo);

        assertNotNull(result);
        assertEquals(expectedName, result.getName());
        assertEquals(expectedPrice, result.getPrice());
        assertEquals(expectedTimestamp, result.getTimestamp());
    }

    @Test
    public void convert_sanityStockHistoryEntry_notNullWithMatchingValues() {
        StockHistoryEntry stockHistoryEntry = mock(StockHistoryEntry.class);
        String expectedName = "_";
        long expectedPrice = 150;
        long expectedTimestamp = 1640000000L;

        doReturn(expectedName).when(stockHistoryEntry).name();
        doReturn(expectedPrice).when(stockHistoryEntry).price();
        doReturn(expectedTimestamp).when(stockHistoryEntry).timestamp();

        FetchResponse result = FetchResponseConverter.convert(stockHistoryEntry);

        assertNotNull(result);
        assertEquals(expectedName, result.getName());
        assertEquals(expectedPrice, result.getPrice());
        assertEquals(expectedTimestamp, result.getTimestamp());
    }
}
