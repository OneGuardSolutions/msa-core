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

    /**
     * Sends a message to the specified service instance exchange.
     *
     * @param instance target service instance representation
     * @param message the message to send
     */
    public void sendToInstance(Instance instance, Message message) {
        sendToInstance(instance, message, message.getType());
    }

    /**
     * Sends a message to the specified service instance exchange.
     *
     * @param instance target service instance representation
     * @param message the message to send
     * @param routingKey AMQP message routing key
     */
    public void sendToInstance(Instance instance, Message message, String routingKey) {
        sendToInstance(instance.getService(), instance.getId().toString(), message, routingKey);
    }

    /**
     * Sends a message to the specified service instance exchange.
     *
     * @param serviceName service name
     * @param instanceId instance ID
     * @param message the message to send
     */
    public void sendToInstance(String serviceName, String instanceId, Message message) {
        sendToInstance(serviceName, instanceId, message, message.getType());
    }

    /**
     * Sends a message to the specified service instance exchange.
     *
     * @param serviceName service name
     * @param instanceId instance ID
     * @param message the message to send
     * @param routingKey AMQP message routing key
     */
    public void sendToInstance(String serviceName, String instanceId, Message message, String routingKey) {
        sendToExchange(Utils.instanceTopic(serviceName, instanceId), message, routingKey);
    }

    /**
     * Sends a message to the specified service exchange.
     *
     * @param serviceName service name
     * @param message the message to send
     */
    public void sendToService(String serviceName, Message message) {
        sendToService(serviceName, message, message.getType());
    }

    /**
     * Sends a message to the specified service exchange.
     *
     * @param serviceName service name
     * @param message the message to send
     * @param routingKey AMQP message routing key
     */
    public void sendToService(String serviceName, Message message, String routingKey) {
        sendToExchange(Utils.serviceTopic(serviceName), message, routingKey);
    }

    /**
     * Construct response to the a message with specified response and sends it to the original message issuer.
     *
     * <p>
     *     Response message is constructed in compliance with
     *     <a href="https://github.com/OneGuardSolutions/msa-interservice-communication-protocol-specification">
     *         inter-service communication protocol specification
     *     </a>.
     *     <ul>
     *         <li>{@link Message#principal} is copied from original message</li>
     *         <li>{@link Message#context} is copied from original message</li>
     *         <li>{@link Message#responseTo} is filled with original message ID</li>
     *     </ul>
     * </p>
     *
     * <p>Original message's property {@link Message#respondToInstance} is taken into account,
     * and response is sent either to the issuing instance or service exchange accordingly.</p>
     *
     * @param requestMessage original message to construct a response from
     * @param messageType response message type
     * @param payload response message payload
     * @param <T> type of response message payload
     */
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

    private void sendToExchange(String exchangeName, Message message, String routingKey) {
        if (message.getId() == null) {
            message.setId(UUID.randomUUID());
        }
        message.setIssuer(currentInstance);
        template.convertAndSend(exchangeName, routingKey, message);
    }
}
