package com.abl.gss.service.grpc;

import com.abl.live.market.data.stubs.*;
import com.abl.gss.service.StockService;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class GSServiceTests {

    @InjectMocks
    private GSService service;

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
    public void update_sanity_streamObserverMethodTriggered() {
        StreamObserver<MarketDataResponse> streamObserver = mock(StreamObserver.class);
        MarketDataRequest request = MarketDataRequest.newBuilder().build();
        MarketDataResponse response = MarketDataResponse.newBuilder().build();

        doReturn(Mono.just(response)).when(stockService).update(request);

        service.update(request, streamObserver);

        verify(stockService).update(request);
        verify(streamObserver).onNext(response);
        verify(streamObserver).onCompleted();
    }

    @Test
    public void update_exceptionThrown_streamObserverMethodTriggered() {
        StreamObserver<MarketDataResponse> streamObserver = mock(StreamObserver.class);
        MarketDataRequest request = MarketDataRequest.newBuilder().build();
        RuntimeException ex = new RuntimeException();

        doReturn(Mono.error(ex)).when(stockService).update(request);

        service.update(request, streamObserver);

        verify(stockService).update(request);
        verify(streamObserver).onError(ex);
    }

    @Test
    public void get_sanity_streamObserverMethodTriggered() {
        StreamObserver<FetchResponse> streamObserver = mock(StreamObserver.class);
        FetchRequest request = FetchRequest.newBuilder().build();
        FetchResponse response = FetchResponse.newBuilder().build();

        doReturn(Flux.just(response)).when(stockService).get(request);

        service.get(request, streamObserver);

        verify(stockService).get(request);
        verify(streamObserver).onNext(response);
        verify(streamObserver).onCompleted();
    }

    @Test
    public void get_exceptionThrown_streamObserverMethodTriggered() {
        StreamObserver<FetchResponse> streamObserver = mock(StreamObserver.class);
        FetchRequest request = FetchRequest.newBuilder().build();
        RuntimeException ex = new RuntimeException();

        doReturn(Flux.error(ex)).when(stockService).get(request);

        service.get(request, streamObserver);

        verify(stockService).get(request);
        verify(streamObserver).onError(ex);
    }

    @Test
    public void getMultiple_sanity_streamObserverMethodTriggered() {
        StreamObserver<FetchMultipleResponse> streamObserver = mock(StreamObserver.class);

        StreamObserver<FetchRequest> output = service.getMultiple(streamObserver);

        assertNotNull(output);
    }

    @Test
    public void getAll_sanity_streamObserverMethodTriggered() {
        StreamObserver<FetchResponse> streamObserver = mock(StreamObserver.class);

        StreamObserver<FetchRequest> output = service.getAll(streamObserver);

        assertNotNull(output);
    }
}
