## 分布式消息通讯 - activemq

**官方网站：**<http://activemq.apache.org/>

### 一 基本介绍

#### 1 简介

ActiveMQ是Apache开源基金会研发的消息中间件。是完全支持JMS1.1和J2EE1.4规范的 JMS provider实现。

ActiveMQ主要应用在分布式系统架构中，帮助构建高可用、高性能、可伸缩的企业级面向消息服务的系统。

#### 2 应用场景

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-activemq/img/activemq1.jpg?raw=true)

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-activemq/img/activemq2.jpg?raw=true)

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-activemq/img/activeme3.jpg?raw=true)

#### 3 安装

1. 下载activeMQ安装包

2. tar -zxvf ***.tar.gz

3. sh /bin/activemq start 启动activeMQ服务

4. 校验是否启动成功

5. 1. ip:8161 admin=admin

#### 4 什么是MOM

<http://activemq.apache.org/mom.html>

面向消息的中间件，使用消息传送提供者来协调消息传输操作。MOM需要提供API和管理工具。客户端调用API。把消息发送到消息传送提供者指定的目的地。在消息发送之后，客户端会继续执行其他的工作。并且在接收方收到这个消息确认之前。提供者一直保留该消息。

#### 5 activemq 依赖包

~~~java
<dependency>
  <groupId>org.apache.activemq</groupId>
  <artifactId>activemq-all</artifactId>
  <version>5.15.0</version>
</dependency>
~~~

### 二 JMS

#### 1 基本概念

1. JMS概念

2. 1. java消息服务（Java Message Service）是java平台中关于面向消息中间件的API，用于再两个应用之间或者分布式系统中发送消息，进行异步通信。
   2. JMS是一个与具体平台无关的API，绝大多数MOM（Message Oriented Middleware 以消息为目的中间件）提供商都对JMS提供了支持。

3. 其他开源的JMS提供商

4. 1. JbossMQ(jboss4)、jboss messaging(jboss5)、joram、ubermq、mantamq、openjms

5. JMS模型

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-activemq/img/activemq4.jpg?raw=true)

#### 2 消息传递域

1. 点对点（point to point P2P）

2. 1. 每一个消息只能有一个消费者
   2. 消息的生产者和消费者之间没有时间上的相关性。无论消费者在生产者发送消息的时候是否处于运行状态，都可以提取消息。

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-activemq/img/activemq5.jpg?raw=true)

2. 发布订阅（pub/sub）

1. 1. 每个消息有多个消费者
   2. 消息的生产者和消费者之间存在时间上的相关性，订阅一个主题的消费者只能消费自它订阅之后的消息。
   3. JMS规范允许提供客户端创建持久订阅。

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-activemq/img/activemq6.jpg?raw=true)

#### 3 JMS API

1. ConnectionFactory：连接工厂
2. Connection：封装客户端与 JMS provider 之间的一个虚拟的链接
3. Session：生产和消费的一个单线程上下文，用于创建
4. producer/consumer/message/queue
5. Destination：消息发送或消息接收的目的地
6. MessageProducer/consumer：消息生产者/消费者

#### 4 消息的组成

1. 消息头

2. 1. 包含消息的识别信息和路由信息

3. 消息体

4. 1. TextMessage
   2. MapMessage
   3. BytesMessage
   4. StreamMessage 输入输出流
   5. ObjectMessage 可序列化对象

5. 属性

#### 5 JMS的可靠性机制

JMS消息被确认后，才会认为是被成功消费。消息的消费包含三个阶段：客户端接收消息、客户端处理消息、消息被确认。

1. 事务性会话

2. 1. Session session = connection.createSession(true,Session.AUTO_ACKNOWLEDGE);
   2. 设置为true的时候，消息会在session.commit以后自动签收

3. 非事务性会话

4. 1. Session session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);

#### 6 创建会话时的应答模式

1. AUTO_ACKNOWLEDGE：自动签收
   1. 当客户端成功从recive方法返回以后
   2. 给[MessageListener.onMessage]方法成功返回以后，会话会自动确认该消息。

