## 分布式消息通讯 - kafka

**官方网站：**<http://kafka.apache.org/>

### 一 简介以及架构设计

#### 1 kafka 简介

kafka是一款分布式消息发布和订阅系统，具有高性能、高吞吐量的特点而被广泛应于大数据传输场景。它是由Linkedln公司开发，使用scala语言编写，之后成为apache基金会的一个顶级项目。

#### 2 kafka 架构

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-kafka/img/kafka1.jpg?raw=true)

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-kafka/img/kafka2.jpg?raw=true)

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-kafka/img/kafka3.jpg?raw=true)

#### 3 kafka 典型应用场景

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-kafka/img/kafka4.jpg?raw=true)

### 二 安装及基本操作

**注意：**kafka的版本差异需要注意

linux下载包： wget https://archive.apache.org/dist/kafka/1.0.0/kafka_2.12-1.0.0.tgz

#### 1 安装

1. 解压安装包

2. 1. tar -zxvf kafka_2.12.-1.0.0.tgz

3. 进入到config目录下修改server.properties

4. 1. broker.id id要唯一

   2. kafka间通讯配置

   3. 1. listeners=PLAINTEXT://192.168.11.140:9092

   4. zookeeper.connect

5. 启动&停止

6. 1. 启动：sh kafka-server-start.sh -daemon ../config/server.properties
   2. 停止：sh kafka-server-stop.sh

7. zookeeper上注册的节点信息

8. 1. cluster, controller, controller_epoch, brokers, zookeeper, admin,isr_change_notification, consumers, latest_producer_id_block, config

~~~java
1.controller：控制节点，可以看到集群谁是leader
[zk: localhost:2181(CONNECTED) 1] get /controller
{"version":1,"brokerid":1,"timestamp":"1530502758005"}
cZxid = 0x300000ab4
ctime = Mon Jul 02 11:39:18 CST 2018
mZxid = 0x300000ab4
mtime = Mon Jul 02 11:39:18 CST 2018
pZxid = 0x300000ab4
cversion = 0
dataVersion = 0
aclVersion = 0
ephemeralOwner = 0x263b65d6ab10002
dataLength = 54
numChildren = 0

2.brokers：kafka集群的broker信息
[zk: localhost:2181(CONNECTED) 2] ls /brokers
[ids, topics, seqid]

3.consumers：消费者信息
ids/owners/offsets
~~~

#### 2 基本操作

官方文档地址：<http://kafka.apache.org/documentation/#quickstart>

1. kafka中创建一个topic

~~~java
./kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic mhxy-wdd

[root@iZ2zeeufalzdvyrne9oa4sZ bin]# ./kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic bfgz
Created topic "bfgz".
~~~

2. kafka中查询topic列表

~~~java
./kafka-topics.sh --list --zookeeper localhost:2181
    
[root@iZ2zeeufalzdvyrne9oa4sZ bin]# ./kafka-topics.sh --list --zookeeper localhost:2181
__consumer_offsets
bfgz
car
mhxy
mhxy-wdd
sleuth
test
~~~

3. zookeeper中查询topic节点

~~~java
[zk: localhost:2181(CONNECTED) 23] ls /brokers/topics
[sleuth, test, car, bfgz, mhxy-wdd, __consumer_offsets, mhxy]
~~~

4. kafka中启动一个消费者

~~~java
./kafka-console-consumer.sh --bootstrap-server 39.107.31.208:9092 --topic bfgz --from-beginning

[root@iZ2zeeufalzdvyrne9oa4sZ bin]# ./kafka-console-consumer.sh --bootstrap-server 39.107.31.208:9092 --topic bfgz --from-beginning
jintian
~~~

5. kafka中发送消息

~~~java
./kafka-console-producer.sh --broker-list 39.107.31.208:9092 --topic mhxy-wdd

[root@iZ2zeeufalzdvyrne9oa4sZ bin]# ./kafka-console-producer.sh --broker-list 39.107.31.208:9092 --topic bfgz
>jintian
>aaa
~~~

