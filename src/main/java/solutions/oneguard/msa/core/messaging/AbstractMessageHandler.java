/*
 * This file is part of the OneGuard Micro-Service Architecture Core library.
 *
 * (c) OneGuard <contact@oneguard.email>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package solutions.oneguard.msa.core.messaging;

public abstract class AbstractMessageHandler <T> implements MessageHandler<T> {
    private final Class<T> messageClass;

    protected AbstractMessageHandler(Class<T> messageClass) {
        this.messageClass = messageClass;
    }

    @Override
    public Class<T> getMessageClass() {
        return messageClass;
    }
}
