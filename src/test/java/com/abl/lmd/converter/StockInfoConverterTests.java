package com.abl.lmd.converter;

import com.abl.live.market.data.stubs.MarketDataRequest;
import com.abl.lmd.model.StockInfo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class StockInfoConverterTests {

    @Test
    public void convert_sanity_notNullWithMatchingValues() {
        MarketDataRequest request = mock(MarketDataRequest.class);
        String expectedName = "AAPL";
        long expectedPrice = 150;
        long expectedTimestamp = 1640000000L;
        String expectedDescription = "Apple stock";

        doReturn(expectedName).when(request).getName();
        doReturn(expectedPrice).when(request).getPrice();
        doReturn(expectedTimestamp).when(request).getTimestamp();
        doReturn(expectedDescription).when(request).getDescription();

        StockInfo result = StockInfoConverter.convert(request);

        assertNotNull(result);
        assertEquals(expectedName, result.name());
        assertEquals(expectedPrice, result.price());
        assertEquals(expectedTimestamp, result.timestamp());
    }
}
