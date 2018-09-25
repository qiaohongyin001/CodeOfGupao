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

- NetworkConnector

主要是配置borker与broker之间的通信连接

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-activemq/img/activemq7.jpg?raw=true)

如上图所示，服务器S1和S2通过 NetworkConnector 相连，则生产者P1发送消息，消费者C3和C4都可以接收到，而生产者P3发送的消息，消费者C1和C2同样也可以接收到。 

#### 1 静态网络连接

1. 修改配置文件activemq.xml，增加如下内容

~~~java
<networkConnectors>
	<networkConnector uri="static://(tcp://192.168.11.140:61616,tcp://192.168.11.137:61616)" />
</networkConnectors>
~~~

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-activemq/img/activemq8.jpg?raw=true)

2. 两个broker通过一个static协议来进行网络链接。一个 consumer 连接到 brokerB的一个地址上，当producer 在brokerA上以相同的地址发送消息时，此时消息会被转移到 brokerB上，也就是说 brokerA 会转发消息到 brokerB上。

#### 2 丢失的消息

1. 一些 consumer 连接到broker1、消费broker2上的消息。消息先被 broker1 从broker2消费掉，然后转发给这些 consumers。假设，转发消息的时候 broker1 重启了，这些consumers 发现 brokers1 连接失败，通过 failover 连接到 broker2 。但是因为有一部分没有消费的消息被broker2已经分发到broker1上去了，这些消息就好像消失了。除非有消费者重新连接到broker1上来消费。
2. 从5.6版本开始，在 destinationPolicy 上新增了一个选项replayWhenNoConsumers 属性，这个属性可以用来解决当broker1上有需要转发的消息但是没有消费者时，把消息回流到它原始的broker。同时把enableAudit设置为false，为了防止消息回流后被当作重复消息而不被分发。
3. 通过如下配置，在activeMQ.xml中。 分别在两台服务器都配置。即可完成消息回流处理。

~~~java
<policyEntry queue=">" enableAudit="false">
	<networkBridgeFilterFactory>
		<conditionalNetworkBridgeFilterFactory replayWhenNoConsumers = "true" />
	</networkBridgeFilterFactory>
</policyEntry>
~~~

#### 3 动态网络连接

multilcast

networkConnector是一个高性能方案，并不是一个高可用方案

### 五 持久化消息和非持久化消息

#### 1 持久化消息和非持久化消息的发送策略

通过一下方式来配置：

~~~java
textMessage.setJMSDeliveryMode(DeliveryMode.PERSISTENT);
~~~

#### 2 消息的同步发送和异步发送

ActiveMQ支持同步、异步两种发送模式将消息发送到broker上。

同步发送过程中，发送者发送一条消息会阻塞直到broker反馈一个确认消息，表示消息已经被broker处理。这个机制提供了消息的安全性保障，但是由于是阻塞的操作，会影响到客户端消息发送的性能。

异步发送的过程中，发送者不需要等待broker提供反馈，所以性能相对较高。但是可能会出现消息丢失的情况。所以使用异步发送的前提是在某些情况下允许出现数据丢失的情况。

默认情况下，非持久化消息是异步发送的，持久化消息并且是在非事务模式下是同步发送的。

但是在开启事务的情况下，消息都是异步发送。由于异步发送的效率会比同步发送性能更高。所以在发送持久化消息的时候，尽量去开启事务会话。

除了持久化消息和非持久化消息的同步和异步特性以外，我们还可以通过以下几种方式来设置异步发送：

~~~java
ConnectionFactory connectionFactory=new ActiveMQConnectionFactory("tcp://192.168.11.153:61616?jms.useAsyncSend=true");2.((ActiveMQConnectionFactory) connectionFactory).setUseAsyncSend(true);3.((ActiveMQConnection)connection).setUseAsyncSend(true);
~~~

#### 3 持久化消息和非持久化消息的存储原理

正常情况下，非持久化消息是存储在内存中的，持久化消息是存储在文件中的。能够存储的最大消息数据在${ActiveMQ_HOME}/conf/activemq.xml文件中的systemUsage节点。

SystemUsage配置设置了一些系统内存和硬盘容量

~~~activemq.xml
<systemUsage>
	<systemUsage>
		该子标记设置整个 ActiveMQ 节点的“可用内存限制”。这个值不能超过 ActiveMQ 本身设置的最大内存大小，其中 percentOfJvmHeap 属性标识百分比，占用70%的堆内存 
		<memoryUsage>
			<memoryUsage percentOfJvmHeap="70" />
		</memoryUsage>
		该标记设置整个 ActiveMQ 节点，用于存储“持久化消息”的“可用磁盘空间”。该子标记的 limit 属性必须要进行设置
		<storeUsage>
			<storeUsage limit="100 gb"/>
		</storeUsage>
		<tempUsage>
			一旦 ActiveMQ 服务节点存储的消息达到了 memoryUsage 的限制，非持久化消息就会被转储到 temp store 区域，虽然我们说过非持久化消息不进行持久化存储，但是 ActiveMQ 为了防止“数据洪峰”出现时，非持久化消息大量堆积致使内存耗尽的情况出现，还是会将非持久化消息写入到磁盘的临时区域 - temp store。这个子标记就是为了设置这个 temp store 区域的“可用磁盘空间限制” 
			<tempUsage limit="50 gb"/>
		</tempUsage>
	</systemUsage>
</systemUsage>
~~~

