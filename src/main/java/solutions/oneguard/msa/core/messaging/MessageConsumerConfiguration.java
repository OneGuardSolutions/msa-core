package solutions.oneguard.msa.core.messaging;

import javafx.util.Pair;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MessageConsumerConfiguration {
    private final List<Pair<String, MessageHandler>> handlers = new LinkedList<>();
    private MessageHandler defaultHandler;

    public List<Pair<String, MessageHandler>> getHandlers() {
        return Collections.unmodifiableList(handlers);
    }

    /**
     * Add new {@link MessageHandler} mapping with message type mapping to the current configuration.
     *
     * Message is routed to the first handler the pattern of which matches the message type.
     *
     * @param pattern message type pattern
     * @param handler message handler
     * @return the same modified configuration
     */
    public MessageConsumerConfiguration addHandler(String pattern, MessageHandler handler) {
        handlers.add(new Pair<>(pattern, handler));

        return this;
    }

    public MessageHandler getDefaultHandler() {
        return defaultHandler;
    }

    /**
     * Sets the default message handler to be used when no other pattern is matched.
     *
     * @param defaultHandler the default message handler
     * @return the same modified configuration
     */
    public MessageConsumerConfiguration setDefaultHandler(MessageHandler defaultHandler) {
        this.defaultHandler = defaultHandler;

        return this;
    }
}
