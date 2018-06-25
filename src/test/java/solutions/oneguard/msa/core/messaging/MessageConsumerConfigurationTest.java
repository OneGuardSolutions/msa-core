package solutions.oneguard.msa.core.messaging;

import javafx.util.Pair;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class MessageConsumerConfigurationTest {

    @Test
    public void getHandlers() {
        MessageConsumerConfiguration configuration = new MessageConsumerConfiguration();
        MessageHandler handler1 = Mockito.mock(MessageHandler.class);
        MessageHandler handler2 = Mockito.mock(MessageHandler.class);
        configuration.addHandler("test.1", handler1);
        configuration.addHandler("test.2", handler2);

        assertEquals(
            Arrays.asList(
                new Pair<>("test.1", handler1),
                new Pair<>("test.2", handler2)
            ),
            configuration.getHandlers()
        );
    }

    @Test
    public void setDefaultHandler() {
        MessageConsumerConfiguration configuration = new MessageConsumerConfiguration();
        MessageHandler handler = Mockito.mock(MessageHandler.class);
        configuration.setDefaultHandler(handler);

        assertSame(handler, configuration.getDefaultHandler());
    }
}
