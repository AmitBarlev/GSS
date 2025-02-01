package com.abl.gss.converter;

import com.abl.gss.model.StockHistoryEntry;
import com.abl.gss.model.StockInfo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class StockHistoryEntryConverterTests {

    @Test
    public void convert_sanity_notNullWithMatchingValues() {
        StockInfo stockInfo = mock(StockInfo.class);
        String expectedName = "AAPL";
        long expectedPrice = 150;
        long expectedTimestamp = 1640000000L;

        doReturn(expectedName).when(stockInfo).name();
        doReturn(expectedPrice).when(stockInfo).price();
        doReturn(expectedTimestamp).when(stockInfo).timestamp();

        StockHistoryEntry result = StockHistoryEntryConverter.convert(stockInfo);

        assertNotNull(result);
        assertEquals(expectedName, result.name());
        assertEquals(expectedPrice, result.price());
        assertEquals(expectedTimestamp, result.timestamp());
    }
}
