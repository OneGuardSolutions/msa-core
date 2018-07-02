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
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import solutions.oneguard.msa.core.model.Instance;
import solutions.oneguard.msa.core.model.Message;
import solutions.oneguard.msa.core.util.Utils;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static junit.framework.TestCase.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class MessageProducerTest {
    private static final String TEST_SERVICE = "test-service";
    private static final String TEST_INSTANCE = "test-currentInstance";
    private static final String ROUTING_KEY = "test.routing.key";

    private Message<String> message;
    private MessageProducer producer;
    private RabbitTemplate template;
    private Instance currentInstance;

    @Before
    public void setUp() {
        message = Message.<String>builder().type("test.message").payload("testPayload").build();
        template = Mockito.mock(RabbitTemplate.class);
        currentInstance = new Instance("test", UUID.randomUUID());
        producer = new MessageProducer(template, currentInstance);
    }

    @Test
    public void sendToServiceAsString() {
        producer.sendToService(TEST_SERVICE, message);

        verify(template).convertAndSend(Utils.serviceTopic(TEST_SERVICE), message.getType(), message);
        assertSame(currentInstance, message.getIssuer());
    }

    @Test
    public void sendToServiceAsStringWithRoutingKey() {
        producer.sendToService(TEST_SERVICE, message, ROUTING_KEY);

        verify(template).convertAndSend(Utils.serviceTopic(TEST_SERVICE), ROUTING_KEY, message);
        assertSame(currentInstance, message.getIssuer());
    }

    @Test
    public void sendToInstance() {
        Instance instance = new Instance(TEST_SERVICE, UUID.randomUUID());
        producer.sendToInstance(instance, message);

        verify(template).convertAndSend(Utils.instanceTopic(instance), message.getType(), message);
        assertSame(currentInstance, message.getIssuer());
    }

    @Test
    public void sendToInstanceWithRoutingKey() {
        Instance instance = new Instance(TEST_SERVICE, UUID.randomUUID());
        producer.sendToInstance(instance, message, ROUTING_KEY);

        verify(template).convertAndSend(Utils.instanceTopic(instance), ROUTING_KEY, message);
        assertSame(currentInstance, message.getIssuer());
    }

    @Test
    public void sendToInstanceAsStrings() {
        producer.sendToInstance(TEST_SERVICE, TEST_INSTANCE, message);

        verify(template).convertAndSend(Utils.instanceTopic(TEST_SERVICE, TEST_INSTANCE), message.getType(), message);
        assertSame(currentInstance, message.getIssuer());
    }

    @Test
    public void sendToInstanceAsStringsWithRoutingKey() {
        producer.sendToInstance(TEST_SERVICE, TEST_INSTANCE, message, ROUTING_KEY);

        verify(template).convertAndSend(Utils.instanceTopic(TEST_SERVICE, TEST_INSTANCE), ROUTING_KEY, message);
        assertSame(currentInstance, message.getIssuer());
    }

    @Test
    public void sendResponseToInstance() {
        Map<String, Object> context = Collections.singletonMap("test", "value");
        message.setRespondToInstance(true);
        message.setContext(context);
        message.setIssuer(currentInstance);

        MessageProducer producer = spy(this.producer);
        //noinspection unchecked
        ArgumentCaptor<Message<String>> messageCaptor = ArgumentCaptor.forClass(Message.class);
        doNothing().when(producer).sendToInstance(eq(currentInstance), messageCaptor.capture());

        producer.sendResponse(message, "test.response", "testResponsePayload");

        Message<String> response = messageCaptor.getValue();
        assertNotNull(response);
        assertEquals("test.response", response.getType());
        assertEquals(context, response.getContext());
        assertEquals(message.getId(), response.getResponseTo());
        assertEquals("testResponsePayload", response.getPayload());
    }

    @Test
    public void sendResponseToService() {
        Map<String, Object> context = Collections.singletonMap("test", "value");
        message.setRespondToInstance(false); // false is default value; set just for clarity
        message.setContext(context);
        message.setIssuer(currentInstance);

        MessageProducer producer = spy(this.producer);
        //noinspection unchecked
        ArgumentCaptor<Message<String>> messageCaptor = ArgumentCaptor.forClass(Message.class);
        doNothing().when(producer).sendToService(eq(currentInstance.getService()), messageCaptor.capture());

        producer.sendResponse(message, "test.response", "testResponsePayload");

        Message<String> response = messageCaptor.getValue();
        assertNotNull(response);
        assertEquals("test.response", response.getType());
        assertEquals(context, response.getContext());
        assertEquals(message.getId(), response.getResponseTo());
        assertEquals("testResponsePayload", response.getPayload());
    }
}
