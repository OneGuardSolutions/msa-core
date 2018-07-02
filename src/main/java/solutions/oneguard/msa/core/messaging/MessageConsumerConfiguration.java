/*
 * This file is part of the OneGuard Micro-Service Architecture Core library.
 *
 * (c) OneGuard <contact@oneguard.email>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package solutions.oneguard.msa.core.messaging;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MessageConsumerConfiguration {
    private final List<MessageHandlerMapping> handlers = new LinkedList<>();
    private MessageHandler defaultHandler;

    public List<MessageHandlerMapping> getHandlers() {
        return Collections.unmodifiableList(handlers);
    }

    /**
     * Add new {@link MessageHandler} mapping with message type mapping to the current configuration.
     *
     * <p>Message is routed to the first handler the pattern of which matches the message type.</p>
     *
     * @param pattern message type pattern
     * @param handler message handler
     * @return the same modified configuration
     */
    public MessageConsumerConfiguration addHandler(String pattern, MessageHandler handler) {
        handlers.add(new MessageHandlerMapping(pattern, handler));

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
