package com.eiou.rabbitmq;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RabbitMqListeners {
    @RabbitListener(queues = RabbitMqNames.LISTENER_QUEUE)
    public void listener(String message) {
        System.out.println("listener received: " + message);
    }

    @RabbitListener(queues = RabbitMqNames.TOPIC_CREATED_QUEUE)
    public void topicCreated(String message) {
        System.out.println("topic created queue received: " + message);
    }

    @RabbitListener(queues = RabbitMqNames.TOPIC_ALL_QUEUE)
    public void topicAll(String message) {
        System.out.println("topic all queue received: " + message);
    }

    @RabbitListener(queues = RabbitMqNames.FANOUT_FIRST_QUEUE)
    public void fanoutFirst(String message) {
        System.out.println("fanout first queue received: " + message);
    }

    @RabbitListener(queues = RabbitMqNames.FANOUT_SECOND_QUEUE)
    public void fanoutSecond(String message) {
        System.out.println("fanout second queue received: " + message);
    }

    @RabbitListener(
            queues = RabbitMqNames.MANUAL_ACK_QUEUE,
            containerFactory = "manualAckListenerContainerFactory"
    )
    public void manualAck(String message,
                          Channel channel,
                          @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        if (message.contains("nack")) {
            channel.basicNack(deliveryTag, false, false);
            System.out.println("manual nack: " + message);
            return;
        }

        channel.basicAck(deliveryTag, false);
        System.out.println("manual ack: " + message);
    }
}
