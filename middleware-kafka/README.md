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

#### 1 实现细节

1. 消息
   1. 消息有key【可选】、value；
   2. 消息是kafka中最基本的数据单元。消息由一串字节构成，其中主要由key和value构成，key和value也都是byte数组。
   3. key的主要作用是根据一定的策略，将消息路由到指定的分区中，这样就可以保证包含同一个key的消息全部写入到同一个分区中，key可以是null。
   4. 为了提高网络的存储和利用率，生产者会批量发送消息到kafka，并在发送之前对消息进行压缩。
2. topic & partition
   1. topic是用于存储消息的逻辑概念，可以看作一个消息集合。每个topic可以有多个生产者向其推送消息，也可以有任意多个消费者消费其中的消息。
   2. 每个topic可以划分多个分区（每个topic至少有一个分区），同一个topic下的不同分区包含的消息是不同的。每个消息在被添加到分区时，都会被分配一个offset（称之为偏移量），他是消息在此分区中的唯一编号，kafka通过offset保证消息在分区内的顺序，offset的顺序不跨分区，即kafka只保证再同一个分区内的消息是有序的；
   3. partition是以文件的形式存储在文件系统中，存储在由配置文件中的log.dirs指定的目录下，命名规则：<topic_name>-<partition_id>

|                                                              |                                                              |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| ![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-kafka/img/kafka5.jpg?raw=true) | ![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-kafka/img/kafka6.jpg?raw=true) |

#### 2 高吞吐量的因素

1. 顺序写的方式存储数据；频繁的IO（网络IO和磁盘IO）
2. 批量发送
   1. 在异步发送模式中。kafka允许进行批量发送，也就是先讲消息缓存到内存中，然后一次请求批量发送出去。这样减少磁盘频繁IO以及网络IO造成的性能瓶颈。
   2. batch.size 每批次发送的数据大小
   3. linger.ms 间隔时间
3. 零拷贝
   1. 消息从发送到落地保存，broker维护的消息日志本身就是文件目录，每个文件都是二进制保存，生产者和消费者使用相同的格式来处理。在消费者获取消息时，服务器先从硬盘读取数据到内存，然后把内存中的数据原封不动的通过socket发送给消费者。虽然这个操作描述起来简单，但实际上经历了很多步骤：

~~~java
//1.操作系统将数据从磁盘读入到内核空间的页缓存
//2.应用程序将数据从内核空间读入到用户空间缓存中
//3.应用程序将数据写回到内核空间到socket缓存中
//4.操作系统将数据从socket缓冲区复制到网卡缓存区，以便数据经网络发出
~~~

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-kafka/img/kafka7.jpg?raw=true)

通过“零拷贝”技术可以去掉这些没必要的数据复制操作，同时也会减少上下文切换次数。FileChannel.transferTo

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-kafka/img/kafka8.jpg?raw=true)

### 五 kafka 的消费原理

之前Kafka存在的一个非常大的性能隐患就是利用ZK来记录各个Consumer Group 的消费进度（offset）。当然JVM Client帮我们自动做了这些事情，但是Consumer需要和ZK频繁交互，而利用ZK Client API对ZK频繁写入事一个低效的操作。并且从水平扩展上来讲也存在问题。所以ZK抖一抖，集群吞吐量就跟着一起抖，严重的时候简直抖的停不下来。

新版Kafka已推荐将consumer的位移信息保存再Kafka内部的topic中，即__consumer_offsets_topic。通过一下才做来看看____consumer_offsets_topic是怎么存储消费进度的，__consumer_offsets_topic默认有50个分区。

1. 计算consumer group对应的hash值

2. 1. Math.abs("DemoGroup1".hashCode())%50

3. 获得consumer group的位移信息

4. 1. bin/kafka-simple-consumer-shell.sh --topic __consumer_offsets --partition 15 -broker-list 192.168.11.140:9092,192.168.11.141:9092,192.168.11.138:9092 --formatter "kafka.coordinator.group.GroupMetadataManager\$OffsetsMessageFormatter"

