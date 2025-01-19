package com.abl.lmd.service.grpc.stream;

import com.abl.live.market.data.stubs.FetchMultipleResponse;
import com.abl.live.market.data.stubs.FetchRequest;
import com.abl.live.market.data.stubs.FetchResponse;
import com.abl.lmd.service.StockService;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

public class GetAllBidirectionalStreamObserverTests {

    @InjectMocks
    private GetAllBidirectionalStreamObserver getAllBidirectionalStreamObserver;

    @Mock
    private StreamObserver<FetchResponse> streamObserver;

    @Mock
    private StockService stockService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void onNext_sanity_responseObserverOnNextTriggered() {
        FetchRequest request = FetchRequest.newBuilder().build();
        FetchResponse response = FetchResponse.newBuilder().build();

        doReturn(Flux.just(response)).when(stockService).get(request);

        getAllBidirectionalStreamObserver.onNext(request);

        verify(stockService).get(request);
        verify(streamObserver).onNext(response);
    }

    @Test
    public void onError_sanity_noExceptionThrown() {
        getAllBidirectionalStreamObserver.onError(new Throwable());
    }

    @Test
    public void onCompleted_sanity_responseObserverOnNextTriggered() {
        getAllBidirectionalStreamObserver.onCompleted();

        verify(streamObserver).onCompleted();
    }
}
