package solutions.oneguard.msa.core.messaging;

import javafx.util.Pair;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MessageConsumerConfiguration {
    private final List<Pair<String, MessageHandler>> handlers = new LinkedList<>();
    private MessageHandler defaultHandler;

    public MessageConsumerConfiguration addHandler(String pattern, MessageHandler handler) {
        handlers.add(new Pair<>(pattern, handler));

        return this;
    }

    public List<Pair<String, MessageHandler>> getHandlers() {
        return Collections.unmodifiableList(handlers);
    }

    public MessageHandler getDefaultHandler() {
        return defaultHandler;
    }

    public MessageConsumerConfiguration setDefaultHandler(MessageHandler defaultHandler) {
        this.defaultHandler = defaultHandler;

        return this;
    }
}
