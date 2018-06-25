package solutions.oneguard.msa.core.messaging;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class MessageConsumerConfigurationTest {
    @Test
    public void getHandlers() {
        MessageHandler handler1 = Mockito.mock(MessageHandler.class);
        MessageHandler handler2 = Mockito.mock(MessageHandler.class);
        MessageConsumerConfiguration configuration = new MessageConsumerConfiguration()
            .addHandler("test.1", handler1)
            .addHandler("test.2", handler2);

        assertEquals(
            Arrays.asList(
                new MessageHandlerMapping("test.1", handler1),
                new MessageHandlerMapping("test.2", handler2)
            ),
            configuration.getHandlers()
        );
    }

    @Test
    public void setDefaultHandler() {
        MessageHandler handler = Mockito.mock(MessageHandler.class);
        MessageConsumerConfiguration configuration = new MessageConsumerConfiguration()
            .setDefaultHandler(handler);

        assertSame(handler, configuration.getDefaultHandler());
    }
}
