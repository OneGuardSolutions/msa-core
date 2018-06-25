package solutions.oneguard.msa.core.messaging;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class MessageHandlerMappingTest {
    @Test
    public void getPatternAndHandler() {
        MessageHandler handler = Mockito.mock(MessageHandler.class);
        MessageHandlerMapping mapping = new MessageHandlerMapping("test.pattern", handler);

        assertEquals("test.pattern", mapping.getPattern());
        assertSame(handler, mapping.getHandler());
    }

    @Test
    public void testHashCode() {
        MessageHandler handler = Mockito.mock(MessageHandler.class);
        MessageHandlerMapping mapping = new MessageHandlerMapping("test.pattern", handler);

        assertNotEquals(0, mapping.hashCode());
    }

    @Test
    public void equals() {
        MessageHandler handler1 = Mockito.mock(MessageHandler.class);
        MessageHandlerMapping mapping1 = new MessageHandlerMapping("test.pattern.1", handler1);
        MessageHandlerMapping mapping2 = new MessageHandlerMapping("test.pattern.1", handler1);
        MessageHandlerMapping mapping3 = new MessageHandlerMapping("test.pattern.2", handler1);

        assertFalse(mapping1.equals(null));
        assertTrue(mapping1.equals(mapping1));
        assertFalse(mapping1.equals("not mapping"));
        assertTrue(mapping1.equals(mapping2));
        assertFalse(mapping1.equals(mapping3));
    }
}
