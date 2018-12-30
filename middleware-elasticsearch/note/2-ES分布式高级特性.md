## ES 分布式高级特性

### 一 分布式特性

#### 1 分布式特性

ES 支持集群模式，是一个分布式系统，其好处主要有两个：增大系统容量，如内存，磁盘，使得 ES 集群可以支持PB级的数据，提高系统可用性，即使部分节点停止服务，整个集群依然可以正常服务。

ES 集群由多个 ES 实例组成不同集群通过集群名字来区分，可通过**cluster.name**来进行修改，默认 **elasticsearch**，每个 ES 实例本质上是一个 JVM 进程，且有自己的名字，可以通过**node.name**来进行修改。 

可视化插件：

- Elasticsearch-head :https://github.com/mobz/elasticsearch-head

- Cerebro:https://github.com/lmenezes/cerebro/releases 

#### 2 节点启动

运行如下命令可以快速启动一个es节点的实例：

~~~nginx
bin/elasticsearch -Ecluster.name=my_cluster -Epath.data=my_cluster_node1 -Enode.name=node1 -Ehttp.port=5100 -d
~~~

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-elasticsearch/img/cluster-1.jpg?raw=true)

#### 3 Cluster State

ES 集群相关的数据称为 cluster state，主要记录如下信息：

- 节点信息，比如节点名称、链接地址等
- 索引信息，比如索引名称、配置等
- ......

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-elasticsearch/img/cluster-2.jpg?raw=true)

#### 4 Master Node

1. 可以修改cluster state的节点称为master节点，一个集群只能有一个。
2. cluster state 存储在每个节点上，master 维护最新版本并同步给其他节点。
3. master 节点是通过集群中所有节点选取产生的，可以被被选举的节点称为master-eligible节点
   ，相关配置:node.master:true。

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-elasticsearch/img/cluster-3.jpg?raw=true)

#### 5 创建一个索引

我们通过如下 api 创建一个索引：`PUT test_index` 

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-elasticsearch/img/cluster-4.jpg?raw=true)

#### 6 处理请求 coordinating Node（协调节点）

处理请求的节点称为 **coordinating** 节点，该节点为所有节点的默认角色，不能取消，路由请求到正确的节点处理，比如创建索引的请求到 master 节点。

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-elasticsearch/img/cluster-5.jpg?raw=true)

#### 7 数据节点 Data Node

存储数据的节点称为 data 节点，默认节点都是 data 类型，相关配置如下：`node.data:true`

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-elasticsearch/img/cluster-6.jpg?raw=true)

#### 8 单点问题 - 也就是一个服务器启动

如果 node1 停止服务，集群就停止服务

#### 9 新增节点

执行节点启动命令，创建集群组

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-elasticsearch/img/cluster-7.jpg?raw=true)

### 二 副本与分片

#### 1 提高系统可用性

如下图所示，node2上是test_index的副本

![](8)

#### 2 数据扩容

如何将数据分布到所有节点上？引入分片(Shard)解决问题 

分片是 es 支持 PB 级数据的基石

- 分片存储了部分数据，可以分布于任意节点上 
- 分片数在索引创建时指定且后续不允许再更改，默认为5个 
- 分片有主分片和副本分片之分，以实现数据的高可用 
- 副本分片的数据由主分片同步，可以有多个，从而提高读取的吞吐量 

#### 3 分片

下图演示的是3个节点的集群中test_index的分片分布情况，创建时我们指定了3个分片和1个副本。

![](9)

**两个问题：**

- 此时增加节点是否能够提高test_index的数据容量吗？

不能，因为只有3个分片，已经分布到3个节点上，新增的节点无法利用 

- 此时增加副本数是否能提高test_index的读取吞吐量呢? 

不能，因为新增的副本是分布在3个节点上，还是利用了同样的资源， 如果要增加吞吐量，还需要增加节点。 

**如何解决这两个问题：**

分片数的设定非常重要，需要提前规划好：

- 分片数太少，导致后续无法通过增加节点实现水平扩容
- 分片数过大，导致一个节点上分布多个分片，造成资源浪费，同时会影响查询性能













