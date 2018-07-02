/*
 * This file is part of the OneGuard Micro-Service Architecture Core library.
 *
 * (c) OneGuard <contact@oneguard.email>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package solutions.oneguard.msa.core.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import solutions.oneguard.msa.core.model.Message;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;

public class RequestAwareMessageConsumer extends SimpleMessageConsumer {
    private static final Logger log = LoggerFactory.getLogger(RequestAwareMessageConsumer.class);

    private final Map<UUID, Tuple2<ResponseListener<?>, Class<?>>> waitingRequests = new ConcurrentHashMap<>();

    public RequestAwareMessageConsumer(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    private String extractRequestIdAsString(Message message) {
        return (message.getResponseTo() != null && message.getContext() != null) ?
            message.getContext().get(RequestProducer.REQUEST_ID_CONTEXT_KEY).toString() :
            null;
    }

    private void handleResponse(Message message, String requestIdAsString) {
        UUID requestId = UUID.fromString(requestIdAsString);
        Tuple2<ResponseListener<?>, Class<?>> responseListenerPayloadClassPair = waitingRequests.remove(requestId);
        if (responseListenerPayloadClassPair == null) {
            log.info("Received response to canceled request '{}': <{}>", requestId, message);
            return;
        }

        ResponseListener<?> listener = responseListenerPayloadClassPair.getT1();
        try {
            Message response = convertMessage(message, responseListenerPayloadClassPair.getT2());
            //noinspection unchecked
            listener.onResponseReceived(response);
        } catch (Throwable t) {
            listener.onError(t);
        }
    }

    public void handleMessage(Message<?> message) {
        String requestIdAsString = extractRequestIdAsString(message);
        if (requestIdAsString != null) {
            handleResponse(message, requestIdAsString);
            return;
        }

        super.handleMessage(message);
    }

    /**
     * Registers new response listener associated with specified request ID.
     *
     * @param requestId the request ID
     * @param listener response lister
     * @param responsePayloadClass the class to map response payload to
     * @param <T> response payload class
     */
    public <T> void registerResponseListener(
        UUID requestId,
        ResponseListener<? extends T> listener,
        Class<T> responsePayloadClass
    ) {
        Objects.requireNonNull(requestId);
        Objects.requireNonNull(listener);
        Objects.requireNonNull(responsePayloadClass);

        waitingRequests.put(requestId, Tuples.of(listener, responsePayloadClass));
    }

    /**
     * Cancels the request with specified ID.
     *
     * @param requestId the request ID
     */
    public void cancelRequest(UUID requestId) {
        Tuple2<ResponseListener<?>, Class<?>> futureClassPair = waitingRequests.remove(requestId);
        if (futureClassPair != null) {
            futureClassPair.getT1().onError(new CancellationException());
        }
    }
}
