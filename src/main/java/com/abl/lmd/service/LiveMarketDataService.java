package com.abl.lmd.service;

import com.abl.live.market.data.stubs.*;
import com.abl.lmd.service.stream.GetAllStreamObserver;
import com.abl.lmd.service.stream.GetMultipleStreamObserver;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
public class LiveMarketDataService extends LiveMarketDataServiceGrpc.LiveMarketDataServiceImplBase {

    @Override
    public void update(MarketDataRequest request, StreamObserver<MarketDataResponse> responseObserver) {

        MarketDataResponse response = MarketDataResponse.newBuilder()
                        .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void get(FetchRequest request, StreamObserver<FetchResponse> responseObserver) {

        FetchResponse response = FetchResponse.newBuilder()
                .build();

        responseObserver.onNext(response);
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
