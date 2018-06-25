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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import solutions.oneguard.msa.core.model.Message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MessageConsumerTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void handleMessage() {
        TestHandler<String> handler1 = new TestHandler<>("test.message.1", String.class);
        TestHandler<String> handler2 = new TestHandler<>("test.message.2", String.class);
        MessageConsumer consumer = new MessageConsumer(new ObjectMapper());
        consumer.addHandler("test.message.1", handler1);
        consumer.addHandler("test.message.2", handler2);

        consumer.handleMessage(Message.builder().type("test.message.1").payload("testPayload1").build());
        consumer.handleMessage(Message.builder().type("test.message.2").payload("testPayload2").build());

        assertEquals("testPayload1", handler1.getPayload());
        assertEquals("testPayload2", handler2.getPayload());
    }

    @Test
    public void handleMessageNoHandler() {
        MessageConsumer consumer = new MessageConsumer(new ObjectMapper());

        consumer.handleMessage(Message.builder().type("test.message").build());
    }

    @Test
    public void constructor() {
        TestHandler<String> handler1 = new TestHandler<>("test.message.1", String.class);
        TestHandler<String> handler2 = new TestHandler<>("test.message.1", String.class);
        MessageConsumer consumer = new MessageConsumer(new ObjectMapper());
        consumer.addHandler("test.message.1", handler1);
        consumer.addHandler("test.message.1", handler2);

        consumer.handleMessage(Message.builder().type("test.message.1").payload("testPayload1").build());

        assertEquals("testPayload1", handler1.getPayload());
        assertNull(handler2.getPayload());
    }

    @Test
    public void setDefaultHandler() {
        TestHandler<String> handler = new TestHandler<>(null, String.class);
        MessageConsumer consumer = new MessageConsumer(new ObjectMapper());
        consumer.setDefaultHandler(handler);

        consumer.handleMessage(Message.builder().type("test.message.missing").payload("testPayload").build());

        assertEquals("testPayload", handler.getPayload());
    }

    private static class TestHandler <T> extends AbstractMessageHandler<T> {
        private T payload;

        TestHandler(String messageType, Class<T> messageClass) {
            super(messageType, messageClass);
        }

        @Override
        public void handleMessage(T payload, Message originalMessage) {
            this.payload = payload;
        }

        T getPayload() {
            return payload;
        }
    }
}
