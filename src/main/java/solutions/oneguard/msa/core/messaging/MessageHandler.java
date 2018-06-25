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

public interface MessageHandler <T> {
    /**
     * Returns the class of {@link Message} payload.
     *
     * @return message payload class
     */
    Class<T> getMessageClass();

    /**
     * Handles the message.
     *
     * @param payload converted message payload.
     * @param originalMessage the original message
     */
    void handleMessage(T payload, Message originalMessage);
}
