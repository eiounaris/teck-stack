# RabbitMQ API 使用

本模块演示 RabbitMQ 的基础依赖、基础配置和最直接 API 调用。示例默认连接仓库内 `docker/rabbitmq` 提供的本地 RabbitMQ 服务，不引入业务模型、不编写测试、不做复杂封装。

## 启动 RabbitMQ

在仓库根目录执行：

```bash
docker compose --env-file docker/rabbitmq/.env -f docker/rabbitmq/docker-compose.yml up -d
```

默认连接信息：

- Host：`localhost`
- AMQP Port：`5672`
- Management Port：`15672`
- Username：`admin`
- Password：`admin`

管理页面：

```text
http://localhost:15672
```

## 构建

在仓库根目录执行：

```bash
mvn -pl java-rabbitmq package
```

## 运行示例

```bash
mvn -pl java-rabbitmq exec:java
```

如需使用其他 RabbitMQ 地址，修改 `src/main/resources/application.yml`：

```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: admin
    password: admin
    publisher-confirm-type: correlated
    publisher-returns: true
    template:
      mandatory: true
```

## 依赖

- `org.springframework.boot:spring-boot-starter-amqp`：提供 Spring AMQP、RabbitMQ Java Client、`RabbitTemplate`、`@RabbitListener` 和自动配置。

## Bean 声明

本模块使用 Spring Bean 声明队列、交换机和绑定，应用启动后由 Spring AMQP 自动声明到 RabbitMQ。

```java
@Bean
Queue directQueue() {
    return new Queue("rabbitmq.demo.durable.direct.queue", true);
}
```

第二个参数 `true` 表示持久化队列。仓库内 RabbitMQ 4.3 镜像默认不再允许普通非持久化非排他队列，所以本模块统一使用持久化声明。

## DirectExchange

```java
@Bean
DirectExchange directExchange() {
    return new DirectExchange("rabbitmq.demo.durable.direct.exchange", true, false);
}
```

第二个参数 `true` 表示持久化交换机，第三个参数 `false` 表示不自动删除。`DirectExchange` 根据 routing key 精确路由。

## Binding

```java
@Bean
Binding directBinding() {
    return BindingBuilder.bind(directQueue())
            .to(directExchange())
            .with("rabbitmq.demo.durable.direct");
}
```

## 发送消息

```java
rabbitTemplate.convertAndSend(
        "rabbitmq.demo.durable.direct.exchange",
        "rabbitmq.demo.durable.direct",
        "hello direct"
);
```

## 同步接收

```java
Object message = rabbitTemplate.receiveAndConvert("rabbitmq.demo.durable.direct.queue", 5_000);
```

`receiveAndConvert` 会从队列同步拉取消息，并把消息体转换为 Java 对象。

## 异步消费

`@RabbitListener` 用于异步监听队列：

```java
@RabbitListener(queues = RabbitMqNames.LISTENER_QUEUE)
public void listener(String message) {
    System.out.println("listener received: " + message);
}
```

发送到监听队列：

```java
rabbitTemplate.convertAndSend(
        RabbitMqNames.LISTENER_EXCHANGE,
        RabbitMqNames.LISTENER_ROUTING_KEY,
        "hello listener"
);
```

## TopicExchange

`TopicExchange` 支持通配符路由：

```java
@Bean
Binding topicAllBinding() {
    return BindingBuilder.bind(topicAllQueue())
            .to(topicExchange())
            .with("rabbitmq.topic.*");
}
```

示例发送：

```java
rabbitTemplate.convertAndSend(
        RabbitMqNames.TOPIC_EXCHANGE,
        "rabbitmq.topic.created",
        "topic created"
);
```

## FanoutExchange

`FanoutExchange` 忽略 routing key，把消息广播给所有绑定队列：

```java
@Bean
Binding fanoutFirstBinding() {
    return BindingBuilder.bind(fanoutFirstQueue()).to(fanoutExchange());
}
```

示例发送：

```java
rabbitTemplate.convertAndSend(RabbitMqNames.FANOUT_EXCHANGE, "", "fanout broadcast");
```

## Publisher Confirm / Return

配置：

```yaml
spring:
  rabbitmq:
    publisher-confirm-type: correlated
    publisher-returns: true
    template:
      mandatory: true
```

回调：

```java
rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
    String id = correlationData == null ? "" : correlationData.getId();
    System.out.println("confirm: id=" + id + ", ack=" + ack + ", cause=" + cause);
});

rabbitTemplate.setReturnsCallback(returned -> System.out.println(
        "return: replyCode=" + returned.getReplyCode()
                + ", replyText=" + returned.getReplyText()
                + ", exchange=" + returned.getExchange()
                + ", routingKey=" + returned.getRoutingKey()
));
```

`confirm` 表示消息是否到达 exchange，`return` 表示 mandatory 消息无法路由到 queue。

## 手动 ACK / NACK

手动确认需要配置监听容器：

```java
factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
```

监听方法：

```java
@RabbitListener(
        queues = RabbitMqNames.MANUAL_ACK_QUEUE,
        containerFactory = "manualAckListenerContainerFactory"
)
public void manualAck(String message, Channel channel,
                      @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
    if (message.contains("nack")) {
        channel.basicNack(deliveryTag, false, false);
        return;
    }
    channel.basicAck(deliveryTag, false);
}
```

## TTL 和死信队列

TTL 队列设置过期时间和 dead-letter 路由：

```java
QueueBuilder.durable(RabbitMqNames.TTL_QUEUE)
        .ttl(2_000)
        .deadLetterExchange(RabbitMqNames.DEAD_LETTER_EXCHANGE)
        .deadLetterRoutingKey(RabbitMqNames.DEAD_LETTER_ROUTING_KEY)
        .build();
```

消息在 TTL 队列中 2 秒未被消费后，会进入死信队列：

```java
rabbitTemplate.convertAndSend(
        RabbitMqNames.TTL_EXCHANGE,
        RabbitMqNames.TTL_ROUTING_KEY,
        "ttl to dead letter"
);
Object message = rabbitTemplate.receiveAndConvert(RabbitMqNames.DEAD_LETTER_QUEUE, 5_000);
```

## 示例文件

查看：

- `src/main/java/com/eiou/rabbitmq/RabbitMqApiDemo.java`
- `src/main/java/com/eiou/rabbitmq/RabbitMqConfig.java`
- `src/main/java/com/eiou/rabbitmq/RabbitMqListeners.java`
- `src/main/resources/application.yml`
