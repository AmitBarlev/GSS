package com.abl.lmd.converter;

import com.abl.live.market.data.stubs.FetchRequest;
import com.abl.live.market.data.stubs.MarketDataRequest;
import com.abl.lmd.model.StockSearchInfo;

public class StockSearchInfoConverter {

    public static StockSearchInfo convert(MarketDataRequest request) {
        return new StockSearchInfo(
                request.getName()
        );
    }

    public static StockSearchInfo convert(FetchRequest request) {
        return new StockSearchInfo(
                request.getName()
        );
    }


}
