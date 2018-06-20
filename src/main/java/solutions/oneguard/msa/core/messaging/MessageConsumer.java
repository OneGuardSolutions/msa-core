package solutions.oneguard.msa.core.messaging;

import org.springframework.stereotype.Component;
import solutions.oneguard.msa.core.model.Message;

@Component
public class MessageConsumer {
    public void handleMessage(Message message) {
        System.out.println("Received <" + message + ">");
    }
}
