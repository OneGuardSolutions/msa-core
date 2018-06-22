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
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import solutions.oneguard.msa.core.messaging.MessageConsumer;
import solutions.oneguard.msa.core.messaging.MessageHandler;
import solutions.oneguard.msa.core.messaging.MessageProducer;
import solutions.oneguard.msa.core.model.Instance;
import solutions.oneguard.msa.core.util.Utils;

import java.util.Collection;
import java.util.UUID;

@Configuration
@EnableConfigurationProperties(ServiceProperties.class)
public class AmqpConfiguration {
    @Bean
    public Instance currentInstance(ServiceProperties serviceProperties) {
        return new Instance(serviceProperties.getName(), UUID.randomUUID());
    }

    @Bean
    public Queue serviceQueue(Instance currentInstance) {
        return new Queue(Utils.serviceTopic(currentInstance), true, false, true);
    }

    @Bean
    public Queue instanceQueue(Instance currentInstance) {
        return new Queue(Utils.instanceTopic(currentInstance), false, false, true);
    }

    @Bean
    public TopicExchange serviceExchange(Instance currentInstance) {
        return new TopicExchange(Utils.serviceTopic(currentInstance), true, false);
    }

    @Bean
    public TopicExchange instanceExchange(Instance currentInstance) {
        return new TopicExchange(Utils.instanceTopic(currentInstance), false, false);
    }

    @Bean
    public Binding serviceBinding(Queue serviceQueue, TopicExchange serviceExchange) {
        return BindingBuilder.bind(serviceQueue).to(serviceExchange).with("#");
    }

    @Bean
    public Binding instanceBinding(Queue instanceQueue, TopicExchange instanceExchange) {
        return BindingBuilder.bind(instanceQueue).to(instanceExchange).with("#");
    }

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

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(MessageConsumer receiver, MessageConverter messageConverter) {
        return new MessageListenerAdapter(receiver, messageConverter);
    }

    @Bean
    public RabbitTemplate serviceTemplate(
        ConnectionFactory factory,
        TopicExchange serviceExchange,
        MessageConverter messageConverter
    ) {
        RabbitTemplate template = new RabbitTemplate(factory);
        template.setExchange(serviceExchange.getName());
        template.setMessageConverter(messageConverter);

        return template;
    }

    @Bean
    public RabbitTemplate instanceTemplate(
        ConnectionFactory factory,
        TopicExchange instanceExchange,
        MessageConverter messageConverter
    ) {
        RabbitTemplate template = new RabbitTemplate(factory);
        template.setExchange(instanceExchange.getName());
        template.setMessageConverter(messageConverter);

        return template;
    }

    @Bean
    public MessageProducer messageProducer(RabbitTemplate serviceTemplate, RabbitTemplate instanceTemplate) {
        return new MessageProducer(serviceTemplate, instanceTemplate);
    }

    @Bean
    public MessageConsumer messageConsumer(
        ObjectMapper mapper,
        ListableBeanFactory beanFactory
    ) {
        Collection<MessageHandler> handlers = beanFactory.getBeansOfType(MessageHandler.class).values();

        return new MessageConsumer(mapper, handlers);
    }
}
