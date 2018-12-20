## 初识 ElasticSearch

ELK，无需代码侵入，无需编码，需要你的系统在设计的时候有日志

Solr：实时性不高
ElasticSearch：实时性更高的索引引擎

Java非常重要的搜索引擎鼻祖 Lucene
Lucene  Java第一代搜索引擎的核心，Java搜索引擎解决方案最先发起者

- Lucene 的工作原理

1. Lucene 是一个 JAVA 搜索类库，它本身并不是一个完整的解决方案，需要额外的开发工作。
2. Document文档存储、文本搜索。
3. Index索引，聚合检索。
4. Analyzer分词器，如IKAnalyzer、word分词、Ansj、Stanford等
5. 大数据搜索引擎解决方案原理
6. NoSQL的兴起（Redis、MongoDB、Memecache）

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-elasticsearch/img/es-base-1.jpg?raw=true)





### 一 版本选择及分布式环境搭建

### 1 版本问题

因为 ElasticSearch 是 ELK 组合中的一部分，都是 Elastic 产品中的组成部分，在 ElasticSearch 2.x 以前，ELK 中的各个中间件的版本不一致，如：ElasticSearch 2.3.4，而 Kibana 对应的版本是 4.5.3。2016 年秋季，为了方便各中间件方便配合使用，ElasticSearch 直接从 2.x 升级到了 5.x，保持了和各个中间件版本一致。 

因此，ElasticSearch 的版本历史是:1.x -> 2.x -> 5.x。 

ElasticSearch 5.5.x 相对以前的 2.x 版本，是基于 Lucene 6 来构建的，它增加了 36%的查询速度，增加了 71% 的索引速度，并且减少了 66% 的硬盘空间占用，还减少了 85%的内存使用，同时还新增 IP 字段，以支持 IP4 和 IP6，在各方面超越了以往的历史版本。

### 2 下载和安装

打开网址:https://www.elastic.co/downloads/past-releases，选择 Elasticsearch 6.5.1 版本，获取下载链接。大家在学习过程中，尽量保持版本一致。

下载之后，直接解压即可。运行 bin 目录下的 elasticsearch.bat 文件(linux 下需要在 root 用户下运行 elasticsearch 文件)，出现如下界面即启动成功。http://127.0.0.1:9200

~~~json
访问：http://127.0.0.1:9200 出现以下提示则证明安装成功
{
  "name" : "EOy0Ku0",
  "cluster_name" : "elasticsearch",
  "cluster_uuid" : "I4XefUzsQIehY7a_g6Bv3A",
  "version" : {
    "number" : "6.5.1",
    "build_flavor" : "default",
    "build_type" : "tar",
    "build_hash" : "8c58350",
    "build_date" : "2018-11-16T02:22:42.182257Z",
    "build_snapshot" : false,
    "lucene_version" : "7.5.0",
    "minimum_wire_compatibility_version" : "5.6.0",
    "minimum_index_compatibility_version" : "5.0.0"
  },
  "tagline" : "You Know, for Search"
}
~~~

#### 2.1 在 CentOS 中安装

##### 2.1.1 安装步骤

1. 下载安装包到服务器上 wget https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-6.5.1.tar.gz
2. 解压安装包：tar  -zxvf elasticsearch-6.5.1.tar.gz
3. 修改文件名称：mv  elasticsearch-6.5.1  elastic
4. 创建ES用户和组（因为ES不允许使用root用户执行）

~~~nginx
[root@localhost /]#    groupadd es                                       // 添加用户组，组名：es
[root@localhost /]#    useradd es -g es                                  // 添加用户，格式： 用户名 -g 组名
[root@localhost /]#    chown -R es:es  /opt/seesun2012/elastic           // 设置目录权限
[root@localhost /]#    passwd es                                         // 设置es用户登陆密码（提示过于简单继续确认）
~~~

5. 修改ES配置文件，执行`vi /opt/seesun2012/elastic/config/elasticsearch.yml`命令

~~~nginx
#network.host: 192.168.0.1
network.host: 39.107.31.208
~~~

6. 启动ES文件，cd 进入到 `/data/elastic/` 

