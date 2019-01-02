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

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-elasticsearch/img/cluster-8.jpg?raw=true)

#### 2 数据扩容

如何将数据分布到所有节点上？引入分片(Shard)解决问题 

分片是 es 支持 PB 级数据的基石

- 分片存储了部分数据，可以分布于任意节点上 
- 分片数在索引创建时指定且后续不允许再更改，默认为5个 
- 分片有主分片和副本分片之分，以实现数据的高可用 
- 副本分片的数据由主分片同步，可以有多个，从而提高读取的吞吐量 

#### 3 分片

下图演示的是3个节点的集群中test_index的分片分布情况，创建时我们指定了3个分片和1个副本。

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-elasticsearch/img/cluster-9.jpg?raw=true)

**两个问题：**

- 此时增加节点是否能够提高test_index的数据容量吗？

不能，因为只有3个分片，已经分布到3个节点上，新增的节点无法利用 

- 此时增加副本数是否能提高test_index的读取吞吐量呢? 

不能，因为新增的副本是分布在3个节点上，还是利用了同样的资源， 如果要增加吞吐量，还需要增加节点。 

**如何解决这两个问题：**

分片数的设定非常重要，需要提前规划好：

- 分片数太少，导致后续无法通过增加节点实现水平扩容
- 分片数过大，导致一个节点上分布多个分片，造成资源浪费，同时会影响查询性能

### 三 集群状态

#### 1 Cluster Health

通过如下api可以查看集群健康状况，包括以下三种：

- green 健康状态，指所有主副分片都正常分配
- yellow 指所有主分片都正常分配，但是有副本分片未正常分配 
- red 有主分片未分配 

三种状态只是代表分片的工作状态，并不是代表整个es集群是否能够对外提供服务 

GET _cluster/health 

### 四 故障转移

- 集群由3个节点组成，如下所示，此时集群状态是green

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-elasticsearch/img/cluster-10.jpg?raw=true)

- node1 所在机器宕机导致服务终止，此时集群会如何处理？

node2 和 node3 发现 node1 无法响应一段时间后会发起 master 选举，比如这里选举node2为master节点，此时由于主分片P0下线，集群状态变为red。

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-elasticsearch/img/cluster-11.jpg?raw=true)

node2 发现主分片 P0 未分配，将R0提升为主分片。此时由于所有主分片都正常分配， 集群状态变为yellow。 

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-elasticsearch/img/cluster-12.jpg?raw=true)

node2 发现主分片 P0 和 P1 生成新的副本，集群状态变为green。

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-elasticsearch/img/cluster-13.jpg?raw=true)

### 五 脑裂问题

脑裂问题，英文为split-brain，是分布式系统中的经典网络问题，如下图所示：

​	node2 与 node3 会重新选举 master，比如 node2 成为了新 master，此时会更新 cluster state node1 自己组成集群后，也会更新 cluster state。

​	同一个集群有两个 master，而维护不同的 cluster state，网络恢复后无法选择正确的 master。

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-elasticsearch/img/cluster-14.jpg?raw=true)

解决方案为仅在可选举 master-eligible 节点数据大于等于 quorum 时才可以进行master选举。

quorum = master-eligible 节点数/2 + 1，例如 3个master-eligible节点时，quorum为2。 

解决：**discovery.zen.mininum_master_nodes** 为 **quorum** 即可避免脑裂。

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-elasticsearch/img/cluster-15.jpg?raw=true)

### 六 分布式存储

#### 1 分布式文件存储

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-elasticsearch/img/cluster-16.jpg?raw=true)

Doc1是如何存储到分片 P1 的呢？选择 P1 的依据是什么呢？ 

需要文档到分片的映射算法

目的：使得文档均匀分布在所有分片上，以充分利用资源

算法：

​	随机选择或者 round-robin 算法。

​	不可取：因为维护文档到分片的映射关系，成本巨大

#### 2 文档到分片的映射算法

es 通过如下公司计算文档对应的分片：

~~~java
shard = hash(routing) % number_of_primary_shards
hash 算法保证可以将数据均匀地分散在分片中
routing是一个关键参数，默认是文档id，也可以自行指定
number_of_primary_shards 是主分片数
~~~

该算法与主分片数相关，这也是**分片数一旦确定后便不能更改**的根本原因

保证数据能够均匀分配各个节点上，就不会产生数据热点问题。

#### 3 文档创建的流程

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-elasticsearch/img/cluster-17.jpg?raw=true)

#### 4 文档读取的流程

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-elasticsearch/img/cluster-18.jpg?raw=true)

### 七 shard分片

#### 1 倒排索引不可变更

倒排索引一旦生成，不能更改。
有如下好处:
​	不用考虑并发写文件的问题，杜绝了锁机制带来的性能问题。
 	由于文件不再更改，可以充分利用文件系统缓存，只需要载入一次，只要内存足够，对该文件的读取都会从内存读取，性能高。
 	利于生成缓存数据。
 	利于对文件进行压缩存储，节省磁盘和内存存储空间
坏处:
 	写入新文档时，必须重新构建倒排索引文件，然后替换老文件后，新文档才能被检索，导致文档实时性受到影响。

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-elasticsearch/img/cluster-19.jpg?raw=true)

#### 2 文档搜索实时性

**解决方案:** 新文档直接生成新的倒排索引文件，查询的时候同时查询所有的倒排文件，然后对查询结果做汇总计算即可。

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-elasticsearch/img/cluster-20.jpg?raw=true)

Lucene采用了这种方案，它构建的单个倒排索引称为segment，合在一起称为Index， 与 ES 中的Index概念不同。ES 中的一个 Shard 对应一个 Lucene Index。 

