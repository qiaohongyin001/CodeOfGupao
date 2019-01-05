## 工作模型与java编程

### 一 环境说明

- 操作系统：CentOS 7
- JDK:1.8
- Erlang：19.0.4 或 [最新版](http://www.rabbitmq.com/releases/erlang/)
- RabbitMQ：3.6.12 或 [最新版](http://www.rabbitmq.com/releases/rabbitmq-server/)
- [版本对应关系](http://www.rabbitmq.com/which-erlang.html)

### 二 典型应用场景

- 跨系统的异步通信。例如：人民银行二代支付系统，使用重量级消息队列 IBM MQ，异步，解耦，削峰都有体现。
- 应用内的同步变成异步。例如：秒杀-自己发送给自己
- 基于Pub/Sub模型实现的事件驱动。例如：放款失败通知、提货通知、购买碎屏保、系统间同步数据、摒弃ELT(比如全量同步商户数据)、摒弃API(比如定时增量获取用户、获取产品，变成增量广播)。
- 利用RabbitMQ实现事务的最终一致性

### 三 基本介绍

#### 1 AMQP 协议

AMQP，即Advanced Message Queuing Protocol，一个提供统一消息服务的应用层标准高级消息队列协议，是应用层协议的一个开放标准，为面向消息的中间件设计。基于此协议的客户端与消息中间件可传递消息，并不受客户端/中间件同产品、不同的开发语言等条件的限制。

AMQP的实现有：RabbitMQ、OpenAMQ、Apache Qpid、Redhat Enterprise MRG、AMQP Infrastructure、ØMQ、Zyre等。

#### 2 RabbitMq的特性

RabbitMQ 使用 Erlang 语言编写，使用 Mnesia 数据库存储消息。

1. 可靠性(Reliability)：RabbitMQ 使用一些机制来保证可靠性，如持久化、传输确认、发布确认。
2. 灵活的路由(Flexible Routing)：在消息进入队列之前，通过 Exchange 来路由消息的。对于典型的路由功能，RabbitMQ 已经提供了一些内置的 Exchange 来实现。针对更复杂的路由功能，可以将多个 Exchange 绑定在
   一起，也通过插件机制实现自己的 Exchange 。
3. 消息集群(Clustering)：多个 RabbitMQ 服务器可以组成一个集群，形成一个逻辑 Broker 。
4. 高可用(Highly Available Queues)：队列可以在集群中的机器上进行镜像，使得在部分节点出问题的情况下队列仍然可用。
5. 多种协议(Multi-protocol)：RabbitMQ 支持多种消息队列协议，比如 AMQP、STOMP、MQTT 等等。
6. 多语言客户端(Many Clients)：RabbitMQ 几乎支持所有常用语言，比如 Java、.NET、Ruby、PHP、C#、JavaScript 等等。
7. 管理界面(Management UI)：RabbitMQ 提供了一个易用的用户界面，使得用户可以监控和管理消息、集群
   中的节点。
8. 插件机制(Plugin System)：RabbitMQ提供了许多插件，以实现从多方面扩展，当然也可以编写自己的插件。

#### 3 工作模型

![](1)

| 概念        | 解释                                                         |
| ----------- | ------------------------------------------------------------ |
| Broker      | 即RabbitMQ的实体服务器。提供一种传输服务，维护一条从生产者到消费者的传输线路，<br/>保证消息数据能按照指定的方式传输。 |
| Exchange    | 消息交换机。指定消息按照什么规则路由到哪个队列Queue。        |
| Queue       | 消息队列。消息的载体，每条消息都会被投送到一个或多个队列中。 |
| Binding     | 绑定。作用就是将Exchange和Queue按照某种路由规则绑定起来。    |
| Routing Key | 路由关键字。Exchange根据Routing Key进行消息投递。定义绑定时指定的关键字称为<br/>Binding Key。 |
| Vhost       | 虚拟主机。一个Broker可以有多个虚拟主机，用作不同用户的权限分离。一个虚拟主机持有一组Exchange、Queue和Binding。 |
| Producer    | 消息生产者。主要将消息投递到对应的Exchange上面。一般是独立的程序。 |
| Consumer    | 消息消费者。消息的接收者，一般是独立的程序。                 |
| Connection  | Producer 和 Consumer 与Broker之间的TCP长连接。               |
| Channel     | 消息通道，也称信道。在客户端的每个连接里可以建立多个Channel，每个Channel代表一个会话任务。在RabbitMQ Java Client API中，channel上定义了大量的编程接口。 |

#### 4 三种主要的交换机

- Direct Exchange 直连交换机

定义：直连类型的交换机与一个队列绑定时，需要指定一个明确的binding key。

路由规则：发送消息到直连类型的交换机时，只有routing key 跟 binding key 完全匹配时，绑定的队列才能收到消息。 

~~~java
// 只有队列1能收到消息
channel.basicPublish("MY_DIRECT_EXCHANGE", "key1", null, msg.getBytes());
~~~

![](2)

- Topic Exchange 主题交换机

定义：主题类型的交换机与一个队列绑定时，可以指定按模式匹配的routing key。

通配符有两个，*代表匹配一个单词。#代表匹配零个或者多个单词。单词与单词之间用 . 隔开。

路由规则：发送消息到主题类型的交换机时，routing key符合binding key的模式时，绑定的队列才能收到消息。

~~~java
// 只有队列1能收到消息
channel.basicPublish("MY_TOPIC_EXCHANGE", "sh.abc", null, msg.getBytes());
// 队列2和队列3能收到消息
channel.basicPublish("MY_TOPIC_EXCHANGE", "bj.book", null, msg.getBytes());
// 只有队列4能收到消息
channel.basicPublish("MY_TOPIC_EXCHANGE", "abc.def.food", null, msg.getBytes());
 
~~~

![](3)

- Fanout Exchange 广播交换机

定义：广播类型的交换机与一个队列绑定时，不需要指定binding key。

路由规则：当消息发送到广播类型的交换机时，不需要指定routing key，所有与之绑定的队列都能收到消息。

~~~java
// 3个队列都会收到消息
channel.basicPublish("MY_FANOUT_EXCHANGE", "", null, msg.getBytes());
~~~

### 四 Java Api 编程

#### 1 创建Maven工程，引入依赖

~~~xml
<dependency>
    <groupId>com.rabbitmq</groupId>
    <artifactId>amqp-client</artifactId>
    <version>4.1.0</version>
</dependency>
~~~

#### 2 生产者

~~~java
package com.wolfman.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;
/**
 * 生产者
 */
public class Producer {
  private final static String QUEUE_NAME = "origin_queue";
  public static void main(String[] args) throws IOException, TimeoutException {
    ConnectionFactory factory = new ConnectionFactory();
    //连接ip
    factory.setHost("39.107.31.208");
    //连接端口
    factory.setPort(5672);
    //虚拟机
    factory.setVirtualHost("/");
    //用户
    factory.setUsername("guest");
    factory.setPassword("guest");

    //建立连接
    Connection conn = factory.newConnection();
    //创建消息通道
    Channel channel = conn.createChannel();

    String msg = "Hello world, Rabbit MQ aaaaa";

    // 声明队列
    // String queue, boolean durable, boolean exclusive, boolean autoDelete,Map<String, Object> arguments
    channel.queueDeclare(QUEUE_NAME,false,false,false,null);

    // 发送消息(发送到默认交换机AMQP Default，Direct)
    // 如果有一个队列名称跟Routing Key相等，那么消息会路由到这个队列
    // String exchange, String routingKey, BasicProperties props, byte[] body
    channel.basicPublish("", QUEUE_NAME, null, msg.getBytes());
    channel.close();
    conn.close();
  }
}
~~~

#### 3 消费者

~~~java
package com.wolfman.rabbitmq;
import com.rabbitmq.client.*;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
public class Consumer {
  private final static String QUEUE_NAME = "origin_queue";
  public static void main(String[] args) throws IOException, TimeoutException {
    ConnectionFactory factory = new ConnectionFactory();
    //连接ip
    factory.setHost("39.107.31.208");
    //连接端口
    factory.setPort(5672);
    //虚拟机
    factory.setVirtualHost("/");
    //用户
    factory.setUsername("guest");
    factory.setPassword("guest");

    //建立连接
    Connection conn = factory.newConnection();
    //创建消息通道
    Channel channel = conn.createChannel();

    String msg = "Hello world, Rabbit MQ";

    // 声明队列
    // String queue, boolean durable, boolean exclusive, boolean autoDelete,Map<String, Object> arguments
    channel.queueDeclare(QUEUE_NAME,false,false,false,null);
    System.out.println(" Waiting for message....");

    // 创建消费者
    DefaultConsumer consumer = new DefaultConsumer(channel) {
      @Override
      public void handleDelivery(String consumerTag, Envelope envelope,
                                 AMQP.BasicProperties properties, byte[] body) throws IOException {
        String msg = new String(body, "UTF-8");
        System.out.println("Received message : '" + msg + "'");
      }
    };
    // 开始获取消息
    // String queue, boolean autoAck, Consumer callback
    channel.basicConsume(QUEUE_NAME, true, consumer);
  }
}
~~~

#### 4 参数说明

- 声明交换机参数

String type：交换机的类型，direct, topic, fanout中的一种。

boolean durable：是否持久化，代表交换机在服务器重启后是否还存在。

- 声明队列的参数

boolean durable：是否持久化，代表队列在服务器重启后是否还存在。 

boolean exclusive：是否排他性队列。排他性队列只能在声明它的Connection中使用，连接断开时自动删除。 

boolean autoDelete：是否自动删除。如果为true，至少有一个消费者连接到这个队列，之后所有与这个队列连接 的消费者都断开时，队列会自动删除。 

Map<String, Object> arguments：队列的其他属性，例如x-message-ttl、x-expires、x-max-length、x-max- length-bytes、x-dead-letter-exchange、x-dead-letter-routing-key、x-max-priority。 

- 消息属性BasicProperties

消息的全部属性有14个，以下列举了一些主要的参数:

| 参数                       | 释义                          |
| -------------------------- | ----------------------------- |
| Map<String,Object> headers | 消息的其他自定义参数          |
| Integer deliveryMode       | 2持久化，其他:瞬态            |
| Integer priority           | 消息的优先级                  |
| String correlationId       | 关联ID，方便RPC相应与请求关联 |
| String replyTo             | 回调队列                      |
| String expiration          | TTL，消息过期时间，单位毫秒   |