~~~java
[root@iZ2zeeufalzdvyrne9oa4qZ kafka-logs]# ls /tmp/kafka-logs/
cleaner-offset-checkpoint  __consumer_offsets-21  __consumer_offsets-33  __consumer_offsets-48        mhxy-wdd-0
__consumer_offsets-0       __consumer_offsets-24  __consumer_offsets-36  __consumer_offsets-6         recovery-point-offset-checkpoint
__consumer_offsets-12      __consumer_offsets-27  __consumer_offsets-39  __consumer_offsets-9         replication-offset-checkpoint
__consumer_offsets-15      __consumer_offsets-3   __consumer_offsets-42  log-start-offset-checkpoint
__consumer_offsets-18      __consumer_offsets-30  __consumer_offsets-45  meta.properties
~~~

### 六 日志策略

#### 1 日志保留策略

无论消费者是否已经消费了消息，kafka都会一直保存这些消息，但并不会像数据库那样长期保存。为了避免磁盘被占满，kafka会配置响应的保留策略（retention policy），以实现周期性地删除陈旧数据。

kafka有两种“保留策略”：

1. 根据消息保留的时间，当消息在kafka中保存的时间超过了指定时间，就可以被删除。
2. 根据topic存储的数据大小，当topic所占的日志文件大小大于一个阈值，则可以开始删除最旧的消息。

#### 2 日志压缩策略

在很多场景中，消息的key与value的值之间对应关系是不断变化的，就像数据库中的数据会不断被修改一样，消费者只关心key对应的最新的value。我们可以开日志压缩功能，kafka定期将相同key的消息进行合并，只保留最新的value值。

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-kafka/img/kafka9.jpg?raw=true)

### 七 消息可靠性机制

#### 1 消息可靠性机制

生产者发送消息到broker，有三种确认方式（request.required.acks）

1. acks=0

2. 1. producer不会等待broker（leader）发送ack。因为发送消息网络超时或broker crash（1.partition的leader还没有commit消息。2.leader与follower数据不同步），既有可能丢失也可能会重发。

3. acks = 1

4. 1. 当leader接收到消息之后发送ack，丢会重发，丢的概率很小

5. acks = -1

6. 1. 当所有的follower都同步消息成功后发送ack. 丢失消息可能性比较低。

#### 2 消息存储可靠性

1. 每一条消息被发送到broker中，会根据partition规则选择被存储到哪一个partition。

2. 1. 如果partition规则设置的合理，所有消息可以均匀分布到不同的partition里，这样就实现了水平扩展。

3. 在创建topic时可以指定这个topic对应的partition的数量。在发送一条消息时，可以指定这条消息的key，producer根据这个key和partition机制来判断这个消息发送到哪个partition。

4. kafka的高可靠性的保障来自于另一个叫副本（replication）策略，通过设置副本的相关参数，可以使kafka在性能和可靠性之间做不同的切换。

#### 3 高可靠性副本

~~~java
//--replication-factor 表示的副本数：1标识默认没有副本
sh kafka-topics.sh --create --zookeeper 192.168.11.140:2181 
--replication-factor 2 --partitions 3 --topic sixsix
~~~

#### 4 副本机制

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-kafka/img/kafka10.jpg?raw=true)

副本的leader选举：数据同步，leader选举

##### 4.1 ISR（副本同步队列 zookeeper上isr_change_notification节点)

**维护的是有资格的follower节点**

1. 副本的所有节点都必须要和zookeeper保持连接状态
2. 副本的最后一条消息的offset和leader副本的最后一条消息的offset之间的差值不能超过指定的阀值，这个阀值是可以设置的（replica.lag.max.messages）

##### 4.2 HW&LEO（follow与leader数据同步）

关于follower副本同步的过程中，还有两个关键的概念，HW(HighWatermark)和LEO(Log End Offset)。 
这两个参数跟ISR集合紧密关联。HW标记了一个特殊的offset，当消费者处理消息的时候，只能拉去到HW之前的消息，HW之后的消息对消费者来说是不可见的。也就是说，取partition对应ISR中最小的LEO作为HW，consumer最多只能消费到HW所在的位置。

