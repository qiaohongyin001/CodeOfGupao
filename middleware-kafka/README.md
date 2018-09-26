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





















































































