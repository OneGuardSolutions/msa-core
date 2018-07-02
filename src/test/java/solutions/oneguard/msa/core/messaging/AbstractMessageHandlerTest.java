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

import static org.junit.Assert.assertNotNull;

public class AbstractMessageHandlerTest {
    @Test
    public void getMessageClass() {
        AbstractMessageHandler<String> handler = new AbstractMessageHandlerTestImpl();

        assertNotNull(handler.getMessageClass());
    }

    private static class AbstractMessageHandlerTestImpl extends AbstractMessageHandler<String> {
        private AbstractMessageHandlerTestImpl() {
            super(String.class);
        }

        @Override
        public void handleMessage(Message<String> message) {}
    }
}
