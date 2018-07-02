/*
 * This file is part of the OneGuard Micro-Service Architecture Core library.
 *
 * (c) OneGuard <contact@oneguard.email>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package solutions.oneguard.msa.core.messaging;

import reactor.core.publisher.Mono;

import solutions.oneguard.msa.core.model.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class RequestProducer {
    /** Context key, that is used for storing the request ID. */
    public static final String REQUEST_ID_CONTEXT_KEY = "requestId";

    private final MessageProducer producer;
    private final RequestAwareMessageConsumer consumer;

    public RequestProducer(MessageProducer producer, RequestAwareMessageConsumer consumer) {
        this.producer = Objects.requireNonNull(producer);
        this.consumer = Objects.requireNonNull(consumer);
    }

    /**
     * Creates a {@link Mono} that send a request on subscription and completes with response on receiving it.
     *
     * @param responsePayloadClass class to map response payload to
     * @param serviceName name of service, that should handle the request
     * @param message the message
     * @param <T> type of response payload
     * @return the request Mono
     */
    public <T> Mono<Message<T>> request(Class<T> responsePayloadClass, String serviceName, Message<?> message) {
        return request(responsePayloadClass, serviceName, message, message.getType());
    }

    /**
     * Creates a {@link Mono} that send a request on subscription and completes with response on receiving it.
     *
     * @param responsePayloadClass class to map response payload to
     * @param serviceName name of service, that should handle the request
     * @param message the message
     * @param routingKey message routing key
     * @param <T> type of response payload
     * @return the request Mono
     */
    public <T> Mono<Message<T>> request(
        Class<T> responsePayloadClass,
        String serviceName,
        Message<?> message,
        String routingKey
    ) {
        UUID requestId = UUID.randomUUID();

        Map<String, Object> context = message.getContext() == null ? new HashMap<>() : message.getContext();
        context.put(REQUEST_ID_CONTEXT_KEY, requestId.toString());
        message.setContext(context);

        return Mono.create(monoSink -> {
            consumer.registerResponseListener(
                requestId,
                new ResponseListener<T>() {
                    @Override
                    public void onResponseReceived(Message<T> response) {
                        monoSink.success(response);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        monoSink.error(throwable);
                    }
                },
                responsePayloadClass
            );
            monoSink.onCancel(() -> consumer.cancelRequest(requestId));
            producer.sendToService(serviceName, message, routingKey);
        });
    }
}