每个replica都有HW，leader和follower各自维护更新自己的HW的状态。对于leader新写入的消息，consumer不能立刻消费，leader会等待该消息被所有ISR中的replicas同步更新HW，此时消息才能被consumer消费。这样就保证了如果leader副本损坏，该消息仍然可以从新选举的leader中获取。

LEO 是所有副本都会有的一个offset标记，它指向追加到当前副本的最后一个消息的offset。当生产者向leader副本追加消息的时候，leader副本的LEO标记就会递增；当follower副本成功从leader副本拉去消息并更新到本地的时候，follower副本的LEO就会增加。

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-kafka/img/kafka11.jpg?raw=true)

### 八 高可用副本机制

在kfaka0.8版本前，并没有提供这种High Availablity机制，也就是说一旦一个或者多个broker宕机，则在这期间内所有的partition都无法继续提供服务。如果broker无法再恢复，则上面的数据就会丢失。所以在0.8版本以后引入了High Availablity机制。

#### 1 关于leader election

在kafka引入replication机制以后，同一个partition会有多个Replica。那么在这些replication之间需要选出一个Leader，Producer或者Consumer只与这个Leader进行交互，其他的Replica作为Follower从leader中复制数据（因为需要保证一个Partition中的多个Replica之间的数据一致性，其中一个Replica宕机以后其他的Replica必须要能继续提供服务且不能造成数据重复和数据丢失）。

如果没有leader，所有的Replica都可以同时读写数据，那么就需要保证多个Replica之间互相同步数据，数据一致性和有序性就很难保证，同时也增加了Replication实现的复杂性和出错的概率。在引入leader以后，leader负责数据读写，follower只向leader顺序fetch数据，简单而且高效。

#### 2 如何将所有的Replica均匀分布到整个集群

为了更好的做到负载均衡，kafka尽量会把所有的partition均匀分配到整个集群上。如果所有的replica都在同一个broker上，那么一旦broker宕机所有的Replica都无法工作。kafka分配Replica的算法。

1. 把所有的Broker（n）和待分配的Partition排序
2. 把第i个partition分配到 （i mod n）个broker上
3. 把第i个partition的第j个Replica分配到 ( (i+j) mod n) 个broker上

#### 3 如何处理所有的Replica不工作的情况

在ISR中至少有一个follower时，Kafka可以确保已经commit的数据不丢失，但如果某个Partition的所有Replica都宕机了，就无法保证数据不丢失了

1. 等待ISR中的任一个Replica“活”过来，并且选它作为Leader
2. 选择第一个“活”过来的Replica（不一定是ISR中的）作为Leader

这就需要在可用性和一致性当中作出一个简单的折衷。

如果一定要等待ISR中的Replica“活”过来，那不可用的时间就可能会相对较长。而且如果ISR中的所有Replica都无法“活”过来了，或者数据都丢失了，这个Partition将永远不可用。

选择第一个“活”过来的Replica作为Leader，而这个Replica不是ISR中的Replica，那即使它并不保证已经包含了所有已commit的消息，它也会成为Leader而作为consumer的数据源（前文有说明，所有读写都由Leader完成）。

Kafka0.8.*使用了第二种方式。Kafka支持用户通过配置选择这两种方式中的一种，从而根据不同的使用场景选择高可用性还是强一致性。

### 九 文件存储机制

~~~java
[root@iZ2zeeufalzdvyrne9oa4qZ mhxy-wdd-0]# ls /tmp/kafka-logs/mhxy-wdd-0/
00000000000000000000.index  00000000000000000000.log  00000000000000000000.timeindex  leader-epoch-checkpoint
~~~

#### 1 查看kafka数据文件内容

在使用kafka的过程中有时候需要我们查看产生的消息的信息，这些都被记录在kafka的log文件中。由于log文件的特殊格式，需要通过kafka提供的工具来查看

