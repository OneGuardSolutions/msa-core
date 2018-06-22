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

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class MessageConsumerTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void handleMessage() {
        TestHandler<String> handler1 = new TestHandler<>("test.message.1", String.class);
        TestHandler<String> handler2 = new TestHandler<>("test.message.2", String.class);
        MessageConsumer consumer = new MessageConsumer(new ObjectMapper(), Arrays.asList(handler1, handler2));

        consumer.handleMessage(Message.builder().type("test.message.1").payload("testPayload1").build());
        consumer.handleMessage(Message.builder().type("test.message.2").payload("testPayload2").build());

        assertEquals("testPayload1", handler1.getPayload());
        assertEquals("testPayload2", handler2.getPayload());
    }

    @Test
    public void handleMessageNoHandler() {
        MessageConsumer consumer = new MessageConsumer(new ObjectMapper(), Collections.emptyList());

        consumer.handleMessage(Message.builder().type("test.message").build());
    }

    @Test
    public void constuctor() {
        TestHandler<String> handler1 = new TestHandler<>("test.message.1", String.class);
        TestHandler<String> handler2 = new TestHandler<>("test.message.1", String.class);

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Handler already registered for 'test.message.1'");

        new MessageConsumer(new ObjectMapper(), Arrays.asList(handler1, handler2));
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
