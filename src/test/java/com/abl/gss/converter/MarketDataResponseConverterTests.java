package com.abl.gss.converter;

import com.abl.live.market.data.stubs.MarketDataResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MarketDataResponseConverterTests {

    @Test
    public void convert_sanity_notNullWithMatchingValues() {
        MarketDataResponse.Status status = MarketDataResponse.Status.APPROVED;

        MarketDataResponse result = MarketDataResponseConverter.convert(status);

        assertNotNull(result);
        assertEquals(status, result.getStatus());
    }
}
