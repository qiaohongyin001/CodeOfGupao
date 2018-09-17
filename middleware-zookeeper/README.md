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

1. zookeeper 集群，包含三种角色：leader、follower、observer
2. observer：是一个特殊的 zookeeper 节点，可以帮助解决 zookeeper 的扩展性（如果大量客户端访问我们 zookeeper 集群，需要增加zookeeper集群机器数量，从而增加 zookeeper 集群的性能，导致 zookeeper 写性能下降，zookeeper 的数据变更需要半数以上服务器投票通过，造成网络消耗，增加投票成本）
   1. Observer 不参与投票，只参与投票结果
   2. 不属于 zookeeper 的关键部位
   3. 如何配置observer
      1. 在 zoo.cfg 里面增加
      2. peerType=observer
      3. server.1=192.168.11.129:2181:3181:observer
      4. - 1. peerType=observer

      5. - 1. peerType=observer

      6. - 1. peerType=observer

      7. - 1. peerType=observer

      8. - 1. peerType=observer

      9. - 1. peerType=observer

      10. - 1. peerType=observer

![zookeeper1](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-zookeeper/img/zookeeper1.jpg?raw=true)

#### 3 集群的搭建

1. 修改配置文件zoo.cfg

   1. server.id=host:post:port
      1. id的取值范围：1-255；用id来标识该机器在集群中的机器序号
   2. server.1=192.168.11.129:2888:3181
   3. server.2=192.168.11.131:2888:3181
   4. server.3=192.168.11.135:2888:3181
   5. 2888表示follower节点与leader节点交换信息的端口号
   6. 3181表示如果leader节点挂掉了，需要一个端口来重新选举

2. zoo.cfg中有一个dataDir=/temp/zookeeper

   1. $dataDir/myid添加一个myid文件。
   2. 修改dataDir=/temp/zookeeper
   3. 在/temp/zookeeper/目录中创建myid文件
   4. 在每一个服务器的dataDir目录下创建一个myid的文件，文件就一行数据，数据内容是每台机器对应的serverID数据

3. 如果需要增加observer节点

   1. 在zoo.cfg中增加：peerType = observer

   2. ~~~zoo.cfg
      server.1=192.168.11.129:2888:3181
      server.2=192.168.11.131:2888:3181
      server.3=192.168.11.135:2888:3181:observer
      ~~~

4. 启动服务

### 四 zoo.cfg 配置文件分析

- tickTime=2000
  - zookeeper中最小的时间单位长度（ms）

- initLimit=10
  - follower节点启动后与leader节点完成数据同步的时间

- syncLimit=5
  - leader节点和follower节点进行心跳检测的最大延时时间

- dataDir=/tmp/zookeeper
  - 表示zookeeper服务器存储快照文件的目录

- dataLogDir
  - 表示配置 zookeeper 事务日志的存储路径，默认指定在 dataDir 目录下

- clientPort
  - 表示客户端和服务端建立连接的端口号：2181

### 五 zookeeper中的一些概念

zookeeper的数据模型和文件系统类似，每一个节点为：znode。是zookeeper中的最小数据单元。每一个znode上都可以保存数据和挂载子节点。从而构成一个层次化的属性结构。

#### 1 节点特性

1. 持久化节点
   1. 节点创建后会一直存在zookeeper服务器上，直到主动删除
2. 持久化有序节点
   1. 每一个节点都会为它的一级子节点维护一个顺序
3. 临时节点
   1. 临时节点的生命周期和客户端的会话保持一致。当客户端会话失效，该节点自动清理，临时节点不能挂子节点
4. 临时有序节点
   1. 在临时节点上多了一个顺序性特性

#### 2 会话状态

![zookeeper1](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-zookeeper/img/zookeeper2.jpg?raw=true)

1. Client初始化连接，状态转为CONNECTING(①)
2. Client与Server成功建立连接，状态转为CONNECTED(②)
3. Client丢失了与Server的连接或者没有接受到Server的响应，状态转为CONNECTING(③)
4. Client连上另外的Server或连接上了之前的Server，状态转为CONNECTED(②)
5. 若会话过期(是Server负责声明会话过期，而不是Client )，状态转为CLOSED(⑤)，状态转为CLOSED
6. Client也可以主动关闭会话(④)，状态转为CLOSED

#### 3 Watcher

zookeeper提供了分布式数据发布/订阅，zookeeper允许客户端向服务器端注册一个watcher监听。当服务器端的节点触发指定事件的时候会出发watcher。服务端会向客户端发送一个事件通知。

watcher的通知时一次性，一旦出发一次通知后，该watcher就失效

#### 4 ACL

zookeeper 提供控制节点访问权限的功能，用于有效的保证 zookeeper 中数据的安全性。避免误操作而导致系统出现重大事故。

