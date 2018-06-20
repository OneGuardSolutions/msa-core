package solutions.oneguard.msa.core.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import solutions.oneguard.msa.core.messaging.MessageConsumer;
import solutions.oneguard.msa.core.model.Instance;
import solutions.oneguard.msa.core.util.Utils;

@Configuration
public class AmqpConfiguration {
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
    public MessageListenerAdapter listenerAdapter(MessageConsumer receiver) {
        return new MessageListenerAdapter(receiver, new Jackson2JsonMessageConverter());
    }
}