~~~java
./bin/kafka-run-class.sh kafka.tools.DumpLogSegments --files /tmp/kafka-logs/*/000**.log  --print-data-log {查看消息内容}

[root@iZ2zeeufalzdvyrne9oa4qZ bin]# sh kafka-run-class.sh kafka.tools.DumpLogSegments --files /tmp/kafka-logs/mhxy-wdd-0/00000000000000000000.log  --print-data-log
Dumping /tmp/kafka-logs/mhxy-wdd-0/00000000000000000000.log
Starting offset: 0
baseOffset: 0 lastOffset: 0 baseSequence: -1 lastSequence: -1 producerId: -1 producerEpoch: -1 partitionLeaderEpoch: 4 isTransactional: false position: 0 CreateTime: 1537926853638 isvalid: true size: 80 magic: 2 compresscodec: NONE crc: 2875669935
baseOffset: 1 lastOffset: 1 baseSequence: -1 lastSequence: -1 producerId: -1 producerEpoch: -1 partitionLeaderEpoch: 4 isTransactional: false position: 80 CreateTime: 1537926854654 isvalid: true size: 80 magic: 2 compresscodec: NONE crc: 3836252510
baseOffset: 2 lastOffset: 2 baseSequence: -1 lastSequence: -1 producerId: -1 
~~~

#### 2 存储机制

在kafka文件存储中，同一个topic下有多个不同的partition，每个partition为一个目录，partition的名称规则为：topic名称+有序序号，第一个序号从0开始，最大的序号为partition数量减1，partition是实际物理上的概念，而topic是逻辑上的概念。

partition还可以细分为segment，这个segment是什么呢？ 假设kafka以partition为最小存储单位，那么我们可以想象当kafka producer不断发送消息，必然会引起partition文件的无线扩张，这样对于消息文件的维护以及被消费的消息的清理带来非常大的挑战，所以kafka 以segment为单位又把partition进行细分。每个partition相当于一个巨型文件被平均分配到多个大小相等的segment数据文件中（每个setment文件中的消息不一定相等），这种特性方便已经被消费的消息的清理，提高磁盘的利用率。

**segment（部分） file组成：**由2大部分组成，分别为index file和data file，此2个文件一一对应，成对出现，后缀".index"和“.log”分别表示为segment索引文件、数据文件。

**segment文件命名规则：**partition全局的第一个segment从0开始，后续每个segment文件名为上一个segment文件最后一条消息的offset值。数值最大为64位long大小，19位数字字符长度，没有数字用0填充。

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-kafka/img/kafka12.jpg?raw=true)

#### 3 查找方式

以下图为例，读取offset=170418的消息，首先查找segment文件，其中00000000000000000000.index为最开始的文件，第二个文件为00000000000000170410.index（起始偏移为170410+1=170411），而第三个文件为00000000000000239430.index（起始偏移为239430+1=239431），所以这个offset=170418就落到了第二个文件之中。其他后续文件可以依次类推，以其实偏移量命名并排列这些文件，然后根据二分查找法就可以快速定位到具体文件位置。其次根据00000000000000170410.index文件中的[8,1325]定位到00000000000000170410.log文件中的1325的位置进行读取。

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-kafka/img/kafka13.jpg?raw=true)

### 十 kafka 分区分配策略

在kafka中每个topic一般都会有很多个partitions。为了提高消息的消费速度，我们可能会启动多个consumer去消费；同时，kafka存在consumer group概念，也就是group.id一样的consumer，这些consumer属于一个consumer group，组内的所有消费者协调在一起来消费订阅主题的所有分区。

当然每个分区只能由通一个消费组内的consumer来消费，那么同一个consumer group里边的consumer事怎么去分配该消费哪个分区的数据，这个就涉及到了kafka内部分区分配策略（Partition Assignment Strategy）。

#### 1. 分区分配策略（Partition Assignment Strategy）

在kafka内部存在两种默认的分区分配策略：Range（默认）和RoundRobin。

通过：partition.assignment.strategy（ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG）指定。

~~~java
//轮询
properties.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG,
"org.apache.kafka.clients.consumer.RoundRobinAssignor");
//范围分区
properties.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG,
"org.apache.kafka.clients.consumer.RangeAssignor");
~~~

#### 2 consumer rebalance

当以下事件发生时，kafka将会进行一次分区分配：

1. 同一个consumer group内新增了消费者
2. 消费者离开当前所属的consumer group，包括shuts down或crashes
3. 订阅的主题新增分区（分区数量发生变化）
4. 消费者主动取消对某个topic的订阅

也就是说，把分区的所有权从一个消费者移到另外一个消费者上，这个kafka consumer的rebalance机制。如何rebalance就涉及导前面说的分区分配策略。

#### 3 两种分区策略

1. Range策略（默认）

2. 1. 0、1、2、3、4、5、6、7、8、9
   2. c0[0,3]、c1[4,6]、c0[7,9]
   3. 10(partition num/3(consumer num) =3

3. roundrobin策略

4. 1. 0、1、2、3、4、5、6、7、8、9
   2. c0,c1,c2
   3. c0 [0,3,6,9]、c1 [1,4,7]、c2 [2,5,8]
   4. kafka 的key 为null， 是随机｛一个Metadata的同步周期内，默认是10分钟｝

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-kafka/img/kafka14.jpg?raw=true)

#### 4 谁来执行 Rebalance 以及管理 consumer 的 group 呢？

Kafka提供了一个角色：coordinator来执行对于consumer group 的管理，当 consumer group 的第一个 consumer 启动的时候，它会去和 kafka server 确定谁是它们组的 coordinator。之后该 group 内的所有成员都会和该 coordinator 进行协调通信。

1.  如何确定 coordinator
   1. consumer group 如何确定自己的 coordinator 是谁呢, 消费者向 kafka 集群中的任意一个 broker 发送一个GroupCoordinatorRequest 请求，服务端会返回一个负载最小的 broker 节 点的 id，并将该 broker 设 置为coordinator。
2. JoinGroup 的过程
   1. 在 rebalance 之前，需要保证 coordinator 是已经确定好了的，整个 rebalance 的过程分为两个步骤，Join 和 Syncjoin: 表示加入到 consumer group 中，在这一步中，所有的成员都会向 coordinator 发送 joinGroup 的请求。一旦所有成员都发送了 joinGroup 请求，那么 coordinator 会选择一个 consumer 担任 leader 角色，并把组成员信息和订阅信息发送消费者。
   2. protocol_metadata: 序列化后的消费者的订阅信息
   3. leader_id： 消费组中的消费者，coordinator 会选择一个作为 leader，对应的就是 member_id
   4. member_metadata 对应消费者的订阅信息
   5. members：consumer group 中全部的消费者的订阅信息
   6. generation_id： 年代信息，类似于之前讲解 zookeeper 的时候的 epoch 是一样的，对于每一轮 rebalance，generation_id 都会递增。主要用来保护 consumer group。
   7. 隔离无效的 offset 提交。也就是上一轮的 consumer 成员无法提交 offset 到新的 consumer group 中。

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-kafka/img/kafka15.jpg?raw=true)

3. Synchronizing Group State 阶段
   1. 完成分区分配之后，就进入了 Synchronizing Group State ， 主要逻辑是向GroupCoordinator 发送SyncGroupRequest 请求，并且处理 SyncGroupResponse响应，简单来说，就是 leader 将消费者对应的 partition 分配方案同步给 consumer group 中的所有 consumer。
   2. 每个消费者都会向 coordinator 发送 syncgroup 请求，不过只有 leader 节点会发送分配方案，其他消费者只是打打酱油而已。当 leader 把方案发给 coordinator 以后，coordinator 会把结果设置到 SyncGroupResponse 中。这样所有成员都知道自己应该消费哪个分区。
   3. consumer group 的分区分配方案是在客户端执行的！Kafka 将这个权利下放给客户端主要是因为这样做可以有更好的灵活性。

#### 5 自定义消息发送分区

**具体代码详见项目**