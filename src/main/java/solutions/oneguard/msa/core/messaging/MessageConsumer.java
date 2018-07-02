/*
 * This file is part of the OneGuard Micro-Service Architecture Core library.
 *
 * (c) OneGuard <contact@oneguard.email>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package solutions.oneguard.msa.core.messaging;

import solutions.oneguard.msa.core.model.Message;

public interface MessageConsumer {
    /**
     * Add new {@link MessageHandler} mapping with message type mapping to the current configuration.
     *
     * <p>Message is routed to the first handler the pattern of which matches the message type.</p>
     *
     * @param pattern message type pattern
     * @param handler message handler
     */
    void addHandler(String pattern, MessageHandler handler);

    /**
     * Sets message handler for message to be routed to, when it matches no pattern.
     *
     * @param defaultHandler default message handler
     */
    void setDefaultHandler(MessageHandler defaultHandler);

    /**
     * Routes the message to correct {@link MessageHandler} and converts its payload to the correct type.
     *
     * @param message the message
     */
    void handleMessage(Message<?> message);
}
