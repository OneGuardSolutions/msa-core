/*
 * This file is part of the OneGuard Micro-Service Architecture Core library.
 *
 * (c) OneGuard <contact@oneguard.email>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package solutions.oneguard.msa.core.messaging;

import org.junit.Test;
import solutions.oneguard.msa.core.model.Message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AbstractMessageHandlerTest {
    private static final String TEST_MSG = "test.message";

    @Test
    public void getMessageType() {
        AbstractMessageHandler<String> handler = new AbstractMessageHandlerTestImpl<>(TEST_MSG, String.class);

        assertEquals(TEST_MSG, handler.getMessageType());
    }

    @Test
    public void getMessageClass() {
        AbstractMessageHandler<String> handler = new AbstractMessageHandlerTestImpl<>(TEST_MSG, String.class);

        assertNotNull(handler.getMessageClass());
    }

    private static class AbstractMessageHandlerTestImpl <T> extends AbstractMessageHandler<T> {
        private AbstractMessageHandlerTestImpl(String messageType, Class<T> messageClass) {
            super(messageType, messageClass);
        }

        @Override
        public void handleMessage(T payload, Message originalMessage) {}
    }
}
