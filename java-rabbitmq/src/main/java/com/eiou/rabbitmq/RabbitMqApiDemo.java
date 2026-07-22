package com.eiou.rabbitmq;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class RabbitMqApiDemo implements CommandLineRunner {
    private final AmqpAdmin amqpAdmin;
    private final RabbitTemplate rabbitTemplate;

    public RabbitMqApiDemo(AmqpAdmin amqpAdmin, RabbitTemplate rabbitTemplate) {
        this.amqpAdmin = amqpAdmin;
        this.rabbitTemplate = rabbitTemplate;
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(RabbitMqApiDemo.class, args);
        context.close();
    }

    @Override
    public void run(String... args) throws Exception {
        purgeDemoQueues();
        directSendAndReceive();
        listenerConsume();
        topicRouting();
        fanoutRouting();
        publisherConfirmAndReturn();
        manualAckAndNack();
        ttlAndDeadLetter();
        Thread.sleep(1_000);
    }

    private void purgeDemoQueues() {
        String[] queues = {
                RabbitMqNames.DIRECT_QUEUE,
                RabbitMqNames.LISTENER_QUEUE,
                RabbitMqNames.TOPIC_CREATED_QUEUE,
                RabbitMqNames.TOPIC_ALL_QUEUE,
                RabbitMqNames.FANOUT_FIRST_QUEUE,
                RabbitMqNames.FANOUT_SECOND_QUEUE,
                RabbitMqNames.CONFIRM_QUEUE,
                RabbitMqNames.MANUAL_ACK_QUEUE,
                RabbitMqNames.TTL_QUEUE,
                RabbitMqNames.DEAD_LETTER_QUEUE
        };
        for (String queue : queues) {
            amqpAdmin.purgeQueue(queue, true);
        }
    }

    private void directSendAndReceive() {
        rabbitTemplate.convertAndSend(
                RabbitMqNames.DIRECT_EXCHANGE,
                RabbitMqNames.DIRECT_ROUTING_KEY,
                "hello direct"
        );
        Object message = rabbitTemplate.receiveAndConvert(RabbitMqNames.DIRECT_QUEUE, 5_000);
        System.out.println("direct received: " + message);
    }

    private void listenerConsume() throws InterruptedException {
        rabbitTemplate.convertAndSend(
                RabbitMqNames.LISTENER_EXCHANGE,
                RabbitMqNames.LISTENER_ROUTING_KEY,
                "hello listener"
        );
        Thread.sleep(500);
    }

    private void topicRouting() throws InterruptedException {
        rabbitTemplate.convertAndSend(
                RabbitMqNames.TOPIC_EXCHANGE,
                "rabbitmq.topic.created",
                "topic created"
        );
        rabbitTemplate.convertAndSend(
                RabbitMqNames.TOPIC_EXCHANGE,
                "rabbitmq.topic.updated",
                "topic updated"
        );
        Thread.sleep(500);
    }

    private void fanoutRouting() throws InterruptedException {
        rabbitTemplate.convertAndSend(
                RabbitMqNames.FANOUT_EXCHANGE,
                "",
                "fanout broadcast"
        );
        Thread.sleep(500);
    }

    private void publisherConfirmAndReturn() throws InterruptedException {
        rabbitTemplate.convertAndSend(
                RabbitMqNames.CONFIRM_EXCHANGE,
                RabbitMqNames.CONFIRM_ROUTING_KEY,
                "confirm routed",
                new CorrelationData("confirm-routed")
        );
        rabbitTemplate.convertAndSend(
                RabbitMqNames.CONFIRM_EXCHANGE,
                "rabbitmq.confirm.missing",
                "confirm unroutable",
                new CorrelationData("confirm-unroutable")
        );
        Thread.sleep(500);
    }

    private void manualAckAndNack() throws InterruptedException {
        rabbitTemplate.convertAndSend(
                RabbitMqNames.MANUAL_ACK_EXCHANGE,
                RabbitMqNames.MANUAL_ACK_ROUTING_KEY,
                "manual ack"
        );
        rabbitTemplate.convertAndSend(
                RabbitMqNames.MANUAL_ACK_EXCHANGE,
                RabbitMqNames.MANUAL_ACK_ROUTING_KEY,
                "manual nack"
        );
        Thread.sleep(500);
    }

    private void ttlAndDeadLetter() throws InterruptedException {
        rabbitTemplate.convertAndSend(
                RabbitMqNames.TTL_EXCHANGE,
                RabbitMqNames.TTL_ROUTING_KEY,
                "ttl to dead letter"
        );
        Thread.sleep(2_500);
        Object message = rabbitTemplate.receiveAndConvert(RabbitMqNames.DEAD_LETTER_QUEUE, 5_000);
        System.out.println("dead letter received: " + message);
    }
}
