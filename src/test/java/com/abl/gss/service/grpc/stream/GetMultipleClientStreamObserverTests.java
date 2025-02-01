package com.abl.gss.service.grpc.stream;

import com.abl.live.market.data.stubs.FetchMultipleResponse;
import com.abl.live.market.data.stubs.FetchRequest;
import com.abl.live.market.data.stubs.FetchResponse;
import com.abl.gss.service.StockService;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

public class GetMultipleClientStreamObserverTests {

    @InjectMocks
    private GetMultipleClientStreamObserver getMultipleObserver;

    @Mock
    private StockService stockService;

    @Mock
    private StreamObserver<FetchMultipleResponse> responseObserver;

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
    public void onNext_sanity_listContainsRequest() {
        FetchRequest request = FetchRequest.newBuilder().build();
        FetchResponse response = FetchResponse.newBuilder().build();
        List<FetchResponse> list = getMultipleObserver.getResponses();

        assertEquals(0, list.size());

        doReturn(Flux.just(response)).when(stockService).get(request);

        getMultipleObserver.onNext(request);

        assertEquals(1, list.size());
    }

    @Test
    public void onError_sanity_noExceptionThrown() {
        responseObserver.onError(new Throwable());
    }

    @Test
    public void onCompleted_sanity_responseObserverOnNextTriggered() {
        getMultipleObserver.onCompleted();

        verify(responseObserver).onNext(any(FetchMultipleResponse.class));
        verify(responseObserver).onCompleted();
    }
}