从上面的配置我们需要get到一个结论，当非持久化消息堆积到一定程度的时候，也就是内存超过指定的设置阀值时，ActiveMQ会将内存中的非持久化消息写入到临时文件，以便腾出内存。但是它和持久化消息的区别是，重启之后，持久化消息会从文件中恢复，非持久化的临时文件会直接删除。

### 六 activemq 源码分析

#### 1 消息发送源码分析

详见pdf文档 有空的时候整理下

#### 2 消息消费源码分析

详见pdf文档 有空的时候整理下

### 七 持久化存储

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-activemq/img/activemq9.jpg?raw=true)

#### 1 持久化存储支持的类型

ActiveMQ支持多种不同的持久化方式，主要有以下几种，不过，无论使用哪种持久化方式，消息的存储逻辑都是一致的。
    1. KahaDB存储（默认存储方式）
    2. JDBC存储
    3. Memory存储
    4. LevelDB存储
    5. JDBC With ActiveMQ Journal

#### 2 KahaDB - 默认的存储方式

KahaDB是目前默认的存储方式,可用于任何场景,提高了性能和恢复能力。消息存储使用一个事务日志和仅仅用一个索引文件来存储它所有的地址。

KahaDB是一个专门针对消息持久化的解决方案,它对典型的消息使用模式进行了优化。在Kaha中,数据被追加到data logs中。当不再需要log文件中的数据的时候,log文件会被丢弃。

1. 配置方式

~~~java
<persistenceAdapter>    
	<kahaDB directory="${activemq.data}/kahadb"/>
</persistenceAdapter>
~~~

2. 存储原理
   1. 在data/kahadb这个目录下，会生成四个文件
   2. db.data 它是消息的索引文件，本质上是B-Tree（B树），使用B-Tree作为索引指向db-*.log里面存储的消息。*
   3. db.redo 用来进行消息恢复
   4. db-*.log 存储消息内容。新的数据以APPEND的方式追加到日志文件末尾。属于顺序写入，因此消息存储是比较快的。默认是32M，达到阀值会自动递增
   5. lock文件锁，表示当前获得kahadb读写权限的broker

#### 3 AMQ 基于文件的存储方式

1. 写入速度很快，容易恢复。
2. 文件默认大小是32M

#### 4 JDBC 基于数据库的存储（JDBC Store）

使用JDBC持久化方式，数据库会创建3个表：activemq_msgs，activemq_acks和activemq_lock。

1. ACTIVEMQ_ACKS ： 存储持久订阅的信息和最后一个持久订阅接收的消息ID
2. ACTIVEMQ_LOCK ： 锁表（用来做集群的时候，实现master选举的表）
3. ACTIVEMQ_MSGS ： 消息表，queue和topic都存在这个表中
4. 实现

~~~java
<persistenceAdapter>
    <jdbcPersistenceAdapter dataSource="#mysqlDataSource" createTablesOnStartup="true" />
</persistenceAdapter>
<bean id="mysqlDataSource" class="org.apache.commons.dbcp.BasicDataSource" destroy-method="close"> 
	<property name="driverClassName" value="com.mysql.jdbc.Driver"/>  
    <property name="url" value="jdbc:mysql://47.93.118.50:3306/activemq"/>  
    <property name="username" value="develop"/>  
    <property name="password" value="develop1234"/>  
</bean>
~~~

5. 把三个包放入 activemq 的 lib 包中

~~~java
lib中的3个包
~~~

#### 5 JDBC Message store with activeMQ journal

这种方式克服了JDBC Store的不足，JDBC每次消息过来，都需要去写库和读库。
ActiveMQ Journal，使用高速缓存写入技术，大大提高了性能。
当消费者的消费速度能够及时跟上生产者消息的生产速度时，journal文件能够大大减少需要写入到DB中的消息。
举个例子，生产者生产了1000条消息，这1000条消息会保存到journal文件，如果消费者的消费速度很快的情况下，在journal文件还没有同步到DB之前，消费者已经消费了90%的以上的消息，那么这个时候只需要同步剩余的10%的消息到DB。
如果消费者的消费速度很慢，这个时候journal文件可以使消息以批量方式写到DB。
配置如下：将原来的标签注释掉，添加如下标签

~~~java
<persistenceFactory>
    <journalPersistenceAdapterFactory dataSource="#Mysql-DS" dataDirectory="activemq-data"/>
</persistenceFactory>
~~~

在服务端循环发送消息。可以看到数据是延迟同步到数据库的

#### 6 Memory 基于内存的存储

基于内存的消息存储，内存消息存储主要是存储所有的持久化的消息在内存中。persistent=”false”,表示不设置持久化存储，直接存储到内存中。

~~~java
<beans>
    <broker brokerName="test-broker" persistent="false"
        xmlns="http://activemq.apache.org/schema/core">
        <transportConnectors>
            <transportConnector uri="tcp://localhost:61635"/>
        </transportConnectors> 
    </broker>
</beans>
~~~

#### 7 LevelDB

LevelDB持久化性能高于KahaDB，虽然目前默认的持久化方式仍然是KahaDB。并且，在ActiveMQ 5.9版本提供了基于LevelDB和Zookeeper的数据复制方式，用于Master-slave方式的首选数据复制方案。

不过，据ActiveMQ官网对LevelDB的表述：LevelDB官方建议使用以及不再支持，推荐使用的是KahaDB。

~~~java
<persistenceAdapter>
    <levelDBdirectory="activemq-data"/>
</persistenceAdapter>
~~~

















