~~~java
MessageConsumer messageConsumer = session.createConsumer(destination);
messageConsumer.setMessageListener(new MessageListener() {
    @Override
    public void onMessage(Message message) {
        System.out.println(message);
   	}
});
~~~

2. CLIENT_ACKNOWLEDGE
   1. 客户端通过调用消息的textMessage.acknowledge();确认消息。
   2. 在这种模式下，如果一个消费消费者一共是10条消息，然后在第5个消息通过textMessage.acknowledge()，那么之前的所有消息都会被消确认。

~~~java
for (int i = 0 ;i<10;i++){
    TextMessage textMessage = (TextMessage) messageConsumer.receive();
    System.out.println(textMessage.getText());
    if (i==5){
    	textMessage.acknowledge();
  	}
}
~~~

3. DUPS_OK_ACKNOWLEDGE：延时确认

#### 7 本地事务

在一个JMS客户端，可以使用本地事务来组合消息的发送和接收。JMS Session 接口提供了commit 和 rollback 方法。

JMS provider 会缓存每个生产者当前生产的所有消息，直到 commit 或者 rollback 。commit 操作将会导致事务中所有的消息被持久存储；rollback 意味着JMS provider 将会清除此事务下所有的消息记录。再事务未提交之前，消息是不会被持久化存储的，也不会被消费者消费。

事务提交意味着生产的所有消息都被发送。消费的所有消息都被确认。

事务回滚意味着生产的所有消息被销毁，消费的所有消息被恢复，也就是下次仍然能够接收到发送端的消息，除非消息已经过期了。

#### 8 JMS(pub/sub)模型

1. 订阅可以分为非持久订阅和持久订阅。
2. 当所有的消息必须接收的时候，则需要用到持久订阅。反之，则用非持久订阅。

#### 9 JMS(P2P)模型

1. 如果session关闭时，有一些消息已经收到，但还没有被签收，那么当消费者下次连接到相同的队列时，消息还会被签收。
2. 如果用户在receive方法中设定了消息选择条件，那么不符合条件的消息会留在队列中不会被接收。
3. 队列可以长久保存消息直到消息被消费者签收。消费者不需要担心因为消息丢失而时刻与jms provider保持连接状态

**activemq是基于broker启动的实例**

#### 10 实例演示

详见演示代码



### 三 传输协议

Connector：ActiveMQ提供的，用来实现连接通讯的功能。包括：client-tobroker、broker-to-broker。 ActiveMQ允许客户端使用多种协议来连接。

配置Transport Connector的文件在activeMQ安装目录的conf/activemq.xml中的<transportConnectors>标签之内。

~~~activemq.xml
 <transportConnectors>
            <!-- DOS protection, limit concurrent connections to 1000 and frame size to 100MB -->
            <transportConnector name="openwire" uri="tcp://0.0.0.0:61616?maximumConnections=1000&amp;wireFormat.maxFrameSize=104857600"/>
            <transportConnector name="amqp" uri="amqp://0.0.0.0:5672?maximumConnections=1000&amp;wireFormat.maxFrameSize=104857600"/>
            <transportConnector name="stomp" uri="stomp://0.0.0.0:61613?maximumConnections=1000&amp;wireFormat.maxFrameSize=104857600"/>
            <transportConnector name="mqtt" uri="mqtt://0.0.0.0:1883?maximumConnections=1000&amp;wireFormat.maxFrameSize=104857600"/>
            <transportConnector name="ws" uri="ws://0.0.0.0:61614?maximumConnections=1000&amp;wireFormat.maxFrameSize=104857600"/>
        </transportConnectors>

~~~

ActiveMQ支持的client-broker通讯协议有：TCP、NIO、UDP、SSL、Http(s)、VM。

所有关于Transport协议的可配置参数，可以参见：<http://activemq.apache.org/configuring-version-5-transports.html>

#### 1 Transmission Control Protocol (TCP)

