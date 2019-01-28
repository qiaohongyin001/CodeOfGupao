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

##### 2.1 数据的发布/订阅（配置中心：disconf）

实现配置信息的集中式管理和数据的动态更新

实现配置中心有两种模式：push、pull

ps：有些公司用长轮询做法

zookeeper采用的是推拉结合的方式。客户端向服务器端注册自己需要关注的节点。一旦节点数据发生变化，那么服务器端就会向客户端发送watcher事件通知。客户端收到通知后，主动到服务器端获取更新后的数据。

特征：

1. 数据量比较小
2. 数据内容在运行时会发生动态变更
3. 集群中的各个机器共享配置

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-zookeeper/img/zookeeper6.jpg?raw=true)

##### 2.2 负载均衡（dubbo利用了zookeeper机制实现了负载均衡）

请求/数据分摊多个计算机单元上

##### 2.3 分布式锁

通常实现分布式锁有几种方式：

1. redis.setNX 存在则会返回0
2. 数据库方式去实现
   1. 创建一个表，通过索引唯一的方式
   2. create table(id,methodname~~~)methodname增加唯一索引
   3. insert一条数据XXX	delete语句删除这条记录
   4. mysql for update

3. zookeeper实现

   排他锁（写锁）

##### 2.4 命名服务

节点增加映射关系

##### 2.5 master选举（kafka、hadoop、hbase）

7*24小时可用， 99.999%可用

master-slave模式

使用zookeeper解决：多客户端争抢一个节点

##### 2.6 分布式队列

activeMQ、kafka、….

先进先出队列：

1. 通过getChildren获取指定根节点下的所有子节点，子节点就是任务
2. 确定自己节点在子节点中的顺序
3. 如果自己不是最小的子节点，那么监控比自己小的上一个子节点，否则处于等待
4. 接收watcher通知，重复流程

Barrier模式：就是加了一个触发条件

在节点中增加子节点，增加到一定数量后，再执行后续操作。否则处于等待

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

**Acl权限的操作**

~~~txt
保证存储在zookeeper上的数据安全性问题
schema(ip/Digest/world/super)
授权对象（192.168.1.1/11 , root:root / world:anyone/ super）
~~~

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
  -  zoo.cfg文件中，DataLogDir
  - log.zxid
- 快照日志
  - 数据备份
  - 基于datadir的路径
  - snapshot.30000232e
- 运行时日志
  - bin/zookeepr.out

查询事务日志命令：

~~~java
java -cp :/data/zookeeper-3.4.12/lib/slf4j-api-1.7.25.jar:/data/zookeeper-3.4.12/zookeeper-3.4.12.jar org.apache.zookeeper.server.LogFormatter log.30000232f 
~~~



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
   1. 各个参与者节点执行事务操作，并将 Undo 和 Redo 信息记录到事务日志中，尽量把提交过程中所有消耗时间的操作和准备都提前完成确保后面 100% 成功提交事务。
3. 各个参与者向协调者反馈事务询问的响应
   1. 如果各个参与者成功执行了事务操作，那么就反馈给协调者 yes 的响应，表示事务可以执行；
   2. 如果参与者没有成功执行事务，就反馈给协调者 no 的响应，表示事务不可以执行；
   3. 上面这个阶段有点类似协调者组织各个参与者对一次事务操作的投票表态过程，因此 2PC 协议的第一个阶段为“投票阶段”，即各参与者投票表明是否需要继续执行接下去的事务提交操作。

- 阶段二

1. 在这个阶段，协调者会根据各参与者反馈情况来决定最终是否可以进行事务提交操作，正常情况下包含两种可能：执行事务、中断事务。

#### 3 zookeeper的集群

在 zookeeper 中，客户端会随机连接到 zookeeper 集群中的一个节点，如果是读请求，就直接从当前节点中读取数据，如果是写请求，那么请求会被转发给leader提交事务，然后 leader 会广播事务，只要有超过半数节点写入成功，那么写请求就会被提交（类 2PC 事务）。

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-zookeeper/img/zookeeper5.jpg?raw=true)

所有事务请求必须由一个全局唯一的服务器来协调处理，这个服务器就是 Leader 服务器，其他的服务器就是follower。leader 服务器把客户端的事务请求转化成一个事务 Proposal（提议），并把这个 Proposal 分发给集群中的所有 Follower 服务器。之后 Leader 服务器需要等待所有Follower 服务器的反馈，一旦超过半数的 Follower 服务器进行了正确的反馈，那么 Leader 就会再次向所有的 Follower 服务器发送 Commit 消息，要求各个 follower 节点对前面的一个 Proposal 进行提交;