Lucene 会有一个专门的文件来记录所有的 segment 信息，称为Commit Point。

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-elasticsearch/img/cluster-21.jpg?raw=true)

#### 2 文档搜索实时性 - refresh

segment 写入磁盘的过程依然很耗时，可以借助文件系统缓存的特性，先将segment在缓存中，创建并开放查询来进一步提升实时性，该过程在 es 中被称为 refresh。  

在 refresh 之前文档会先存储在一个 buffer 中，refresh 时将 buffer 中的所有文档清空并生成segment， es 默认每1秒执行一次refresh，因此文档的实时性被提高到1秒，这也是es被称为近实时(Near Real Time)的真正原因 

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-elasticsearch/img/cluster-22.jpg?raw=true)

#### 3 文档搜索实时性 - translog

如果在内存中的 segment 还没有写入磁盘前发生了宕机，那么内存中的文档就无法恢复了。 那么如何解决这个问题呢？

es 引入 translog 机制。写入文档到buffer时，同时将该操作写入translog。

translog 文件会即时写入磁盘(fsync)，6.x默认每个请求都会落盘，可以修改为每5秒写一次， 这样风险便是丢失5秒内的数据，相关配置为index.translog.* 

es重新启动时会自动检查translog文件，并从中恢复数据。

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-elasticsearch/img/cluster-23.jpg?raw=true)

#### 4 文档搜索实时性 - flush  - reflush

**flush** 负责将内存中的segment写入磁盘，主要做如下的工作:
​	将translog写入磁盘。
​	将index buffer清空，其中的文档生成一个新的segment，相当于一个refrsh操作。
​	更新commit point并写入磁盘
​	执行fsync操作，将内存中的segment写入磁盘。
​	删除旧的translog文件

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-elasticsearch/img/cluster-24.jpg?raw=true)

**refresh** 发生的时机主要有以下几种情况：

​	间隔时间达到时，通过index.settings.refresh_interval来设定，默认是1秒 

​	index.buffer占满时，其大小通过indices.memory.index_buffer_size设置， 默认为jvm heap的10%，所有shard共享 

​	flush发生时也会发生refresh 

**flush** 发生的时机主要有以下几种情况：

​	间隔时间达到时，默认是30分钟，5.x之前可以通过index.translog.flush_threshold_period修改，之后发布的版本无法设置。

​	translog占满时，其大小可以通过index.translog.flush_threshold_size控制，默认是512MB 每个index有自己的translog。

#### 5 文档搜索实时性 - 删除与更新

segment 一旦生成就不能更改，那么如果要删除文档该如何操作？

​	lucene会专门维护一个.del的文件，记录所有已经删除的文档， 注意.del上记录的是文档在Lucene的内部id。

​	在查询结果返回前会过滤掉.del中所有的文档 

更新文档如何进行呢? 

​	首先删除文档，然后再创建新的文档。

ES索引是一个增量索引。

每操作一次，每个DOC都会维护一个自己的版本号，自增，查询结果只取最大的版本号。

#### 6 整体视角

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-elasticsearch/img/cluster-25.jpg?raw=true)

#### 7 Segment Merging

随着 segment 的增多，由于一次查询的 segment 数增多，查询速度会变慢。

es 会定时在后台进行 segment merge 的操作，减少segment的数量。

通过 force_merge api 可以手动强制做 segment merge 的操作。

### 八 Search的运行机制

Search执行的时候实际分两个步骤运行的：

- Query阶段 

- Fetch阶段

 Query-Then-Fetch 

#### 1 Search 的运行机制 -Query阶段

- node3 在接收到用户的search请求后，先会进行 Query 阶段(此时Coordinating Node角色)。
- node3 在6个主副分片中随机选择3个分片，发送search request。
- 被选中的3个分片会分别执行查询并排序，返回from+size个文档Id和排序值

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-elasticsearch/img/cluster-26.jpg?raw=true)

#### 2 Search 的运行机制 -Fetch阶段

node3 根据 Query 阶段获取到文档Id列表对应的shard上获取文档详情数据：

- node3 向相关的分片发送 multi_get 请求
- 3个分片返回文档详细数据
- node3拼接返回的结果并返回给用户

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-elasticsearch/img/cluster-27.jpg?raw=true)

### 九 相关性算分

相关性算分在 shard 与 shard 间是相互独立的，也就意味着同一个 Tearm 的 IDF 等值在不同 shard 上是不同的。文档的相关性算法和它所处的 shard 相关在文档数量不多时，会导致相关性算分严重不准的情况发生。

解决思路有两个：

- 设置分片数为1个，从根本上排除问题，在文档数量不多的时候可以考虑该方案，比如百万到千万级别的文档数量，一个分片是搞不定的
- 采用DFS Query-then-Fetch的查询方式 

DFS Query-then-Fetch 是拿到所有文档后再重新完整的计算一次相关性算法，耗费更多的cpu和内存，执行性能也比较低下，一般不建议使用。操作方式如下:

~~~java
GET test_search_relevace/_search?search_type=dfs_query_then_fetch
{
	"query":{
    	"match":{
			"name":"hello"    
    	}
	}
}
~~~

设计业务的时候，就要考虑到数据使用场景。

分片数：决定了后边的数据扩容。

节点数：吞吐量。

reindex：重新再创建一个index，把原来数据导入到新的index，在把老的index删掉。

关键在于设计。

原理大同小异，套路都差不多。

就是API不一样了。

评分标准：

​	如果是text，就是单词出现频率来评分。

​	匹配度，完整度越高评分就越高。