**kafka中发送消息和启动消费者，通讯配置必须与配置一样**

### 三 java api的使用

#### 1 实现简单通讯

详细代码见项目中代码

#### 2 发送端的可选配置信息分析

1. acks - 表示 producer 发送消息到 broker 上以后的确认值。有三个可选
   1. 0：表示 producer 不需要等待 broker 的消息确认。这个选项时延最小但同时风险最大（因为当 server 宕机时，数据将会丢失）。
   2. 1：表示 producer 只需要获得 kafka 集群中的 leader 节点确认即可，这个选择时延较小同时确保了 leader 节点确认接收成功。
   3. all(-1)：需要 ISR 中所有的 Replica 给予接收确认，速度最慢，安全性最高，但是由于 ISR 可能会缩小到仅包含一个 Replica，所以设置参数为 all 并不能一定避免数据丢失。
2. batch.size
   1. 生产者发送多个消息到 broker 上的同一个分区时，为了减少网络请求带来的性能开销，通过批量的方式来提交消息，可以通过这个参数来控制批量提交的字节数大小，默认大小是 16384byte,也就是 16kb，意味着当一批消息大小达到指定的 batch.size 的时候会统一发送。
3. linger.ms
   1. Producer 默认会把两次发送时间间隔内收集到的所有 Requests 进行一次聚合然后再发送，以此提高吞吐量，而 linger.ms 就是为每次发送到 broker 的请求增加一些 delay，以此来聚合更多的 Message 请求。 这个有点想 TCP 里面的Nagle 算法，在 TCP 协议的传输中，为了减少大量小数据包的发送，采用了Nagle 算法，也就是基于小包的等-停协议。
   2. batch.size 和 linger.ms 这两个参数是 kafka 性能优化的关键参数，很多同学会发现 batch.size 和 linger.ms 这两者的作用是一样的，如果两个都配置了，那么怎么工作的呢？实际上，当二者都配置的时候，只要满足其中一个要求，就会发送请求到 broker 上。
      4.max.request.size
4. max.request.size
   1. 设置请求的数据的最大字节数，为了防止发生较大的数据包影响到吞吐量，默认值为 1MB。

#### 3 消费端的可选配置分析

1. enable.auto.commit
   1. 消费者消费消息以后自动提交，只有当消息提交以后，该消息才不会被再次接收到，还可以配合 auto.commit.interval.ms 控制自动提交的频率。
   2. 当然，我们也可以通过 consumer.commitSync()的方式实现手动提交
2. auto.offset.reset
   1. 这个参数是针对新的 groupid 中的消费者而言的，当有新 groupid 的消费者来消费指定的 topic 时，对于该参数的配置，会有不同的语义。
   2. auto.offset.reset=latest 情况下，新的消费者将会从其他消费者最后消费的offset 处开始消费 Topic 下的消息。
   3. auto.offset.reset= earliest 情况下，新的消费者会从该 topic 最早的消息开始消费
   4. auto.offset.reset=none 情况下，新的消费者加入以后，由于之前不存在offset，则会直接抛出异常。
3. max.poll.records
   1. 此设置限制每次调用 poll 返回的消息数，这样可以更容易的预测每次 poll 间隔要处理的最大值。通过调整此值，可以减少 poll 间隔。

#### 4 提交方式

1. 自动提交

~~~java
//是否自动提交消息：offset
properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,"true");
//自动提交的时间间隔
properties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG,"1000");
~~~

2. 手动提交

~~~java
//手动异步提交
consumer.commitAsync();
//手动同步提交
consumer.commitSync();
~~~

**代码使用详见项目**

#### 5 指定消费某个分区的消息

~~~java
TopicPartition p0=new TopicPartition(KafkaProperties.TOPIC,0);
this.consumer.assign(Arrays.asList(p0));
~~~

### 四 实现细节及高吞吐量的因素

























































































