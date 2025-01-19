package com.abl.lmd.service.grpc.stream;

import com.abl.live.market.data.stubs.FetchMultipleResponse;
import com.abl.live.market.data.stubs.FetchRequest;
import com.abl.live.market.data.stubs.FetchResponse;
import com.abl.lmd.service.StockService;
import io.grpc.stub.StreamObserver;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class GetMultipleClientStreamObserver implements StreamObserver<FetchRequest> {

    private final StreamObserver<FetchMultipleResponse> responseObserver;
    private final StockService stockService;

    @Getter
    private final List<FetchResponse> responses = new ArrayList<>();

    @Override
    public void onNext(FetchRequest value) {
        stockService.get(value)
                .subscribe(responses::add);
    }

    @Override
    public void onError(Throwable t) {
        log.error("Exception of type {} has been thrown", t.getClass(), t);
    }

    @Override
    public void onCompleted() {
        responseObserver.onNext(FetchMultipleResponse.newBuilder()
                .addAllData(responses)
                .build());

        responseObserver.onCompleted();
        responses.clear();
    }
}
