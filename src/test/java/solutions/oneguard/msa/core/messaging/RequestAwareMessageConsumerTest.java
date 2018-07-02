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
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.Before;
import org.junit.Test;

import solutions.oneguard.msa.core.TestHandler;
import solutions.oneguard.msa.core.model.Message;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.CancellationException;

import static junit.framework.TestCase.*;

public class RequestAwareMessageConsumerTest {
    private RequestAwareMessageConsumer consumer;

    @Before
    public void setUp() {
        consumer = new RequestAwareMessageConsumer(new ObjectMapper());
    }

    @Test
    public void handleMessageNotResponse() {
        Message<?> message = Message.builder()
            .type("test.message")
            .payload(TextNode.valueOf("testPayload"))
            .build();
        TestHandler<String> testHandler = new TestHandler<>(String.class);
        consumer.addHandler("test.message", testHandler);
        consumer.handleMessage(message);

        assertNotNull(testHandler.getMessage());
        assertEquals("testPayload", testHandler.getMessage().getPayload());
    }

    @Test
    public void handleMessageResponse() {
        UUID requestId = UUID.randomUUID();
        TestListener<String> listener = new TestListener<>();
        consumer.registerResponseListener(requestId, listener, String.class);

        Message<?> message = Message.builder()
            .type("test.response")
            .payload(TextNode.valueOf("testPayload"))
            .context(Collections.singletonMap(RequestProducer.REQUEST_ID_CONTEXT_KEY, requestId))
            .responseTo(UUID.randomUUID())
            .build();
        consumer.handleMessage(message);

        assertNotNull(listener.getResponse());
        assertEquals("testPayload", listener.getResponse().getPayload());
    }

    @Test
    public void cancelRequest() {
        UUID requestId = UUID.randomUUID();
        TestListener<String> listener = new TestListener<>();
        consumer.registerResponseListener(requestId, listener, String.class);
        consumer.cancelRequest(requestId);

        assertTrue(listener.getError() instanceof CancellationException);
        listener.reset();

        Message<?> message =  Message.builder()
            .type("test.response")
            .payload(TextNode.valueOf("testPayload"))
            .context(Collections.singletonMap(RequestProducer.REQUEST_ID_CONTEXT_KEY, requestId))
            .responseTo(UUID.randomUUID())
            .build();
        consumer.handleMessage(message);

        assertNull(listener.getResponse());
    }

    @Test
    public void errorPayload() {
        UUID requestId = UUID.randomUUID();
        TestListener<Integer> listener = new TestListener<>();
        consumer.registerResponseListener(requestId, listener, Integer.class);

        Message<?> message =  Message.builder()
            .type("test.response")
            .payload(TextNode.valueOf("testPayload"))
            .context(Collections.singletonMap(RequestProducer.REQUEST_ID_CONTEXT_KEY, requestId))
            .responseTo(UUID.randomUUID())
            .build();
        consumer.handleMessage(message);

        assertNotNull(listener.getError());
    }

    private static class TestListener <T> implements ResponseListener <T> {
        private Message<T> response;
        private Throwable error;

        @Override
        public void onResponseReceived(Message<T> response) {
            this.response = response;
        }

        @Override
        public void onError(Throwable t) {
            error = t;
        }

        Message<T> getResponse() {
            return response;
        }

        Throwable getError() {
            return error;
        }

        void reset() {
            response = null;
            error = null;
        }
    }
}