- 集群角色

1. Leader 角色
   1. Leader 服务器是整个 zookeeper 集群的核心，主要的工作任务有两项
      1. 事务请求的唯一调度和处理者，保证集群事务处理的顺序性
      2. 集群内部各服务的调度者
2. Follower 角色
   1. 处理客户端非事务请求，转发事务请求给 leader 服务器
   2. 参与事务请求 Proposal 的投票（需要半数以上服务器通过才能通知 leader commit 数据; Leader 发起的提案，要求 Follower 投票）
   3. 参与 leader 选举的投票
3. Observer 角色
   1. 观察 zookeeper 集群中最新状态的变化并将这些状态同步到 observer 服务器上。
   2. 增加 observer，不影响急群众事务处理能力，同时还能提高集群的非事务处理能力。

- 集群的组成

1. zookeeper 一般是由 2n+1 台服务器组成。
2. 因为一个节点要成为集群中的 leader，需要有超过及群众过半数的节点支持，这个涉及到 leader 选举算法，同时也涉及到事务请求的提交投票。

#### 4 ZAB 协议

ZAB（Zookeeper Atomic Broadcast） 协议是为分布式协调服务 ZooKeeper 专门设计的一种支持**崩溃恢复的原子广播协议**。

在 ZooKeeper 中，主要依赖 ZAB 协议来实现分布式数据一致性，基于该协议，ZooKeeper 实现了一种主备模式的系统架构来保持集群中各个副本之间的数据一致性。

- ZAB 协议包含两种基本模式
  - 崩溃恢复
  - 原子广播

当整个集群在启动时，或者当 leader 节点出现网络中断、崩溃等情况时，ZAB 协议就会进入恢复模式并选举产生新的 Leader，当 leader 服务器选举出来后，并且集群中有过半的机器和该 leader 节点完成数据同步后（同步指的是数据同步，用来保证集群中过半的机器能够和 leader 服务器的数据状态保持一致），ZAB 协议就会退出恢复模式。当集群中已经有过半的 Follower 节点完成了和 Leader 状态同步以后，那么整个集群就进入了消息广播模式。这个时候，在 Leader 节点正常工作时，启动一台新的服务器加入到集群，那这个服务器会直接进入数据恢复模式，和leader 节点进行数据同步。同步完成后即可正常对外提供非事务请求的处理。

- 原子广播

1. leader 接收到消息请求后，将消息赋予一个全局唯一的 64 位自增 id，叫：zxid，通过 zxid 的大小比较既可以实现因果有序这个特征。
2. leader 为每个 follower 准备了一个 FIFO 队列（通过 TCP 协议来实现，以实现了全局有序这一个特点）将带有 zxid 的消息作为一个提案（proposal）分发给所有的 follower 
3. 当 follower 接收到 proposal，先把 proposal 写到磁盘，写入成功以后再向 leader 回复一个 ack
4. 当 leader 接收到合法数量（超过半数节点）的 ACK 后，leader 就会向这些 follower 发送 commit 命令，同时会在本地执行该消息
5. 当 follower 收到消息的 commit 命令以后，会提交该消息

**leader 的投票过程，不需要 Observer 的 ack，也就是 Observer 不需要参与投票过程，但是 Observer 必须要同步 Leader 的数据从而在处理请求的时候保证数据的一致性。**

每个角色详细工作：

**Leader与Follower同步数据（原子广播）**

1. 设置新的逻辑时钟值。每次leader选举后都会根据数据id值，生成新的逻辑时钟值。
2. leader构建NEWLEADER封包,该封包的数据是当前最大数据的id
3. 广播给所有的follower,也就是告知follower leader保存的数据id是多少,大家看看是不是需要同步。
4. leader根据follower数量给每个follower创建一个线程LearnerHandler,专门负责接收它们的同步数据请求，leader主线程开始阻塞在这里,等待其他follower的回应(也就是LearnerHandler线程的处理结果)
5. 只有在超过半数的follower已经同步数据完毕,这个过程才能结束,leader才能正式成为leader.

**leader所做的工作**

leader接收到的来自某个follower封包一定是FOLLOWERINFO,该封包告知了该服务器保存的数据id.之后根据这个数据id与本机保存的数据进行比较:

1. 如果数据完全一致,则发送DIFF封包告知follower当前数据就是最新的了.

2. 判断这一阶段之内有没有已经被提交的提议值,如果有,那么:

