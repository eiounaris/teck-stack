package com.eiou.rabbitmq;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMqConfig {
    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (correlationData != null) {
                System.out.println("confirm: id=" + correlationData.getId() + ", ack=" + ack + ", cause=" + cause);
            }
        });
        rabbitTemplate.setReturnsCallback(returned -> System.out.println(
                "return: replyCode=" + returned.getReplyCode()
                        + ", replyText=" + returned.getReplyText()
                        + ", exchange=" + returned.getExchange()
                        + ", routingKey=" + returned.getRoutingKey()
        ));
        return rabbitTemplate;
    }

    @Bean
    RabbitListenerContainerFactory<SimpleMessageListenerContainer> manualAckListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return factory;
    }

    @Bean
    DirectExchange directExchange() {
        return new DirectExchange(RabbitMqNames.DIRECT_EXCHANGE, true, false);
    }

    @Bean
    Queue directQueue() {
        return new Queue(RabbitMqNames.DIRECT_QUEUE, true);
    }

    @Bean
    Binding directBinding() {
        return BindingBuilder.bind(directQueue()).to(directExchange()).with(RabbitMqNames.DIRECT_ROUTING_KEY);
    }

    @Bean
    DirectExchange listenerExchange() {
        return new DirectExchange(RabbitMqNames.LISTENER_EXCHANGE, true, false);
    }

    @Bean
    Queue listenerQueue() {
        return new Queue(RabbitMqNames.LISTENER_QUEUE, true);
    }

    @Bean
    Binding listenerBinding() {
        return BindingBuilder.bind(listenerQueue()).to(listenerExchange()).with(RabbitMqNames.LISTENER_ROUTING_KEY);
    }

    @Bean
    TopicExchange topicExchange() {
        return new TopicExchange(RabbitMqNames.TOPIC_EXCHANGE, true, false);
    }

    @Bean
    Queue topicCreatedQueue() {
        return new Queue(RabbitMqNames.TOPIC_CREATED_QUEUE, true);
    }

    @Bean
    Queue topicAllQueue() {
        return new Queue(RabbitMqNames.TOPIC_ALL_QUEUE, true);
    }

    @Bean
    Binding topicCreatedBinding() {
        return BindingBuilder.bind(topicCreatedQueue()).to(topicExchange()).with("rabbitmq.topic.created");
    }

    @Bean
    Binding topicAllBinding() {
        return BindingBuilder.bind(topicAllQueue()).to(topicExchange()).with("rabbitmq.topic.*");
    }

    @Bean
    FanoutExchange fanoutExchange() {
        return new FanoutExchange(RabbitMqNames.FANOUT_EXCHANGE, true, false);
    }

    @Bean
    Queue fanoutFirstQueue() {
        return new Queue(RabbitMqNames.FANOUT_FIRST_QUEUE, true);
    }

    @Bean
    Queue fanoutSecondQueue() {
        return new Queue(RabbitMqNames.FANOUT_SECOND_QUEUE, true);
    }

    @Bean
    Binding fanoutFirstBinding() {
        return BindingBuilder.bind(fanoutFirstQueue()).to(fanoutExchange());
    }

    @Bean
    Binding fanoutSecondBinding() {
        return BindingBuilder.bind(fanoutSecondQueue()).to(fanoutExchange());
    }

    @Bean
    DirectExchange confirmExchange() {
        return new DirectExchange(RabbitMqNames.CONFIRM_EXCHANGE, true, false);
    }

    @Bean
    Queue confirmQueue() {
        return new Queue(RabbitMqNames.CONFIRM_QUEUE, true);
    }

    @Bean
    Binding confirmBinding() {
        return BindingBuilder.bind(confirmQueue()).to(confirmExchange()).with(RabbitMqNames.CONFIRM_ROUTING_KEY);
    }

    @Bean
    DirectExchange manualAckExchange() {
        return new DirectExchange(RabbitMqNames.MANUAL_ACK_EXCHANGE, true, false);
    }

    @Bean
    Queue manualAckQueue() {
        return new Queue(RabbitMqNames.MANUAL_ACK_QUEUE, true);
    }

    @Bean
    Binding manualAckBinding() {
        return BindingBuilder.bind(manualAckQueue()).to(manualAckExchange()).with(RabbitMqNames.MANUAL_ACK_ROUTING_KEY);
    }

    @Bean
    DirectExchange ttlExchange() {
        return new DirectExchange(RabbitMqNames.TTL_EXCHANGE, true, false);
    }

    @Bean
    Queue ttlQueue() {
        return QueueBuilder.durable(RabbitMqNames.TTL_QUEUE)
                .ttl(2_000)
                .deadLetterExchange(RabbitMqNames.DEAD_LETTER_EXCHANGE)
                .deadLetterRoutingKey(RabbitMqNames.DEAD_LETTER_ROUTING_KEY)
                .build();
    }

    @Bean
    Binding ttlBinding() {
        return BindingBuilder.bind(ttlQueue()).to(ttlExchange()).with(RabbitMqNames.TTL_ROUTING_KEY);
    }

    @Bean
    DirectExchange deadLetterExchange() {
        return new DirectExchange(RabbitMqNames.DEAD_LETTER_EXCHANGE, true, false);
    }

    @Bean
    Queue deadLetterQueue() {
        return new Queue(RabbitMqNames.DEAD_LETTER_QUEUE, true);
    }

    @Bean
    Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with(RabbitMqNames.DEAD_LETTER_ROUTING_KEY);
    }
}
