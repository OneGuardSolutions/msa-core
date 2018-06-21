package solutions.oneguard.msa.core.messaging;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.amqp.core.AmqpTemplate;
import solutions.oneguard.msa.core.model.Message;
import solutions.oneguard.msa.core.util.Utils;

import static org.mockito.Mockito.verify;

public class MessageProducerTest {
    @Test
    public void sendToService() {
        Message message = Message.builder().type("test.message").build();
        AmqpTemplate template = Mockito.mock(AmqpTemplate.class);
        MessageProducer producer = new MessageProducer(template);

        producer.send("test-service", message);

        verify(template).convertAndSend(Utils.serviceTopic("test-service"), "test.message", message);
    }

    @Test
    public void sendToServiceWithRoutingKey() {
        Message message = Message.builder().build();
        AmqpTemplate template = Mockito.mock(AmqpTemplate.class);
        MessageProducer producer = new MessageProducer(template);

        producer.send("test-service", message, "test.type");

        verify(template).convertAndSend(Utils.serviceTopic("test-service"), "test.type", message);
    }

    @Test
    public void sendToInstance() {
        Message message = Message.builder().type("test.message").build();
        AmqpTemplate template = Mockito.mock(AmqpTemplate.class);
        MessageProducer producer = new MessageProducer(template);

        producer.send("test-service", "test-instance", message);

        verify(template).convertAndSend(
            Utils.instanceTopic("test-service", "test-instance"),
            "test.message",
            message
        );
    }

    @Test
    public void sendToInstanceWithRoutingKey() {
        Message message = Message.builder().build();
        AmqpTemplate template = Mockito.mock(AmqpTemplate.class);
        MessageProducer producer = new MessageProducer(template);

        producer.send("test-service", "test-instance", message, "test.type");

        verify(template).convertAndSend(
            Utils.instanceTopic("test-service", "test-instance"),
            "test.type",
            message
        );
    }
}