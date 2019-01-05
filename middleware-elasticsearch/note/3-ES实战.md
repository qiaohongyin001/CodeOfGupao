## ES实战

### 一 ElasticSearch 与 Spring 集成

#### 1 关系型数据库和 ES 操作姿势对比

| 关系型数据库         | ElasticSearch      |
| -------------------- | ------------------ |
| 建库（DB）           | 建库（Index）      |
| 建表（Table）        | 建表（Index Type） |
| 键约束（Constraint） | 主键（ID）         |

| JDBC操作                | ES Client 操作                   |
| ----------------------- | -------------------------------- |
| 1.加载驱动类(JDBC驱动)  | ----                             |
| 2.建立连接(Connection)  | 1.建立连接(TransportClient)      |
| 3.创建语句集(Statement) | 2.条件构造(SearchRequestBuilder) |
| 4.执行语句集 execute()  | 3.执行语句 execute()             |
| 5.获取结果集(ResultSet) | 4.获取结果(SearchResponse)       |
| 6.关闭结果、语句、连接  | 关闭以上操作                     |

示例：

我们来模拟微信摇一摇的功能，从**10W条记录(大数据)**中**快速( < 0.1s)**搜索到**附近( 排序算法 )**的人

**具体代码详见 es-spring-demo**

### 二 分布式带来的变革

问题主要为：多节点、日志分散、运维成本高

### 三 Logstash

Logstash 是开源的服务器端数据处理管道，能够同时从多个来源采集数据，转换数据，然后将数据发送到您最喜欢的 “存储库” 中。（我们的存储库当然是 Elasticsearch。）

#### 1 执行流程

Input（数据采集）→ Filter（数据解析/转换）→ Output（数据输出）

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-elasticsearch/img/logstash-1.jpg?raw=true)

#### 2 必须明白的概念

Pipeline：

​	input-filter-output的3个阶段处理流程

​	队列管理

​	插件生命周期管理	

Logstash Event：

​	内部流转的数据表现形式

​	原始数据在input被转换为Event，在output event被转换为目标格式数据

​	在配置文件中可以对Event中的属性进行增删改查

#### 3 Input Plugin

input 插件可以指定数据输入源，一个 pipeline 可以有多个 input 插件，我们简单几个常用插件的作用：

- stdin 控制台标准输入
- file 文件输入

- tcp TCP输入

- http 通过HTTP协议输入

每个插件的属性都有差异，详情查看官方文档。

#### 4 Codec Plugin

Codec(Code Decode) Plugin作用于 input 和 output plugin，负责将数据在原始与 Logstash 之间转换，常见的codec 有：

- plain 读取原始内容
- dots 将内容简化为点进行输出
- rubydebug 将内容按照ruby格式输出，方便调试 line 处理带有换行符的内容
- json 处理json格式的内容
- multiline 处理多行数据的内容 

#### 5 Filter Plugin

Filter 是 Logstash 功能强大的主要原因，它可以对数据内容进行丰富的处理，比如解析数据、 删除字段、类型转换等等，常见的有如下几个：

- date 日期解析
- grok 正则匹配解析
- dissect 分隔符解析
- mutate 对字段作处理，比如重命名、删除、替换等操作 json 按照json格式解析字段内容到指定字段中
- geoip 增加地理位置数据
- ruby 利用ruby代码来动态修改数据内容

### 四 现场搭建流量监控平台

**具体详见代码**

### 五 ELK 的一般部署方案

Logstash → ElasticSearch → Kibana

这种结构因为需要在各个服务器上部署 Logstash，而它比较消耗CPU 和内存资源，所以比较适合计算资源比较丰富的服务器，否则容易造成服务器性能下降，甚至可能导致无法正常工作，这是不可忍受的。 所以!我们需要一个资源消耗低，效率还不错的日志采集工具——Filebeat介绍。

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-elasticsearch/img/elk-1.jpg?raw=true)

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-elasticsearch/img/elk-2.jpg?raw=true)

最后从Kibana上看可以发现：日志有延时，缺失

如果解决这个问题？提升采集效率才是王道

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-elasticsearch/img/elk-3.jpg?raw=true)

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-elasticsearch/img/elk-4.jpg?raw=true)

数据吞吐量持续增加怎么办?

- ELK同步的采集机制
- 异步化 
- Filebeat支持异步化
- 引入消息队列机制 如:Kafka 

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-elasticsearch/img/elk-5.jpg?raw=true)







