package com.abl.lmd.service.grpc.stream;

import com.abl.live.market.data.stubs.FetchMultipleResponse;
import com.abl.live.market.data.stubs.FetchRequest;
import com.abl.live.market.data.stubs.FetchResponse;
import com.abl.lmd.service.StockService;
import io.grpc.stub.StreamObserver;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class GetMultipleClientStreamObserver implements StreamObserver<FetchRequest> {

    private final StreamObserver<FetchMultipleResponse> responseObserver;
    private final StockService stockService;
    private final List<Flux<FetchResponse>> publishers = new ArrayList<>();

    @Getter
    private final List<FetchResponse> responses = new ArrayList<>();

    @Override
    public void onNext(FetchRequest value) {
        Flux<FetchResponse> response = stockService.get(value);
        publishers.add(response);
        response.subscribe(responses::add);
    }

    @Override
    public void onError(Throwable t) {
        log.error("Exception of type {} has been thrown", t.getClass(), t);
    }

    @Override
    public void onCompleted() {
        Flux.merge(publishers)
                .collectList()
                .map(list -> FetchMultipleResponse.newBuilder()
                        .addAllData(responses)
                        .build())
                .subscribe(responseObserver::onNext, responseObserver::onError, responseObserver::onCompleted);
    }
}
