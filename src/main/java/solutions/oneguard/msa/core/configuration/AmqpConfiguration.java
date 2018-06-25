/*
 * This file is part of the OneGuard Micro-Service Architecture Core library.
 *
 * (c) OneGuard <contact@oneguard.email>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package solutions.oneguard.msa.core.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import solutions.oneguard.msa.core.messaging.MessageConsumer;
import solutions.oneguard.msa.core.messaging.MessageConsumerConfiguration;
import solutions.oneguard.msa.core.messaging.MessageHandler;
import solutions.oneguard.msa.core.messaging.MessageProducer;
import solutions.oneguard.msa.core.model.Instance;
import solutions.oneguard.msa.core.util.Utils;

import java.util.Optional;
import java.util.UUID;

@Configuration
@EnableConfigurationProperties(ServiceProperties.class)
public class AmqpConfiguration {
    /**
     * Creates current services instance representation.
     *
     * @param serviceProperties configuration for current Instance
     * @return current services instance representation
     */
    @Bean
    public Instance currentInstance(ServiceProperties serviceProperties) {
        return new Instance(serviceProperties.getName(), UUID.randomUUID());
    }

    /**
     * Provides declaration of queue to subscribe to as a service.
     *
     * @param currentInstance current services instance representation
     * @return service queue declaration
     */
    @Bean
    public Queue serviceQueue(Instance currentInstance) {
        return new Queue(Utils.serviceTopic(currentInstance), true, false, true);
    }

    /**
     * Creates declaration of queue to subscribe to as a service instance.
     *
     * @param currentInstance current services instance representation
     * @return service instance queue declaration
     */
    @Bean
    public Queue instanceQueue(Instance currentInstance) {
        return new Queue(Utils.instanceTopic(currentInstance), false, false, true);
    }

    /**
     * Creates declaration of exchange to use as a service.
     *
     * @param currentInstance current services instance representation
     * @return service exchange declaration
     */
    @Bean
    public TopicExchange serviceExchange(Instance currentInstance) {
        return new TopicExchange(Utils.serviceTopic(currentInstance), true, false);
    }

    /**
     * Creates declaration of exchange to use as a service instance.
     *
     * @param currentInstance current services instance representation
     * @return service instance exchange declaration
     */
    @Bean
    public TopicExchange instanceExchange(Instance currentInstance) {
        return new TopicExchange(Utils.instanceTopic(currentInstance), false, false);
    }

    /**
     * Creates a binding between a service queue and a service exchange.
     *
     * @param serviceQueue service queue declaration
     * @param serviceExchange service exchange declaration
     * @return service queue to service exchange binding
     */
    @Bean
    public Binding serviceBinding(Queue serviceQueue, TopicExchange serviceExchange) {
        return BindingBuilder.bind(serviceQueue).to(serviceExchange).with("#");
    }

    /**
     * Creates a binding between a service instance queue and a service instance exchange.
     *
     * @param instanceQueue service instance queue declaration
     * @param instanceExchange service instance exchange declaration
     * @return service instance queue to service instance exchange binding
     */
    @Bean
    public Binding instanceBinding(Queue instanceQueue, TopicExchange instanceExchange) {
        return BindingBuilder.bind(instanceQueue).to(instanceExchange).with("#");
    }

    /**
     * Creates an internal message listener container that subscribes to service and insance specific queues.
     *
     * @param connectionFactory RabbitMQ connection factory
     * @param listenerAdapter custom message listener adapter
     * @param serviceQueue service queue declaration
     * @param instanceQueue service instance queue declaration
     * @return internal message listener container
     */
    @Bean
    public SimpleMessageListenerContainer container(
        ConnectionFactory connectionFactory,
        MessageListenerAdapter listenerAdapter,
        Queue serviceQueue,
        Queue instanceQueue
    ) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueues(serviceQueue, instanceQueue);
        container.setMessageListener(listenerAdapter);

        return container;
    }

    /**
     * Creates message converter for Spring Messaging.
     *
     * @return message converter
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * Creates internal message listener adapter.
     *
     * @param receiver custom message consumer
     * @param messageConverter message converter
     * @return internal message listener adapter
     */
    @Bean
    public MessageListenerAdapter listenerAdapter(MessageConsumer receiver, MessageConverter messageConverter) {
        return new MessageListenerAdapter(receiver, messageConverter);
    }

    /**
     * Creates internal RabbitMQ template used to send messages.
     *
     * @param factory RabbitMQ connection factory
     * @param instanceExchange exchange used to send messages
     * @param messageConverter message converter
     * @return RabbitMQ template
     */
    @Bean
    public RabbitTemplate template(
        ConnectionFactory factory,
        TopicExchange instanceExchange,
        MessageConverter messageConverter
    ) {
        RabbitTemplate template = new RabbitTemplate(factory);
        template.setExchange(instanceExchange.getName());
        template.setMessageConverter(messageConverter);

        return template;
    }

    /**
     * Creates message producer that can be used by to send {@link solutions.oneguard.msa.core.model.Message}
     * to other services.
     *
     * @param template RabbitMQ template
     * @return message producer
     */
    @Bean
    public MessageProducer messageProducer(RabbitTemplate template) {
        return new MessageProducer(template);
    }

    /**
     * Creates internal message consumer used to route messages to {@link MessageHandler}s
     * from specified {@link MessageConsumerConfiguration}.
     *
     * @param mapper Jackson object mapper
     * @param configuration message consumer configuration
     * @return message consumer
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @Bean
    public MessageConsumer messageConsumer(
        ObjectMapper mapper,
        Optional<MessageConsumerConfiguration> configuration
    ) {
        MessageConsumer consumer = new MessageConsumer(mapper);
        if (configuration.isPresent()) {
            configuration.get().getHandlers().forEach(
                mapping -> consumer.addHandler(mapping.getPattern(), mapping.getHandler())
            );
            consumer.setDefaultHandler(configuration.get().getDefaultHandler());
        }

        return consumer;
    }
}
