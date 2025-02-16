package com.abl.gss.service.grpc;

import com.abl.live.market.data.stubs.*;
import com.abl.gss.service.StockService;
import com.abl.gss.service.grpc.stream.GetAllBidirectionalStreamObserver;
import com.abl.gss.service.grpc.stream.GetMultipleClientStreamObserver;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
@Slf4j
@RequiredArgsConstructor
public class GSService extends GrpcStockServiceGrpc.GrpcStockServiceImplBase {

    private final StockService stockService;

    @Override
    public void update(MarketDataRequest request, StreamObserver<MarketDataResponse> responseObserver) {
        stockService.update(request)
                .subscribe(responseObserver::onNext, responseObserver::onError, responseObserver::onCompleted);
    }

    @Override
    public void get(FetchRequest request, StreamObserver<FetchResponse> responseObserver) {
        stockService.get(request)
                .subscribe(responseObserver::onNext, responseObserver::onError, responseObserver::onCompleted);
    }

    @Override
    public StreamObserver<FetchRequest> getMultiple(StreamObserver<FetchMultipleResponse> responseObserver) {
        return new GetMultipleClientStreamObserver(responseObserver, stockService);
    }

    @Override
    public StreamObserver<FetchRequest> getAll(StreamObserver<FetchResponse> responseObserver) {
        return new GetAllBidirectionalStreamObserver(responseObserver, stockService);
    }

}
