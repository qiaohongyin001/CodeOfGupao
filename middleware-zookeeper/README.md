## 分布式协调服务-zookeeper

### 一 基本概念

#### 1 分布式环境的特点

- 分布式
- 并发性
  - 程序运行过程中，并发性操作是很常见的。比如：同一个分布式系统中的多个节点，同时访问一些共享资源、数据库、分布式存储。

- 无序性
  - 进程之间的消息通信，会出现顺序不一致的问题

#### 2 分布式环境下面临的问题

网络通信：网络本身的不可靠性，因此会涉及到一些网路通信问题。

网络分区（脑裂）：当网络发生异常，导致分布式系统中部分节点之间的网络延时不断增大，最终导致组成分布式架构的所有节点，只有部分节点能够正常通信。

三态：成功、失败、超时

分布式事务：ACID（原子性、隔离性、一致性、永久性）

#### 3 中心化和去中心化

冷备或者热备

分布式架构中，很多的架构思想采用的是：当集群发生故障的时候，集群中的人群会自动“选举”出一个新领导。

最典型的是：zookeeper/etcd



### 二 初识zookeeper

zookeeper是一个开源的分布式协调服务，是由雅虎创建的，基于google chubby

#### 1 是什么

分布式数据一致性的解决方案

#### 2 能做什么

数据的发布/订阅（配置中心：disconf）

负载均衡（dubbo利用了zookeeper机制实现了负载均衡）

命名服务

master选举（kafka、hadoop、hbase）

分布式队列

分布式锁

#### 3 特性

1. 顺序一致性：从同一客户端发起的事务请求，最终会严格按照顺序被应用到zookeeper中。
2. 原子性：所有的事务请求的处理结果在整个集群中的所有机器上的应用情况是一致的，也就是说，要么整个集群中的所有机器都成功应用了某一事务、要么全都不应用。
3. 可靠性：一旦事务被成功应用了某一个事务数据，并且对客户端做了响应，那么这个数据在整个集群中一定是同步保存并且保存下来的。
4. 实时性：一旦事务被成功应用，客户端能够立即从服务端读取到事务变更后的最新数据状态；（zookeeper仅仅保存在一定时间内，接近实时）
5. 单一视图：无论客户端连接到哪个服务端，所看到的模型都是一样的。

**zookeeper并不是来存储数据的，通过监控数据状态的变化，达到基于数据的集群管理**

### 三 zookeeper安装部署

#### 1 单环境下的安装

- 下载zookeeper的安装包
  - http://apache.fayea.com/zookeeper/stable/zookeeper-3.4.10.tar.gz

- 解压zookeeper
  - tar -zxvf zookeeper-3.4.10.tar.gz

- cd 到 ZK_HOME/conf，copy一份zoo.cfg
  - cp  zoo_sample.cfg  zoo.cfg

- sh zkService.sh
  - {start|start-foreground|stop|restart|status|upgrade|print-cmd}
  - sh zkServer.sh xxx：可以看到命令帮助

- sh zkCli.sh -server  ip:port 客户端链接zookeeper服务
  - sh zkCli.sh -server localhost:2181

#### 2 集群环境





















































