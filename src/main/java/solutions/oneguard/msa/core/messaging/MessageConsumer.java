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

    public void addHandler(String pattern, MessageHandler handler) {
        this.handlers.add(new MessageHandlerMapping(pattern, handler));
    }

    public void setDefaultHandler(MessageHandler defaultHandler) {
        this.defaultHandler = defaultHandler;
    }

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

    public static final class MessageHandlerMapping {
        private final String pattern;
        private final MessageHandler handler;

        MessageHandlerMapping(String pattern, MessageHandler handler) {
            this.pattern = pattern;
            this.handler = handler;
        }

        String getPattern() {
            return pattern;
        }

        MessageHandler getHandler() {
            return handler;
        }
    }
}
