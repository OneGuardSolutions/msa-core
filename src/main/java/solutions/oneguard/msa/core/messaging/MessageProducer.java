package solutions.oneguard.msa.core.messaging;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import solutions.oneguard.msa.core.model.Message;
import solutions.oneguard.msa.core.util.Utils;

@Service
public class MessageProducer {
    private final AmqpTemplate amqpTemplate;

    @Autowired
    public MessageProducer(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    public void send(String service, Message message) {
        send(service, message, message.getType());
    }

    public void send(String service, Message message, String routingKey) {
        amqpTemplate.convertAndSend(Utils.serviceTopic(service), routingKey, message);
    }

    public void send(String service, String instance, Message message) {
        send(service, instance, message, message.getType());
    }

    public void send(String service, String instance, Message message, String routingKey) {
        amqpTemplate.convertAndSend(Utils.instanceTopic(service, instance), routingKey, message);
    }
}
