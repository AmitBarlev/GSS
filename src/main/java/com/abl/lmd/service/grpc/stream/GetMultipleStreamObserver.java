package com.abl.lmd.service.grpc.stream;

import com.abl.live.market.data.stubs.FetchRequest;
import com.abl.live.market.data.stubs.FetchResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetMultipleStreamObserver implements StreamObserver<FetchRequest> {

    private final StreamObserver<FetchResponse> responseObserver;

    @Override
    public void onNext(FetchRequest value) {

    }

    @Override
    public void onError(Throwable t) {

    }

    @Override
    public void onCompleted() {
        responseObserver.onNext(FetchResponse.newBuilder().build());
        responseObserver.onCompleted();
    }
}
