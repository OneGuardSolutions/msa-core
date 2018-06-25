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
import java.util.Objects;

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

    public static final class MessageHandlerMapping {
        private final String pattern;
        private final MessageHandler handler;

        MessageHandlerMapping(String pattern, MessageHandler handler) {
            this.pattern = pattern;
            this.handler = handler;
        }

        /**
         * Returns matching pattern.
         *
         * @return matching pattern
         */
        public String getPattern() {
            return pattern;
        }

        /**
         * Returns mapped message handler.
         *
         * @return message handler
         */
        public MessageHandler getHandler() {
            return handler;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MessageHandlerMapping that = (MessageHandlerMapping) o;

            return Objects.equals(pattern, that.pattern) &&
                Objects.equals(handler, that.handler);
        }
    }
}