~~~nginx
[root@localhost seesun2012]#./bin/elasticsearch -d   //  启动ES服务，-d 代表后台启动
~~~

7. 查看ES动态日志

~~~nginx
[root@localhost elastic]#tail -f /opt/seesun2012/elastic/logs/elasticsearch.log
~~~

8. 访问：http://39.107.31.208:9200/ 出现json信息则安装成功

##### 2.1.2 错误&解决方案

1. **max file descriptors [4096] for elasticsearch process is too low, increase to at least [65536]**

原因： 意思是说你的进程不够用了

解决方案： 切到root 用户：进入到security目录下的limits.conf；执行命令 `vim /etc/security/limits.conf` 在文件的末尾添加下面的参数值：

~~~conf
* soft nofile 65536
* hard nofile 131072
* soft nproc 2048
* hard nproc 4096
~~~

2. **max number of threads [3803] for user [es] is too low, increase to at least [4096]**

原因：意思就是说你的线程数不够用了

解决方案： 切到root 用户：执行命令 `vi /etc/security/limits.d/20-nproc.conf` 修改3803为4096：

~~~conf
*          soft    nproc     4096
root       soft    nproc     unlimited
~~~

如果还是失败（`大多出现在 Centos7 以上`），换下面这种：

~~~conf
* hard nproc 4096
* soft nproc 4096
elk soft nproc 4096
root soft nproc unlimited
~~~

3. **max virtual memory areas vm.max_map_count [65530] is too low, increase to at least [262144]**

原因： 需要修改系统变量的最大值

解决方案：切换到 root 用户修改配置 `/etc/sysctl.conf` 增加配置值：`vm.max_map_count=655360`

执行命令 `sysctl -p` 这样就可以了，会显示如下信息

~~~nginx
[root@localhost ~]#    sysctl -p
vm.max_map_count = 262144
~~~

4. **system call filters failed to install; check the logs and fix your configuration or disable system call filters at your own risk**

问题原因：因为Centos6不支持SecComp，而ES5.2.1默认bootstrap.system_call_filter为true进行检测，所以导致检测失败，失败后直接导致ES不能启动。详见 ：https://github.com/elastic/elasticsearch/issues/22899

解决方法：在elasticsearch.yml中配置bootstrap.system_call_filter为false，注意要在Memory下面：

~~~conf
bootstrap.memory_lock: false
bootstrap.system_call_filter: false
~~~

#### 2.2 分布式安装

1. 分别在三台机器上下载安装包，并可以独立运行
2. 选择分布式服务器1作为主节点进行配置，打开 elasticsearch-6.5.1-master\config 下的 elasticsearch.yml 文件，在底部追加如下内容:

~~~nginx
cluster.name: tom-test	#集群名称
node.name: master	#节点 ID，保证唯一 
node.master: true	#标记是否为主节点
network.host: 127.0.0.1	#对外公开的 IP 地址，如果自动识别配置为 0.0.0.0
#我的配置如下：
cluster.name: wolfman
node.name: master
node.master: true
network.host: 39.107.31.208
~~~

3. 配置 slave-1 节点，打开 elasticsearch-6.5.1-slave-1\config 下的 elasticsearch.yml 文件，在底部追加如下内容:

~~~nginx
cluster.name: tom-test	#集群名称三个节点保持一致
node.name: slave-1	#从节点 ID，保证唯一
network.host: 127.0.0.1	#对外公开的 IP 地址，如果自动识别配置为 0.0.0.0
http.port: 8200	#默认端口为 9200，因为我的环境是在同一台机器，因此，指定服务端口号
discovery.zen.ping.unicast.hosts: ["127.0.0.1"] #集群的 IP 组，配置主节点 IP 即可
#我的配置如下：
network.host: 39.107.32.43
cluster.name: wolfman
node.name: slave-1
discovery.zen.ping.unicast.hosts: ["39.107.31.208"]

~~~

3. 配置 slave-2 节点，打开 elasticsearch-5.5.1-slave-2\config 下的 elasticsearch.yml 文件，在底部追加如下内容:

~~~nginx
cluster.name: tom-test	#集群名称三个节点保持一致
node.name: slave-2	#从节点 ID，保证唯一
network.host: 127.0.0.1	#对外公开的 IP 地址，如果自动识别配置为 0.0.0.0
http.port: 8000	#默认端口为 9200，因为我的环境是在同一台机器，因此，指定服务端口号
discovery.zen.ping.unicast.hosts: ["127.0.0.1"] #集群的 IP 组，配置主节点 IP 即可
#我的配置如下：
network.host: 47.95.39.176
cluster.name: wolfman
node.name: slave-1
discovery.zen.ping.unicast.hosts: ["39.107.31.208"]
~~~

4. 分别启动三个节点

#### 2.3 可视化插件安装

1. 下载 NodeJS 环境，打开官网 https://nodejs.org/en/download/
2. 安装 NodeJS，检查输入 node -v 检查 node 是否安装成功。

3. 下载 ElasticSearch，打开 https://github.com，搜索 elasticsearch-head 关键字。
4. 搜索结果，选择 mobz/elasticsearch-head
5. 下载 elasticsearch-head-master.zip 包。
6. 修改 master 节点的跨域配置，在 elasticsearch.yml 中追加以下内容。重启所有节点。

~~~nginx
http.cors.enabled: true #允许跨域 
http.cors.allow-origin: "*"
~~~

7. 启动 head 插件

~~~nginx
npm install
npm run start
~~~

8. 输入 http://localhost:9100/，可以看到所有节点的信息。

#### 2.4 Cerebro 的安装

下载地址:https://github.com/lmenezes/cerebro/releases

### 二 基本原理及学习方法论

#### 1 Lucene 工作原理

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-elasticsearch/img/lucene-1.jpg?raw=true)

处理文本的最高效做法就是：正则匹配。

#### 2 es 中的基本概念

索引：含有相同属性的文档集合

类型：索引可以定义一个或多个类型，文档必须属于一个类型

文档：文档是可以被索引的基本数据单元

分片：没饿索引都有多个分片，每个分片是一个Lucene索引

备份：拷贝一份分片就完成了分片的备份

#### 3  es api 命名风格

api 基本格式：http://<ip>:<port>/<索引>/<类型>/<文档 ID>

常用的 HTTP 动词：GET/PUT/POST/DELETE

#### 4 关系型数据库和 ElasticSearch 操作姿势对比

| JDBC 操作                                                    | es client 操作                                               |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| 1、加载驱动类(JDBC 驱动)<br>2、建立连接(Connection)
3、创建语句集(Statement)
4、执行语句集 execute()
5、获取结果集(ResultSet)
6、关闭结果、语句、连接 | ---<br/>1、建立连接(TransportClient)
2、条件构造(SearchRequestBuilder)
3、执行语句 execute()
4、获取结果(SearchResponse)
5、关闭以上操作 |

### 三 es 基本操作

#### 1 创建索引

##### 1.1 创建非结构化的索引

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-elasticsearch/img/es-1.jpg?raw=true)

##### 1.2 创建结构化的索引，输入 book/novel/_mappings

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-elasticsearch/img/es-2.jpg?raw=true)

##### 1.3 可以在 Postman 中选择 PUT 方法，输入 localhost:9200/people，然后在 raw 中编辑一下 json 信息

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-elasticsearch/img/es-3.jpg?raw=true)

输入的 json 内容如下：

~~~txt
{
	"settings":{
		"number_of_shards":3,
        "number_of_replicas":1
    },
    "mappings":{
         "man":{
             "properties":{
                 "name":{ "type":"text" },
                 "conutry":{ "type":"keyword" },
                 "age":{ "type":"integer" },
                 "date":{
                     "type":"date",
                     "format":"yyyy-MM-dd HH:mm:ss ||yyyy-MM-dd||epoch_millis"
                 }
			}
		},
		"woman":{
		} 
	}
}
~~~

#### 2 插入数据

##### 2.1 指定文档ID插入，在 Postman 中使用 PUT 方法，输入 localhost:9200/people/man/1,在 raw 区域输入:

~~~java
{
    "name":"Tom",
    "country":"China",
    "age":18,
    "date":"2000-10-11"
}
~~~

执行结果如下：

~~~java
{
    "_index": "people",
    "_type": "man",
    "_id": "1",
    "_version": 1,
    "result": "created",
    "_shards": {
        "total": 2,
        "successful": 2,
        "failed": 0
    },
    "_seq_no": 0,
    "_primary_term": 1
}
~~~

##### 2.2 自动生成文档 ID 插入，在 Postman 中使用 POST 方法，输入 localhost:9200/people/man，在 raw 区域输入:

~~~java
{
    "name":"Tom 老师", 
    "country":"China", 
    "age":19,
    "date":"1999-10-11"
}
~~~

执行结果如下：

~~~java
{
    "_index": "people",
    "_type": "man",
    "_id": "0kT3cWcBrugTYmys2xoc",
    "_version": 1,
    "result": "created",
    "_shards": {
        "total": 2,
        "successful": 2,
        "failed": 0
    },
    "_seq_no": 0,
    "_primary_term": 1
}
~~~

#### 3 修改文档

##### 3.1 直接修改文档，打开 Postmain，选择 POST 方法，输入 localhost:9200/people/man/1/_update，运行结果如下:

~~~java
//请求参数：
{
    "doc":{
    	"age":20
    }
}
~~~

~~~java
//响应结果
{
    "_index": "people",
    "_type": "man",
    "_id": "1",
    "_version": 2,
    "result": "updated",
    "_shards": {
        "total": 2,
        "successful": 2,
        "failed": 0
    },
    "_seq_no": 1,
    "_primary_term": 1
}
~~~

##### 3.2 通过脚本修改文档，在 raw 区输入以下内容:

所有年龄增加一岁。

~~~java
{
    "script":{
    	"lang":"painless",
        "inline":"ctx._source.age += 1"
    }
}
~~~

或者输入：修改年龄为30岁

~~~java
{
	"script":{
        "lang":"painless",
        "inline":"ctx._source.age = params.age",
        "params":{
            "age":30 
        }
	} 
}
~~~

都可以得到以下结果：

~~~java
{
    "_index": "people",
    "_type": "man",
    "_id": "1",
    "_version": 3,
    "result": "updated",
    "_shards": {
        "total": 2,
        "successful": 2,
        "failed": 0
    },
    "_seq_no": 2,
    "_primary_term": 1
}
~~~

#### 4 删除文档

##### 4.1 删除文档

打开 Postman，选择 DELETE 方法，输入 localhost:9200/people/man/1，执行结果如下:

~~~java
{
    "_index": "people",
    "_type": "man",
    "_id": "1",
    "_version": 4,
    "result": "deleted",
    "_shards": {
        "total": 2,
        "successful": 2,
        "failed": 0
    },
    "_seq_no": 3,
    "_primary_term": 1
}
~~~

##### 4.2 删除索引

打开 Postman，选择 DELETE 方法，输入 localhost:9200/people，执行结果如下:

~~~java
{
    "acknowledged": true
}
~~~

#### 5 查询方法

##### 5.1 全表查询

全表查询:在 Postman 中选择 GET 方法，输入 localhost:9200/book/_search 得到以下结果:

~~~java
{
    "took": 10,
    "timed_out": false,
    "_shards": {
        "total": 5,
        "successful": 5,
        "skipped": 0,
        "failed": 0
    },
    "hits": {
        "total": 4,
        "max_score": 1,
        "hits": [
            {
                "_index": "people",
                "_type": "man",
                "_id": "00QRcmcBrugTYmysOBq7",
                "_score": 1,
                "_source": {
                    "name": "huhao",
                    "country": "China",
                    "age": 27,
                    "date": "2060-10-11"
                }
            },
			...
            ,
            {
                "_index": "people",
                "_type": "man",
                "_id": "1UQRcmcBrugTYmysYRpx",
                "_score": 1,
                "_source": {
                    "name": "张龙",
                    "country": "China",
                    "age": 35,
                    "date": "2000-10-11"
                }
            }
        ]
    }
}
~~~

##### 5.2 条件查询

