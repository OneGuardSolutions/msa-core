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
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import solutions.oneguard.msa.core.model.Instance;
import solutions.oneguard.msa.core.model.Message;
import solutions.oneguard.msa.core.util.Utils;

import java.util.UUID;

import static org.mockito.Mockito.verify;

public class MessageProducerTest {
    private static final String TEST_SERVICE = "test-service";
    private static final String TEST_INSTANCE = "test-instance";
    private static final String ROUTING_KEY = "test.routing.key";

    private static final Message message = Message.builder().type("test.message").build();

    private MessageProducer producer;
    private RabbitTemplate template;

    @Before
    public void setUp() {
        template = Mockito.mock(RabbitTemplate.class);
        template = Mockito.mock(RabbitTemplate.class);
        producer = new MessageProducer(template);

    }

    @Test
    public void sendToService() {
        Instance instance = new Instance(TEST_SERVICE, UUID.randomUUID());
        producer.sendToService(instance, message);

        verify(template).convertAndSend(Utils.serviceTopic(TEST_SERVICE), message.getType(), message);
    }

    @Test
    public void sendToServiceWithRoutingKey() {
        Instance instance = new Instance(TEST_SERVICE, UUID.randomUUID());
        producer.sendToService(instance, message, ROUTING_KEY);

        verify(template).convertAndSend(Utils.serviceTopic(TEST_SERVICE), ROUTING_KEY, message);
    }

    @Test
    public void sendToServiceAsString() {
        producer.sendToService(TEST_SERVICE, message);

        verify(template).convertAndSend(Utils.serviceTopic(TEST_SERVICE), message.getType(), message);
    }

    @Test
    public void sendToServiceAsStringWithRoutingKey() {
        producer.sendToService(TEST_SERVICE, message, ROUTING_KEY);

        verify(template).convertAndSend(Utils.serviceTopic(TEST_SERVICE), ROUTING_KEY, message);
    }

    @Test
    public void sendToInstance() {
        Instance instance = new Instance(TEST_SERVICE, UUID.randomUUID());
        producer.sendToInstance(instance, message);

        verify(template).convertAndSend(Utils.instanceTopic(instance), message.getType(), message);
    }

    @Test
    public void sendToInstanceWithRoutingKey() {
        Instance instance = new Instance(TEST_SERVICE, UUID.randomUUID());
        producer.sendToInstance(instance, message, ROUTING_KEY);

        verify(template).convertAndSend(Utils.instanceTopic(instance), ROUTING_KEY, message);
    }

    @Test
    public void sendToInstanceAsStrings() {
        producer.sendToInstance(TEST_SERVICE, TEST_INSTANCE, message);

        verify(template).convertAndSend(Utils.instanceTopic(TEST_SERVICE, TEST_INSTANCE), message.getType(), message);
    }

    @Test
    public void sendToInstanceAsStringsWithRoutingKey() {
        producer.sendToInstance(TEST_SERVICE, TEST_INSTANCE, message, ROUTING_KEY);

        verify(template).convertAndSend(Utils.instanceTopic(TEST_SERVICE, TEST_INSTANCE), ROUTING_KEY, message);
    }
}
