/*
 * This file is part of the OneGuard Micro-Service Architecture Core library.
 *
 * (c) OneGuard <contact@oneguard.email>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package solutions.oneguard.msa.core.messaging;

import java.util.Objects;

public final class MessageHandlerMapping {
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
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MessageHandlerMapping)) {
            return false;
        }
        MessageHandlerMapping that = (MessageHandlerMapping) other;

        return Objects.equals(pattern, that.pattern) && Objects.equals(handler, that.handler);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pattern, handler);
    }
}
