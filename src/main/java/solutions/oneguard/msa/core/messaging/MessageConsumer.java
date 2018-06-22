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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MessageConsumer {
    private static final Logger logger = LoggerFactory.getLogger(MessageConsumer.class);

    private final Map<String, MessageHandler> handlers = new HashMap<>();
    private final ObjectMapper objectMapper;

    public MessageConsumer(ObjectMapper objectMapper, Collection<MessageHandler> handlers) {
        this.objectMapper = objectMapper;

        for (MessageHandler handler : handlers) {
            if (this.handlers.containsKey(handler.getMessageType())) {
                throw new IllegalArgumentException(
                    String.format("Handler already registered for '%s'", handler.getMessageType())
                );
            }
            this.handlers.put(handler.getMessageType(), handler);
        }
    }

    public void handleMessage(Message message) {
        MessageHandler handler = handlers.get(message.getType());
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
