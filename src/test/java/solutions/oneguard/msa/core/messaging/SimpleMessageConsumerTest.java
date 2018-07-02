/*
 * This file is part of the OneGuard Micro-Service Architecture Core library.
 *
 * (c) OneGuard <contact@oneguard.email>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package solutions.oneguard.msa.core.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.Before;
import org.junit.Test;

import solutions.oneguard.msa.core.TestHandler;
import solutions.oneguard.msa.core.model.Message;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SimpleMessageConsumerTest {
    private SimpleMessageConsumer consumer;

    @Before
    public void setUp() {
        consumer = new SimpleMessageConsumer(new ObjectMapper());
    }

    @Test
    public void handleMessage() {
        TestHandler<String> handler1 = new TestHandler<>(String.class);
        TestHandler<String> handler2 = new TestHandler<>(String.class);
        consumer.addHandler("test.message.1", handler1);
        consumer.addHandler("test.message.2", handler2);

        consumer.handleMessage(
            Message.<JsonNode>builder().type("test.message.1").payload(TextNode.valueOf("testPayload1")).build()
        );
        consumer.handleMessage(
            Message.<JsonNode>builder().type("test.message.2").payload(TextNode.valueOf("testPayload2")).build()
        );

        assertNotNull(handler1.getMessage());
        assertEquals("testPayload1", handler1.getMessage().getPayload());
        assertNotNull(handler2.getMessage());
        assertEquals("testPayload2", handler2.getMessage().getPayload());
    }

    @Test
    public void handleMessageNoHandler() {
        consumer.handleMessage(
            Message.<JsonNode>builder().type("test.message").payload(TextNode.valueOf("testPayload")).build()
        );
    }

    @Test
    public void handleMessageMoreHandlersWithSamePattern() {
        TestHandler<String> handler1 = new TestHandler<>(String.class);
        TestHandler<String> handler2 = new TestHandler<>(String.class);
        consumer.addHandler("test.message.1", handler1);
        consumer.addHandler("test.message.1", handler2);

        consumer.handleMessage(
            Message.<JsonNode>builder().type("test.message.1").payload(TextNode.valueOf("testPayload1")).build()
        );

        assertNotNull(handler1.getMessage());
        assertEquals("testPayload1", handler1.getMessage().getPayload());
        assertNull(handler2.getMessage());
    }

    @Test
    public void setDefaultHandler() {
        TestHandler<String> handler = new TestHandler<>(String.class);
        consumer.setDefaultHandler(handler);

        consumer.handleMessage(
            Message.<JsonNode>builder().type("test.message.missing").payload(TextNode.valueOf("testPayload")).build()
        );

        assertEquals("testPayload", handler.getMessage().getPayload());
    }
}
