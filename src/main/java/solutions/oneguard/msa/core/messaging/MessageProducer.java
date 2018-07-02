/*
 * This file is part of the OneGuard Micro-Service Architecture Core library.
 *
 * (c) OneGuard <contact@oneguard.email>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package solutions.oneguard.msa.core.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import solutions.oneguard.msa.core.model.Instance;
import solutions.oneguard.msa.core.model.Message;
import solutions.oneguard.msa.core.util.Utils;

import java.util.UUID;

public class MessageProducer {
    private final RabbitTemplate template;
    private final Instance currentInstance;

    public MessageProducer(RabbitTemplate template, Instance currentInstance) {
        this.template = template;
        this.currentInstance = currentInstance;
    }

    public void sendToInstance(Instance instance, Message message) {
        sendToInstance(instance, message, message.getType());
    }

    public void sendToInstance(Instance instance, Message message, String routingKey) {
        sendToInstance(instance.getService(), instance.getId().toString(), message, routingKey);
    }

    public void sendToInstance(String serviceName, String instanceId, Message message) {
        sendToInstance(serviceName, instanceId, message, message.getType());
    }

    public void sendToInstance(String serviceName, String instanceId, Message message, String routingKey) {
        message.setIssuer(currentInstance);
        template.convertAndSend(Utils.instanceTopic(serviceName, instanceId), routingKey, message);
    }

    public void sendToService(String serviceName, Message message) {
        sendToService(serviceName, message, message.getType());
    }

    public void sendToService(String serviceName, Message message, String routingKey) {
        if (message.getId() == null) {
            message.setId(UUID.randomUUID());
        }
        message.setIssuer(currentInstance);
        template.convertAndSend(Utils.serviceTopic(serviceName), routingKey, message);
    }

    public <T> void sendResponse(Message<?> requestMessage, String messageType, T payload) {
        Message<T> response = Message.<T>builder()
            .type(messageType)
            .principal(requestMessage.getPrincipal())
            .payload(payload)
            .context(requestMessage.getContext())
            .responseTo(requestMessage.getId())
            .build();

        if (requestMessage.isRespondToInstance()) {
            sendToInstance(requestMessage.getIssuer(), response);
        } else {
            sendToService(requestMessage.getIssuer().getService(), response);
        }
    }
}
