package com.abl.gss.converter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.abl.live.market.data.stubs.FetchRequest;
import com.abl.live.market.data.stubs.MarketDataRequest;
import com.abl.gss.model.StockSearchInfo;
import org.junit.jupiter.api.Test;

class StockSearchInfoConverterTests {

    @Test
    public void convert_sanity_notNullWithMatchingValues_MarketDataRequest() {
        MarketDataRequest request = mock(MarketDataRequest.class);
        String expectedName = "_";

        doReturn(expectedName).when(request).getName();

        StockSearchInfo result = StockSearchInfoConverter.convert(request);

        assertNotNull(result);
        assertEquals(expectedName, result.name());
    }

    @Test
    public void convert_sanity_notNullWithMatchingValues_FetchRequest() {
        FetchRequest request = mock(FetchRequest.class);
        String expectedName = "_";

        doReturn(expectedName).when(request).getName();

        StockSearchInfo result = StockSearchInfoConverter.convert(request);

        assertNotNull(result);
        assertEquals(expectedName, result.name());
    }
}