在 Postman 中选择 GET 方法，输入 localhost:9200/people/_search，然后在 raw 区域中编辑如下内容:
查询书籍中包含 ElasticSearch 关键字，且按发版日期降序排序。

~~~java
//请求参数，查询名称包含 huhao 的数据，按照年龄降序
{
	"query":{
        "match":{
             "name":"huhao"
        }
	},
    "sort":[{
    	"age":{"order":"desc"}
    	
    }]
}
~~~

~~~java
//响应结果
{
    "took": 19,
    "timed_out": false,
    "_shards": {
        "total": 5,
        "successful": 5,
        "skipped": 0,
        "failed": 0
    },
    "hits": {
        "total": 1,
        "max_score": null,
        "hits": [
            {
                "_index": "people",
                "_type": "man",
                "_id": "00QRcmcBrugTYmysOBq7",
                "_score": null,
                "_source": {
                    "name": "huhao",
                    "country": "China",
                    "age": 27,
                    "date": "2060-10-11"
                },
                "sort": [
                    27
                ]
            }
        ]
    }
}
~~~

##### 5.3 聚合查询

在 Postman 中选择 GET 方法，输入 localhost:9200/people/_search，然后在 raw 区域中编辑如下内容:
根据书籍字数和发版日期进行分组

~~~java
{
    "aggs":{
        "group_by_word_count":{
             "terms":{
                 "field":"word_count"
             }
        },
        "group_by_publish_date":{
             "terms":{
                 "field":"publish_date"
             } 
        }
    } 
}
~~~

##### 5.4 聚合统计

在 Postman 中选择 GET 方法，输入 localhost:9200/book/_search，然后在 raw 区域中编辑如下内容:
根据书籍的字数进行聚合统计。

~~~java
//请求参数，
{
	"aggs":{
        "grades_word_count":{
             "stats":{ "field":"word_count" }
        } 
    }
}
~~~

### 四 es 高级查询

#### 1 query 条件

##### 1.1 匹配模糊

在 Postman 中选择 GET 方法，输入 localhost:9200/book/_search，然后在 raw 区域中编辑如下内容:

~~~java
//查询标题中包含”ElasticSearch”和”入门”关键字的书籍
{
    "query":{
        "match":{ "title":"ElasticSearch 入门"} 
    }
}
~~~

##### 1.2  习语匹配

~~~java
//查询标题中包含”ElasticSearch”的书籍
{
    "query":{
        "match_phrase":{
             "title":"ElasticSearch"
        }
    }
}
~~~

##### 1.3 多字段匹配

~~~java
//查询作者和标题中都包含”Tom”的书籍
{
    "query":{
        "multi_match":{
             "query":"Tom",
             "fields":["author","title"]
        }
	} 
}
~~~

##### 1.4 Query 语法查询

~~~java
//查询标题和作者中同时包含 ElasticSearch 和大法，或者包含 Python 的书籍。
{
    "query":{
        "query_string":{
            "query":"(ElasticSearch AND 大法) OR Python", 
            "fields":["title","author"]
        } 
    }
}
~~~

##### 1.5 结构化数据查询

~~~java
//查询字数在 1000 到 2000 之间的数据
{
    "query":{
        "range":{
             "word_count":{
                 "gt":1000,
                 "lte":2000
             }
        } 
    }
}
~~~

```java
//查询 2018-01-01 至今发版的所有书籍
{
    "query":{
         "range":{
             "publish_date":{
                 "gt":"2018-01-01",
                 "lte":"now"
             }
         } 
    }
}
```

#### 2 filter 条件

~~~java
{
"query":{
        "bool":{
             "filter":{
                 "term":{
                     "word_count":1000
                 }
             }
        } 
	}
}
~~~

#### 3 复合查询

~~~java
{
    "query":{
         "bool":{
             "must":[{
                 "match":{
                     "title":"ElasticSearch"
                 }
             }, {
                 "match":{
                     "author":"Tom"
                 }
             } ],
             "filter":{
                 "term":{
                     "word_count":3000
                 }
             } 
         }
	}
}
~~~

### 五 es 与 spring api 集成

**详细请看代码**









