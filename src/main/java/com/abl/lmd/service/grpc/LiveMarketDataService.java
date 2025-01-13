package com.abl.lmd.service.grpc;

import com.abl.live.market.data.stubs.*;
import com.abl.lmd.service.StockService;
import com.abl.lmd.service.grpc.stream.GetAllStreamObserver;
import com.abl.lmd.service.grpc.stream.GetMultipleStreamObserver;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class LiveMarketDataService extends LiveMarketDataServiceGrpc.LiveMarketDataServiceImplBase {

    private final StockService stockService;


    @Override
    public void update(MarketDataRequest request, StreamObserver<MarketDataResponse> responseObserver) {
        stockService.update(request)
                .doOnNext(responseObserver::onNext)
                .subscribe(ignored -> responseObserver.onCompleted());
    }

    @Override
    public void get(FetchRequest request, StreamObserver<FetchResponse> responseObserver) {
        stockService.get()
                .subscribe(responseObserver::onNext);
    }

    @Override
    public StreamObserver<FetchRequest> getMultiple(StreamObserver<FetchResponse> responseObserver) {
        return new GetMultipleStreamObserver(responseObserver);
    }

    @Override
    public StreamObserver<FetchRequest> getAll(StreamObserver<FetchResponse> responseObserver) {
        return new GetAllStreamObserver(responseObserver);
    }

}