1. 这是默认的Broker配置，TCP的Client监听端口是61616。
2. 在网络传输数据前，必须要序列化数据，消息是通过一个叫wire protocol的来序列化成字节流。默认情况下，ActiveMQ把wire protocol叫做OpenWire，它的目的是促使网络上的效率和数据快速交互。
3. TCP连接的URI形式：tcp://hostname:port?key=value&key=value
4. TCP传输的优点：
   1. TCP协议传输可靠性高，稳定性强
   2. 高效性：字节流方式传递，效率很高
   3. 有效性、可用性：应用广泛，支持任何平台

~~~java
<transportConnector name="openwire" uri="tcp://0.0.0.0:61616?	
    maximumConnections=1000&wireFormat.maxFrameSize=104857600"/>
~~~

#### 2 New I/O API Protocol（NIO）

1. NIO协议和TCP协议类似，但NIO更侧重于底层的访问操作。它允许开发人员对同一资源可有更多的client调用和服务端有更多的负载。
2. 适合使用NIO协议的场景
   1. 可能有大量的Client去链接到Broker上，一般情况下，大量的Client去链接Broker是被操作系统的线程数所限制的。因此，NIO的实现比TCP需要更少的线程去运行，所以建议使用NIO协议。
   2. 可能对于Broker有一个很迟钝的网络传输，NIO比TCP提供更好的性能。
3. NIO连接的URI形式：nio://hostname:port?key=value

~~~java
<transportConnector name="nio" uri="nio://localhost:61618?trace=true" />
~~~

#### 3 User Datagram Protocol（UDP)

1. UDP和TCP的区别
   1. TCP是一个原始流的传递协议，意味着数据包是有保证的，换句话说，数据包是不会被复制和丢失的。UDP，另一方面，它是不会保证数据包的传递的。
   2. TCP也是一个稳定可靠的数据包传递协议，意味着数据在传递的过程中不会被丢失。这样确保了在发送和接收之间能够可靠的传递。相反，UDP仅仅是一个链接协议，所以它没有可靠性之说。

2. 从上面可以得出：TCP是被用在稳定可靠的场景中使用的；UDP通常用在快速数据传递和不怕数据丢失的场景中，还有ActiveMQ通过防火墙时，只能用UDP
3. UDP连接的URI形式：udp://hostname:port?key=value

~~~java
<transportConnector name="udp" uri="udp://localhost:61618?trace=true" />
~~~

#### 4 Secure Sockets Layer Protocol (SSL)

1. 连接的URI形式：ssl://hostname:port?key=value

~~~java
<transportConnector name="ssl" uri="ssl://localhost:61617?trace=true"/>
~~~

#### 5 Hypertext Transfer Protocol (HTTP/HTTPS)

1. 像web和email等服务需要通过防火墙来访问的，Http可以使用这种场合
2. 连接的URI形式：http://hostname:port?key=value或者<https://hostname:port?key=value>

~~~java
<transportConnector name="http" uri="http://localhost:8080?trace=true" />
~~~

#### 6 VM Protocol（VM）

1. VM transport允许在VM内部通信，从而避免了网络传输的开销。这时候采用的连接不是socket连接，而是直接的方法调用。
2. 第一个创建VM连接的客户会启动一个embed VM broker，接下来所有使用相同的 broker name的VM连接都会使用这个broker。当这个broker上所有的连接都关闭的时候，这个broker也会自动关闭。
3. 连接的URI形式：vm://brokerName?key=value
4. Java中嵌入的方式： vm:broker:(tcp://localhost:6000)?brokerName=embeddedbroker&persistent=false，定义了一个嵌入的broker名称为embededbroker以及配置了一个 tcptransprotconnector在监听端口6000上
5. 使用一个加载一个配置文件来启动broker

~~~java
vm://localhost?brokerConfig=xbean:activemq.xml
~~~

### 四 网络连接

activeMQ如果要实现扩展性和高可用性的要求的话，就需要利用到网络连接模式。

#### 1 NetworkConnector

主要是配置borker与broker之间的通信连接











