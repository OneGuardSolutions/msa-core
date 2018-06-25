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
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import solutions.oneguard.msa.core.model.Message;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class MessageConsumer {
    private static final Logger logger = LoggerFactory.getLogger(MessageConsumer.class);

    private final List<Pair<Pattern, MessageHandler>> handlers = new LinkedList<>();
    private final ObjectMapper objectMapper;
    private MessageHandler defaultHandler;

    public MessageConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void addHandler(String pattern, MessageHandler handler) {
        this.handlers.add(new Pair<>(Pattern.compile(pattern), handler));
    }

    public void setDefaultHandler(MessageHandler defaultHandler) {
        this.defaultHandler = defaultHandler;
    }

    public void handleMessage(Message message) {
        MessageHandler handler = handlers.stream().filter(
            pair -> pair.getKey().matcher(message.getType()).matches()
        ).findFirst().map(Pair::getValue).orElse(defaultHandler);


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
