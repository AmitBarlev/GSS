package com.abl.lmd.service.grpc.stream;

import com.abl.live.market.data.stubs.FetchRequest;
import com.abl.live.market.data.stubs.FetchResponse;
import com.abl.lmd.service.StockService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class GetAllBidirectionalStreamObserver implements StreamObserver<FetchRequest> {

    private final StreamObserver<FetchResponse> responseObserver;
    private final StockService stockService;

    private final List<Flux<FetchResponse>> publishers = new ArrayList<>();

    @Override
    public void onNext(FetchRequest value) {
        Flux<FetchResponse> response = stockService.get(value);
        publishers.add(response);
        response.subscribe(responseObserver::onNext);
    }

    @Override
    public void onError(Throwable t) {
        log.error("Exception of type {} has been thrown", t.getClass(), t);
    }

    @Override
    public void onCompleted() {
        Flux.merge(publishers)
                .collectList()
                .subscribe(ignored -> responseObserver.onCompleted());
        ;
    }
}
