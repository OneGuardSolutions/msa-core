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

public class MessageConsumer {
    private static final Logger logger = LoggerFactory.getLogger(MessageConsumer.class);

    private final List<MessageHandlerMapping> handlers = new LinkedList<>();
    private final ObjectMapper objectMapper;
    private MessageHandler defaultHandler;

    public MessageConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Add new {@link MessageHandler} mapping with message type mapping to the current configuration.
     *
     * <p>Message is routed to the first handler the pattern of which matches the message type.</p>
     *
     * @param pattern message type pattern
     * @param handler message handler
     */
    public void addHandler(String pattern, MessageHandler handler) {
        this.handlers.add(new MessageHandlerMapping(pattern, handler));
    }

    /**
     * Sets message handler for message to be routed to, when it matches no pattern.
     *
     * @param defaultHandler default message handler
     */
    public void setDefaultHandler(MessageHandler defaultHandler) {
        this.defaultHandler = defaultHandler;
    }

    /**
     * Routes the message to correct {@link MessageHandler} and converts its payload to the correct type.
     *
     * @param message the message
     */
    public void handleMessage(Message message) {
        MessageHandler handler = handlers.stream().filter(
            mapping -> mapping.getPattern().matches(message.getType())
        ).findFirst().map(MessageHandlerMapping::getHandler).orElse(defaultHandler);

        if (handler == null) {
            logger.info("Received message with no handler: <{}>", message);
            return;
        }

        //noinspection unchecked
        handler.handleMessage(
            objectMapper.convertValue(message.getPayload(), handler.getMessageClass()),
            message
        );
    }
}
