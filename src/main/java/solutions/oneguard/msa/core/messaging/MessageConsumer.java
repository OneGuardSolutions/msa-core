package solutions.oneguard.msa.core.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import solutions.oneguard.msa.core.model.Message;

@Service
public class MessageConsumer {
    private static final Logger logger = LoggerFactory.getLogger(MessageConsumer.class);

    public void handleMessage(Message message) {
        logger.info("Received <{}>", message);
    }
}
