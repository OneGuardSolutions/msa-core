/*
 * This file is part of the OneGuard Micro-Service Architecture Core library.
 *
 * (c) OneGuard <contact@oneguard.email>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package solutions.oneguard.msa.core;

import solutions.oneguard.msa.core.messaging.AbstractMessageHandler;
import solutions.oneguard.msa.core.model.Message;

public class TestHandler <T> extends AbstractMessageHandler<T> {
    private Message<T> message;

    public TestHandler(Class<T> clazz) {
        super(clazz);
    }

    @Override
    public void handleMessage(Message<T> message) {
        this.message = message;
    }

    public Message<T> getMessage() {
        return message;
    }
}