3. 1. 如果有部分数据没有同步,那么会发送DIFF封包将有差异的数据同步过去.同时将follower没有的数据逐个发送COMMIT封包给follower要求记录下来.
   2. 如果follower数据id更大,那么会发送TRUNC封包告知截除多余数据.（一台leader数据没同步就宕掉了，选举之后恢复了，数据比现在leader更新）

4. 如果这一阶段内没有提交的提议值,直接发送SNAP封包将快照同步发送给follower.

5. 消息完毕之后,发送UPTODATE封包告知follower当前数据就是最新的了,再次发送NEWLEADER封包宣称自己是leader,等待follower的响应.

**follower做的工作**

1. 会尝试与leader建立连接,这里有一个机制,如果一定时间内没有连接上,就报错退出,重新回到选举状态.
2. 其次在发送FOLLOWERINFO封包,该封包中带上自己的最大数据id,也就是会告知leader本机保存的最大数据id.
3. 根据前面对LeaderHandler的分析,leader会根据不同的情况发送DIFF,UPTODATE,TRUNC,SNAP,依次进行处理就是了,此时follower跟leader的数据也就同步上了.
4. 由于leader端发送的最后一个封包是UPTODATE,因此在接收到这个封包之后follower结束同步数据过程,发送ACK封包回复leader.

以上过程中,任何情况出现的错误,服务器将自动将选举状态切换到LOOKING状态,重新开始进行选举.

- 崩溃恢复

当Leader节点崩溃或由于网络问题导致 Leader 服务器失去了过半的Follower 节点的联系。就会进入崩溃回复模式。在 ZAB 协议中，为了保证程序的正确运行，整个恢复过程结束后需要选举出一个新的Leader。

为了使 leader 挂了后系统能正常工作，需要解决以下两个问题：

1. 已经被处理的消息不能丢失
   1. 当 leader 收到合法数量 follower 的 ACKs 后，就向各个 follower 广播 COMMIT 命令，同时也会在本地执行 COMMIT 并向连接的客户端返回「成功」。但是如果在各个 follower 在收到 COMMIT 命令前 leader就挂了，导致剩下的服务器并没有执行都这条消息。
   2. leader 对事务消息发起 commit 操作，但是该消息在follower1 上执行了，但是 follower2 还没有收到 commit，就已经挂了，而实际上客户端已经收到该事务消息处理成功的回执了。所以在 zab 协议下需要保证所有机器都要执行这个事务消息。
2. 被丢弃的消息不能再次出现
   1. 当 leader 接收到消息请求生成 proposal 后就挂了，其他 follower 并没有收到此 proposal，因此经过恢复模式重新选了 leader 后，这条消息是被跳过的。此时，之前挂了的 leader 重新启动并注册成了 follower，他保留了被跳过消息的 proposal 状态，与整个系统的状态是不一致的，需要将其删除。

ZAB 协议需要满足上面两种情况，就必须要设计一个leader 选举算法：能够确保已经被 leader 提交的事务Proposal 能够提交、同时丢弃已经被跳过的事务Proposal。

如果 leader 选举算法能够保证新选举出来的 Leader 服务器拥有集群中所有机器最高编号（ZXID 最大）的事务 Proposal，那么就可以保证这个新选举出来的 Leader 一定具有已经提交的提案。因为所有提案被 COMMIT 之前必须有超过半数的 follower ACK，即必须有超过半数节点的服务器的事务日志上有该提案的 proposal，因此，只要有合法数量的节点正常工作，就必然有一个节点保存了所有被 COMMIT 消息的 proposal 状态。

另外一个，zxid 是 64 位，高 32 位是 epoch 编号，每经过一次 Leader 选举产生一个新的 leader，新的 leader 会将 epoch 号+1，低 32 位是消息计数器，每接收到一条消息这个值+1，新 leader 选举后这个值重置为 0.这样设计的好处在于老的leader挂了以后重启，它不会被选举为leader，因此此时它的 zxid 肯定小于当前新的 leader。当老的leader 作为 follower 接入新的 leader 后，新的 leader 会让它将所有的拥有旧的 epoch 号的未被 COMMIT 的proposal 清除。

- 关于 ZXID

zxid，也就是事务 id

为了保证事务的顺序一致性，zookeeper 采用了递增的事务 id 号（zxid）来标识事务。所有的提议（proposal）都在被提出的时候加上了 zxid。实现中 zxid 是一个 64 位的数字，它高 32 位是 epoch（ZAB 协议通过 epoch 编号来区分 Leader 周期变化的策略）用来标识 leader 关系是否改变，每次一个 leader 被选出来，它都会有一个新的epoch=（原来的 epoch+1），标识当前属于那个 leader 的统治时期。低 32 位用于递增计数。

