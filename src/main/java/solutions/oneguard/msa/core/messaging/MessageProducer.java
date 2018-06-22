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

public class MessageProducer {
    private final RabbitTemplate serviceTemplate;
    private final RabbitTemplate instanceTemplate;

    public MessageProducer(RabbitTemplate serviceTemplate, RabbitTemplate instanceTemplate) {
        this.serviceTemplate = serviceTemplate;
        this.instanceTemplate = instanceTemplate;
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
        instanceTemplate.convertAndSend(Utils.instanceTopic(serviceName, instanceId), routingKey, message);
    }

    public void sendToService(Instance instance, Message message) {
        sendToService(instance, message, message.getType());
    }

    public void sendToService(Instance instance, Message message, String routingKey) {
        sendToService(instance.getService(), message, routingKey);
    }

    public void sendToService(String serviceName, Message message) {
        sendToService(serviceName, message, message.getType());
    }

    public void sendToService(String serviceName, Message message, String routingKey) {
        serviceTemplate.convertAndSend(Utils.serviceTopic(serviceName), routingKey, message);
    }
}
