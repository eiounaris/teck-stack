package com.eiou.rabbitmq;

final class RabbitMqNames {
    static final String DIRECT_EXCHANGE = "rabbitmq.demo.durable.direct.exchange";
    static final String DIRECT_QUEUE = "rabbitmq.demo.durable.direct.queue";
    static final String DIRECT_ROUTING_KEY = "rabbitmq.demo.durable.direct";

    static final String LISTENER_EXCHANGE = "rabbitmq.demo.durable.listener.exchange";
    static final String LISTENER_QUEUE = "rabbitmq.demo.durable.listener.queue";
    static final String LISTENER_ROUTING_KEY = "rabbitmq.demo.durable.listener";

    static final String TOPIC_EXCHANGE = "rabbitmq.demo.durable.topic.exchange";
    static final String TOPIC_CREATED_QUEUE = "rabbitmq.demo.durable.topic.created.queue";
    static final String TOPIC_ALL_QUEUE = "rabbitmq.demo.durable.topic.all.queue";

    static final String FANOUT_EXCHANGE = "rabbitmq.demo.durable.fanout.exchange";
    static final String FANOUT_FIRST_QUEUE = "rabbitmq.demo.durable.fanout.first.queue";
    static final String FANOUT_SECOND_QUEUE = "rabbitmq.demo.durable.fanout.second.queue";

    static final String CONFIRM_EXCHANGE = "rabbitmq.demo.durable.confirm.exchange";
    static final String CONFIRM_QUEUE = "rabbitmq.demo.durable.confirm.queue";
    static final String CONFIRM_ROUTING_KEY = "rabbitmq.demo.durable.confirm";

    static final String MANUAL_ACK_EXCHANGE = "rabbitmq.demo.durable.manual-ack.exchange";
    static final String MANUAL_ACK_QUEUE = "rabbitmq.demo.durable.manual-ack.queue";
    static final String MANUAL_ACK_ROUTING_KEY = "rabbitmq.demo.durable.manual-ack";

    static final String TTL_EXCHANGE = "rabbitmq.demo.durable.ttl.exchange";
    static final String TTL_QUEUE = "rabbitmq.demo.durable.ttl.queue";
    static final String TTL_ROUTING_KEY = "rabbitmq.demo.durable.ttl";
    static final String DEAD_LETTER_EXCHANGE = "rabbitmq.demo.durable.dlx.exchange";
    static final String DEAD_LETTER_QUEUE = "rabbitmq.demo.durable.dlx.queue";
    static final String DEAD_LETTER_ROUTING_KEY = "rabbitmq.demo.durable.dlx";

    private RabbitMqNames() {
    }
}