epoch：可以理解为当前集群所处的年代或者周期，每个leader 就像皇帝，都有自己的年号，所以每次改朝换代，leader 变更之后，都会在前一个年代的基础上加 1。这样就算旧的 leader 崩溃恢复之后，也没有人听他的了，因为 follower 只听从当前年代的 leader 的命令。

**epoch 的变化大家可以做一个简单的实验：**

1. 启动一个 zookeeper 集群。
2. 在/tmp/zookeeper/VERSION-2 路径下会看到一个currentEpoch 文件。文件中显示的是当前的 epoch
3. 把 leader 节点停机，这个时候在看 currentEpoch 会有变化。 随着每次选举新的 leader，epoch 都会发生变化。

#### 5 Leader 选举

- leader 选举会分为两个过程
  - 启动的时候的 leader 选举
  - leader 崩溃的时候的选举

- 选举方式
  - leaderElection/AuthFastLeaderElection/**FastLeaderElection**
    - QuorumPeer、startLeaderElection
    - 源码地址：https://github.com/apache/zookeeper.git
    - 需要的条件： jdk 1.7以上 、ant 、idea
  - **FastLeaderElection**
    - serverid：在配置server集群的时候，给定服务器的标识id(myid)
    - zxid：服务器在运行时产生的数据ID，zxid的值越大，表示数据越新
    - Epoch：选举的轮数
    - server的状态：Looking、Following、Observering、Leading

##### 5.1 启动的时候的 leader 选举

每个节点启动的时候状态都是 LOOKING，处于观望状态，接下来就开始进行选主流程。进行 Leader 选举，至少需要两台机器（具体原因前面已经讲过了），我们选取 3 台机器组成的服务器集群为例。在集群初始化阶段，当有一台服务器 Server1 启动时，它本身是无法进行和完成 Leader 选举，当第二台服务器 Server2 启动时，这个时候两台机器可以相互通信，每台机器都试图找到 Leader，于是进入 Leader 选举过程。选举过程如下：

1. 每个Server发出一个投票。由于是初始情况，server1和server2都会将自己作为Leader服务器进行投票，每次投票会包含所推举的服务器myid和ZXID、epoch，使用（myid，ZXID，epoch）来表示，此时Server1的投票为（1，0），Server2的投票为（2，0），然后各自将这个投票发送给集群中其他机器。

2. 接收来自各个服务器的投票。集群的每个服务器收到投票后，首先判断该投票的有效性，如检查是否是本轮投票（epoch）、是否来自LOOKING状态的服务器。

3. 处理投票。针对每个投票，服务器都讲别人的投票和自己的投票进行PK，PK规则如下：

4. 1. 有限检查ZXID。ZXID比较大的服务器优先作为Leader
   2. 如果ZXID相同，比较myid，myid较大的服务器作为Leader

对于Server1而言，他的投票是（1，0），接收Server2的投票为（2，0），首先比较首先会比较两者的 ZXID，均为 0，再比较 myid，此时 Server2 的 myid 最大，于是更新自己的投票为(2, 0)，然后重新投票，对于Server2而言，它不需要更新自己的投票，只是再次向集群中所有机器发出上一次投票信息即可。

4. 统计投票。每次投票后，服务器都会统计投票信息，判断是否已经有过半机器接受到相同的投票信息，对于 Server1、Server2 而言，都统计出集群中已经有两台机器接受了(2, 0)的投票信息，此时便认为已经选出了 Leader。

5. 改变服务器状态。一旦确定了 Leader，每个服务器就会更新自己的状态，如果是 Follower，那么就变更为FOLLOWING，如果是 Leader，就变更为 LEADING。

##### 5.2 运行过程中的 leader 选举

当集群中的 leader 服务器出现宕机或者不可用的情况时，那么整个集群将无法对外提供服务，而是进入新一轮的Leader 选举，服务器运行期间的 Leader 选举和启动时期的 Leader 选举基本过程是一致的。
​    1. 变更状态
​      a. Leader 挂后，余下的非 Observer 服务器都会将自己的服务器状态变更为 LOOKING，然后开始进入 Leader 选举过程。
​    2. 发起投票
​      a. 每个 Server 会发出一个投票。在运行期间，每个服务器上的 ZXID 可能不同，此时假定 Server1 的 ZXID 为123，Server3的ZXID为122；在第一轮投票中，Server1和 Server3 都会投自己，产生投票(1, 123)，(3, 122)，然后各自将投票发送给集群中所有机器。接收来自各个服务器的投票。与启动时过程相同。
​    3. 处理投票
​      a. 与启动时过程相同，此时，Server1 将会成为 Leader。
​    4. 统计投票
​      a. 与启动时过程相同。
​    5. 改变服务器的状态。与启动时过程相同。

##### 5.3 综合来讲

1. 所有在集群中的server都会推荐自己为leader，然后把（myid、zxid、epoch）作为广播信息，广播给集群中的其他server，然后等待其他服务器返回。

2. 每个服务器都会接收来自集群中的其他服务器的投票。集群中的每个服务器在接受到投票后，开始判断投票的有效性：

3. 1. 判断逻辑时钟(Epoch) ，如果Epoch大于自己当前的Epoch，说明自己保存的Epoch是过期。更新Epoch，同时clear其他服务器发送过来的选举数据。判断是否需要更新当前自己的选举情况。

   2. 1. 首先判断看数据的id，数据id大的胜出
      2. 其次判断leaderId，leaderId大的胜出

   3. 如果Epoch小于目前的Epoch，说明对方的epoch过期了，也就意味着对方服务器的选举轮数是过期的。这个时候，只需要讲自己的信息发送给对方。

   4. 两边的逻辑时钟相同,此时也只是调用totalOrderPredicate函数判断是否需要更新本机的数据,如果更新了再将自己最新的选举结果广播出去就是了。

4. 然后再处理两种情况

5. 1. 服务器判断是不是已经收集到了所有服务器的选举状态,如果是，那么这台服务器选举的leader就定下来了，然后根据选举结果设置自己的角色(FOLLOWING还是LEADER),然后退出选举过程就是了。
   2. 即使没有收集到所有服务器的选举状态,也可以根据该节点上选择的最新的leader是不是得到了超过半数以上服务器的支持,如果是,那么当前线程将被阻塞等待一段时间(这个时间在finalizeWait定义)看看是不是还会收到当前leader的数据更优的leader,如果经过一段时间还没有这个新的leader提出来，那么这台服务器最终的leader就确定了,否则进行下一次选举。

##### 5.4 leader 选举源码分析（需要过后整理下）

TUDO

### 七 zookeeper 的实践

~~~java
详细信息看项目中的代码

curator官网：http://curator.apache.org/

实现带注册中心的RPC框架
https://github.com/wolfJava/rmi
~~~

### 八 Watcher 机制与原理

#### 1 EventType

~~~java
None(-1) 客户端与服务器端成功建立会话
NodeCreated(1)  节点创建
NodeDeleted(2)  节点删除
NodeDataChanged(3) 数据变更：数据内容
NodeChildrenChanged(4) 子节点发生变更： 子节点删除、新增的时候，才会触发
~~~

#### 2 watcher的特性

当数据发生变化的时候，zookeeper会产生一个watcher事件，并且会发送到客户端。但是客户端只会收到一次通知。如果后续这个节点再次发生变化，那么之前设置watcher的客户端不会再次收到信息。（watcher是一次性的操作）。可以通过循环监听去达到永久监听效果。

zkClient （ 永久监听的封装）

curator （ 永久监听的封装）

java api的话， zk.exists , zk.getData  创建一个watcher监听

zookeeper序列化使用的是Jute

#### 3 如何注册事件机制

通过三个操作来绑定事件：getData、Exists、getChildren

如何触发事件：凡是事务类型的操作，都会触发监听时间。create/delete/setData

详细看代码

#### 4 什么样的操作会产生什么类型的事件

|                            | zk-persist-mic（监听事件）exists、getData、getChildren | zk-persist-mic/child（监听事件） |
| -------------------------- | ------------------------------------------------------ | -------------------------------- |
| create("/point")           | NodeCreated(exists、getData)                           | 无                               |
| delete("/point")           | NodeDeleted(exists、getData)                           | 无                               |
| setData("/point")          | NodeDataChanged(exists、getData)                       | 无                               |
| create("/point/children")  | NodeChildrenChanged(getChildren)                       | NodeCreated                      |
| delete("/point/children")  | NodeChildrenChanged(getChildren)                       | NodeDeleted                      |
| setData("/point/children") |                                                        | NodeDataChanged                  |

### 九 zookeeper 的集群监控

<https://www.cnblogs.com/linuxbug/p/4840506.html>





**有时间需要看下源码，重中之重**









