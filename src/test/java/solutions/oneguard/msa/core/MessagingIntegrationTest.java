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
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

import solutions.oneguard.msa.core.messaging.AbstractMessageHandler;
import solutions.oneguard.msa.core.messaging.MessageConsumerConfiguration;
import solutions.oneguard.msa.core.messaging.MessageProducer;
import solutions.oneguard.msa.core.model.Instance;
import solutions.oneguard.msa.core.model.Message;

import java.util.Collections;

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
    private TestConfiguration.TestHandler handler;

    @Autowired
    private Instance instance;

    @Test
    public void handleMessage() throws InterruptedException {
        producer.sendToInstance(instance, Message.builder()
            .type(TEST_MSG_TYPE)
            .issuer(instance)
            .payload(Collections.singletonMap("testProperty", "testValue"))
            .build()
        );
        Thread.sleep(500);

        assertNotNull(handler.getPayload());
        assertEquals("testValue", handler.getPayload().getTestProperty());
    }

    @SpringBootApplication
    @Configuration
    public static class TestConfiguration {
        @Component
        public static class TestHandler extends AbstractMessageHandler<TestPayload> {
            private TestPayload payload;

            public TestHandler() {
                super(TEST_MSG_TYPE, TestPayload.class);
            }

            @Override
            public void handleMessage(TestPayload payload, Message originalMessage) {
                this.payload = payload;
            }

            TestPayload getPayload() {
                return payload;
            }
        }

        @Bean
        public MessageConsumerConfiguration messageConsumerConfiguration(TestHandler testHandler) {
            return new MessageConsumerConfiguration()
                .addHandler(TEST_MSG_TYPE, testHandler);
        }
    }

    @Data
    @NoArgsConstructor
    private static class TestPayload {
        private String testProperty;
    }
}
