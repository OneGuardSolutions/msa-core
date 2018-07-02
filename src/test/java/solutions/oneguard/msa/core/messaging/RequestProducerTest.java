/*
 * This file is part of the OneGuard Micro-Service Architecture Core library.
 *
 * (c) OneGuard <contact@oneguard.email>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package solutions.oneguard.msa.core.messaging;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import solutions.oneguard.msa.core.model.Message;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static junit.framework.TestCase.assertSame;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

public class RequestProducerTest {
    private MessageProducer messageProducer;
    private RequestAwareMessageConsumer consumer;
    private RequestProducer requestProducer;

    @Before
    public void setUp() {
        messageProducer = Mockito.mock(MessageProducer.class);
        consumer = Mockito.mock(RequestAwareMessageConsumer.class);
        requestProducer = new RequestProducer(messageProducer, consumer);
    }

    @Test
    public void request() {
        RequestProducer producer = Mockito.spy(requestProducer);
        Message message = Message.builder().type("test.message").payload("testPayload").build();

        //noinspection UnassignedFluxMonoInstance
        producer.request(String.class,"test", message);

        //noinspection UnassignedFluxMonoInstance
        verify(producer).request(String.class, "test", message, message.getType());
    }

    @Test
    public void requestWithRoutingKey() {
        Message<String> message = Message.<String>builder().type("test.message").payload("testPayload").build();

        //noinspection unchecked
        ArgumentCaptor<ResponseListener<String>> listenerArgumentCaptor = ArgumentCaptor.forClass(ResponseListener.class);
        doNothing().when(consumer).registerResponseListener(any(), listenerArgumentCaptor.capture(), any());

        Mono<Message<String>> mono = requestProducer.request(String.class, "test", message, "test.routing.key");
        UUID requestId = UUID.fromString(message.getContext().get(RequestProducer.REQUEST_ID_CONTEXT_KEY).toString());
        mono.subscribe().dispose();

        verify(messageProducer).sendToService("test", message, "test.routing.key");
        verify(consumer).registerResponseListener(eq(requestId), same(listenerArgumentCaptor.getValue()), eq(String.class));

        Message<String> response = Message.<String>builder().type("test.response").payload("testPayload").build();
        listenerArgumentCaptor.getValue().onResponseReceived(response);
        Schedulers.single().schedule(
            () -> listenerArgumentCaptor.getValue().onResponseReceived(response),
            10,
            TimeUnit.MILLISECONDS
        );
        Message<String> messageFromMono = mono.block(Duration.of(1, ChronoUnit.SECONDS));

        assertSame(response, messageFromMono);
    }

    @Test
    public void cancel() {
        Message<String> message = Message.<String>builder().type("test.message").payload("testPayload").build();
        Mono<Message<String>> mono = requestProducer.request(String.class, "test", message);
        UUID requestId = UUID.fromString(message.getContext().get(RequestProducer.REQUEST_ID_CONTEXT_KEY).toString());

        mono.subscribe().dispose();
        verify(consumer).cancelRequest(eq(requestId));
    }

    @Test
    public void error() {
        Message<String> message = Message.<String>builder().type("test.message").payload("testPayload").build();

        //noinspection unchecked
        ArgumentCaptor<ResponseListener<String>> listenerArgumentCaptor = ArgumentCaptor.forClass(ResponseListener.class);
        doNothing().when(consumer).registerResponseListener(any(), listenerArgumentCaptor.capture(), any());

        Mono<Message<String>> mono = requestProducer.request(String.class, "test", message, "test.routing.key");

        Schedulers.single().schedule(
            () -> listenerArgumentCaptor.getValue().onError(new CancellationException()),
            10,
            TimeUnit.MILLISECONDS
        );

        AtomicReference<Throwable> reference = new AtomicReference<>();
        try {
            mono.doOnError(reference::set).blockOptional(Duration.of(1, ChronoUnit.SECONDS));
        } catch (Throwable ignored) {}

        assertTrue(reference.get() instanceof CancellationException);
    }
}