CREATE /READ/WRITE/DELETE/ADMIN

#### 5 zookeeper 的命令操作

- create [-s][-e] path data acl

1. -s 表示节点是否有序，默认为无序结点
2. -e 表示是否为临时节点，默认为持久化节点

~~~java
创建无序持久化节点：create /test 123
创建有序持久化节点：create -s /test/test1 123
创建无序临时接点：create -e /test/test2 123
创建有序临时接点：create -s -e /test/test3 123
~~~

- get path [watch]

1. 获得指定 path 的信息

- set path data [version]

1. 修改节点 path 对应的值
2. version：乐观锁的概念
3. 数据库中有一个 version 字段去控制数据行的版本号

- delete path [version]

1. 删除节点。删除父节点时，必须要把所有子节点删除。

#### 6 stat 信息

~~~java
cversion = 0 子节点的版本号
aclVersion = 0 标识 acl 的版本号，修改节点权限
dataVersion = 1 当前数据的版本号

czxid 节点创建时的事物ID
mzxid 节点最后一次被更新的事物ID
pzxid 当前节点下的子节点最后一次被修改时的事务ID

ctime = Sat Aug 05 20:48:26 CST 2017
mtime = Sat Aug 05 20:48:50 CST 2017

ephemeralOwner = 0x0   创建临时节点的时候，会有一个sessionId 。 该值存储的就是这个sessionid
dataLength = 3    数据值长度
numChildren = 0  子节点数
~~~

#### 7 日志信息

DataTree：底层的数据结构是基于ConcurrentHashMap的存储

- 事务日志
  -  zoo.cfg文件中，datadir
- 快照日志
  - 数据备份
  - 基于datadir的路径
- 运行时日志
  - bin/zookeepr.out

### 六 zookeeper的原理

#### 1 zookeeper 的设计猜想

zookeeper 主要是解决分布式环境下的服务协调问题而产生的，如果我们要去实现一个 zookeeper 这样的中间件，我们需要做什么？

1. 防止单点故障
   1. 如果要防止 zookeeper 这个中间件的单点故障，那就势必要做集群。而且这个集群如果要满足高性能要求的话，还得是一个高性能高可用的集群。高性能意味着这个集群能够分担客户端的请求流量，高可用意味着集群中的某一个节点宕机以后，不影响整个集群的数据和继续提供服务的可能性。
   2. **结论：所以这个中间件需要考虑到集群,而且这个集群还需要分摊客户端的请求流量。**
2. 接着上面那个结论再来思考，如果要满足这样的一个高性能集群，我们最直观的想法应该是，每个节点都能接收到请求，并且每个节点的数据都必须要保持一致。要实现各个节点的数据一致性，就势必要一个 leader 节点负责协调和数据同步操作。这个我想大家都知道，如果在这样一个集群中没有 leader 节点，每个节点都可以接收所有请求，那么这个集群的数据同步的复杂度是非常大。
   1. **结论：所以这个集群中涉及到数据同步以及会存在 leader 节点。**
3. 继续思考，如何在这些节点中选举出 leader 节点，以及leader 挂了以后，如何恢复呢？
   1. **结论：所以 zookeeper 用了基于 paxos 理论所衍生出来的 ZAB 协议**
4. leader 节点如何和其他节点保证数据一致性，并且要求是强一致的。在分布式系统中，每一个机器节点虽然都能够明确知道自己进行的事务操作过程是成功和失败，但是却无法直接获取其他分布式节点的操作结果。所以当一个事务操作涉及到跨节点的时候，就需要用到分布式事务，分布式事务的数据一致性协议有 2PC 协议和 3PC 协议。

基于这些猜想，我们基本上知道 zookeeper 为什么要用到 zab 理论来做选举、为什么要做集群、为什么要用到分布式事务来实现数据一致性了。接下来我们逐步去剖析zookeeper 里面的这些内容。

#### 2 2PC协议

当一个事务操作需要跨越多个分布式节点的时候，为了保持事务处理的 ACID 特性，就需要引入一个“协调者”（TM）来统一调度所有分布式节点的执行逻辑，这些被调度的分布式节点被称为 AP。

TM 负责调度 AP 的行为，并最终决定这些 AP 是否要把事务真正进行提交；因为整个事务是分为两个阶段提交，所以叫 2pc。

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-zookeeper/img/zookeeper3.jpg?raw=true)

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-zookeeper/img/zookeeper4.jpg?raw=true)

- 阶段一：提交事务请求（投票）

1. 事务询问
   1. 协调者向所有的参与者发送事务内容，询问是否可以执行事务提交操作，并开始等待各参与者的响应。
2. 执行事务
   1. 各个参与者节点执行事务操作，并将 Undo 和 Redo 信息记录到事务日志中，
3. 22
4. 































