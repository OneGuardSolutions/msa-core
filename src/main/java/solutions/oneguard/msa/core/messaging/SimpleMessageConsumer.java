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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import solutions.oneguard.msa.core.model.Message;

import java.util.LinkedList;
import java.util.List;

public class SimpleMessageConsumer implements MessageConsumer {
    private static final Logger logger = LoggerFactory.getLogger(SimpleMessageConsumer.class);

    private final ObjectMapper objectMapper;

    private final List<MessageHandlerMapping> handlers = new LinkedList<>();
    private MessageHandler defaultHandler;

    public SimpleMessageConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void addHandler(String pattern, MessageHandler handler) {
        this.handlers.add(new MessageHandlerMapping(pattern, handler));
    }

    @Override
    public void setDefaultHandler(MessageHandler defaultHandler) {
        this.defaultHandler = defaultHandler;
    }

    @Override
    public void handleMessage(Message<?> message) {
        MessageHandler handler = handlers.stream().filter(
            mapping -> mapping.getPattern().matches(message.getType())
        ).findFirst().map(MessageHandlerMapping::getHandler).orElse(defaultHandler);

        if (handler == null) {
            logger.info("Received message with no handler: <{}>", message);
            return;
        }

        //noinspection unchecked
        Message convertedMessage = convertMessage(message, handler.getMessageClass());

        //noinspection unchecked
        handler.handleMessage(convertedMessage);
    }

    private  <T> T convertValue(Object payload, Class<T> clazz) {
        return objectMapper.convertValue(payload, clazz);
    }

    protected <T> Message<T> convertMessage(Message<?> message, Class<T> payloadClass) {
        T payload = convertValue(message.getPayload(), payloadClass);

        return new Message<>(
            message.getId(),
            message.getType(),
            message.getPrincipal(),
            message.getIssuer(),
            payload,
            message.getContext(),
            message.getResponseTo(),
            message.getOccurredAt(),
            message.isRespondToInstance()
        );
    }
}
