/*
 * This file is part of the OneGuard Micro-Service Architecture Core library.
 *
 * (c) OneGuard <contact@oneguard.email>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package solutions.oneguard.msa.core;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import solutions.oneguard.msa.core.messaging.MessageConsumerConfiguration;
import solutions.oneguard.msa.core.messaging.MessageProducer;
import solutions.oneguard.msa.core.model.Instance;
import solutions.oneguard.msa.core.model.Message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest({
    "oneguard.msa.service.name=test",
    "spring.main.banner-mode=off"
})
public class MessagingIntegrationTest {
    private static final String TEST_MSG_TYPE = "test.receive";

    @Autowired
    private MessageProducer producer;

    @Autowired
    private TestHandler<ComplexTestPayload> handler;

    @Autowired
    private Instance instance;

    @Test
    public void handleMessage() throws InterruptedException {
        ComplexTestPayload payload = new ComplexTestPayload();
        payload.setTestPayload(new TestPayload());
        payload.getTestPayload().setStringProperty("testValue");
        payload.getTestPayload().setIntProperty(12);

        producer.sendToInstance(instance, Message.builder()
            .type(TEST_MSG_TYPE)
            .issuer(instance)
            .payload(payload)
            .build()
        );

        for (int i = 0; i < 500; i++) {
            Thread.sleep(10);
            if (handler.getMessage() != null) {
                break;
            }
        }

        assertNotNull(handler.getMessage());
        assertEquals("testValue", handler.getMessage().getPayload().getTestPayload().getStringProperty());
        assertEquals(12, handler.getMessage().getPayload().getTestPayload().getIntProperty());
    }

    @SpringBootApplication
    @Configuration
    public static class TestConfiguration {
        @Bean
        public TestHandler<ComplexTestPayload> testPayloadMessageHandler() {
            return new TestHandler<>(ComplexTestPayload.class);
        }

        @Bean
        public MessageConsumerConfiguration messageConsumerConfiguration(TestHandler<ComplexTestPayload> handler) {
            return new MessageConsumerConfiguration().addHandler(TEST_MSG_TYPE, handler);
        }
    }

    @Data
    @NoArgsConstructor
    private static class ComplexTestPayload {
        private TestPayload testPayload;
    }

    @Data
    @NoArgsConstructor
    private static class TestPayload {
        private String stringProperty;
        private int intProperty;
    }
}
