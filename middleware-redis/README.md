## 分布式缓存技术 - redis 

### 一 初识 redis

#### 1 redis 存储结构

redis 全程 remote dictionary server（远程字典服务器），他以字典结构存储数据，并允许其他应用通过 TCP 协议读取字典中的内容。 

Redis实例 —— 16个DB —— key —— value ，value值分为以下五种数据类型：

- 字符类型（String）
- 散列类型（hash）
- 列表类型（list）
- 集合类型（set）
- 有序集合类型（sorted set）

#### 2 功能

> 可以为每个key设置超时时间
>
> 可以通过列表类型实现分布式队列操作
>
> 支持发布订阅的消息模式

#### 3 应用场景

- 数据缓存（商品数据、新闻、热点数据等。。。）
- 单点登录
- 秒杀、抢购
- 网站访问排名
- 应用的模块开发
- 关注、被关注等等

### 二 redis 的安装配置

**官方网站：**<https://redis.io/>

redis 约定次版本号（第一个小数点后的数字）为偶数版本是稳定版，如2.8、3.0，奇数版本为非稳定版，生产环境需要使用稳定版；目前最新版本为Redis4.0.9。 

#### 1 安装

1. 获得安装包
   1. 官网下载 tar.gz 的安装包
   2. 通过 wget 的方式下载
      1. wget http://download.redis.io/releases/redis-4.0.10.tar.gz 看官网就行
2. 解压安装包
   1. tar -zxvf redis-4.0.1.tar.gz
3. 在redis目录下 执行 make 进行编译安装
4. 可以通过make test 测试编译状态（报错时找到相应解决方案https://blog.csdn.net/zhangshu123321/article/details/51440106）
5. make install [prefix=/path]完成安装
   1. 例如：make install PREFIX=/data/redis/redis
6. 安装后需要把源码的redis.conf拷贝到安装目录下

#### 2 配置

1. 启动停止redis

2. 1. ./redis-server ../redis.conf —— 启动服务

   2. ./redis-cli shutdown —— 关闭服务

   3. ~~~txt
      考虑到redis有可能正在将内存的数据同步到硬盘中，强行终止redis进程可能会导致数据丢失，正确停止redis的方式应该是向Redis发送SHUTDOW命令。
      当redis收到SHUTDOWN命令后，会先断开所有客户端连接，然后根据配置执行持久化，最终完成退出。
      ~~~

3. 以后台进程的方式启动

4. 1. 修改redis.conf daemonize=yes

5. 连接到redis的命令

6. 1. ./redis-cli 访问本地服务
   2. ./redis-cli -h 127.0.0.1 -p 6379 访问远程服务

7. 其他命令说明

8. 1. redis-server —— 启动服务
   2. redis-cli —— 访问到redis的控制台
   3. redis-benchmark —— 性能测试的工具
   4. redis-check-aof —— aof文件进行检测的工具
   5. redis-check-dump —— rdb文件检查工具
   6. redis-sentinel —— sentinel服务器配置

9. 多数据支持

10. 1. 默认支持16个数据库；可以理解为一个命名空间；

   2. 跟关系型数据库不一样的点：

   3. 1. redis不支持自定义数据库名词
      2. 每个数据库不能单独设置授权
      3. 每个数据库之间并不是完全隔离的。可以通过flushall命令清空redis实例里面的所有数据库中的数据。

   4. 通过select dbid去选择不同的数据库命名空间。dbid的取值范围默认是0-15

   5. 1. 例如：select 5

#### 3 卸载

1. 停止redis服务器

2. 1. 通过：ps aux|grep redis 命令来查看服务是否启动
   2. 通过：redis-cli shutdown 命令来停止服务

3. 删除make的时候生成的几个redisXXX的文件

4. 顺便也删除掉解压后的文件目录和所以文件 

### 三 客户端的使用

**官方文档：**<https://redis.io/commands>

#### 1 使用入门

1. 获得一个符合匹配规则的键名列表

~~~java
规则：keys pattern [?/*/[]]
例如：keys mic* , keys mic?
例如：keys mick:hobby
~~~

2. 判断一个键是否存在

~~~java
规则：EXISTS key
结果：(integer) 0 不存在
结果：(integer) 1 存在
~~~

3. type key 去获得这个key的数据结构类型

~~~java
127.0.0.1:6379> type huhao
string
~~~

#### 2 字符类型的使用

1. 一个字符类型的最大容量是512M
2. 赋值

~~~java
127.0.0.1:6379> set xiaohetao 11
OK
~~~

3. 取值

~~~java
127.0.0.1:6379> get xiaohetao
"11"
~~~

4. 递增数据

~~~java
正确：
127.0.0.1:6379> incr age
(integer) 2
错误：
int value = get key; value = value + 1; set key value;
~~~

5. key的设计
   1. 对象类型：对象id：对象属性：对象子属性
   2. 议对key进行分类，同步在wiki统一管理

6. SET key value [EX seconds][PX milliseconds] [NX|XX]

~~~java
EX seconds：设置key的过时时间，单位为秒。
PX milliseconds：设置key的过期时间，单位为毫秒。
NX：（if Not eXist）只有键key不存在的时候才会设置key的值
XX：只有键key存在的时候才会设置key的值
~~~

7. incrby key increment 递增指定的整数

~~~java
127.0.0.1:6379> incrby age 2
(integer) 5
~~~

8. decr key 原子递减

~~~java
127.0.0.1:6379> decr age
(integer) 4
~~~

9. append key value 向指定的key追加字符串

~~~java
127.0.0.1:6379> append huhao 5211314
(integer) 16
~~~

10. strlen key 获得key对应的value的长度

~~~java
127.0.0.1:6379> strlen huhao
(integer) 16
~~~

11. mget key key.. 同时活的多个key的value

~~~java
127.0.0.1:6379> mget huhao xiaohetao
1) "xiaohetao5211314"
2) "11"
~~~

12. mset key value key value key value ... 同时设置多个键值

~~~java
127.0.0.1:6379> mset wdd gjl pts wtj
OK
~~~

13. setnx key value 将key的值设为value，当且仅当key不存在。若给定的 key 已经存在，则setnx不做任何动作。

~~~java
存在键时：
127.0.0.1:6379> setnx huhao 123
(integer) 0
不存在键时：
127.0.0.1:6379> setnx zhaofengwei huhao
(integer) 1
~~~

#### 3 散列类型

1. hash key value 不支持数据类型的嵌套，比较适合存储对象

~~~java
例如：person：age=18 sex=男 name=mic 这样的数据
~~~

2. hset key filed value —— 设置数据

~~~java
127.0.0.1:6379> hset person age 20 name huhao country china
(integer) 3
~~~

3. hget key filed —— 获取数据

~~~java
127.0.0.1:6379> hget person name
"huhao"
~~~

4. hmset key filed value [filed value ...] 一次性设置多个值

~~~java
127.0.0.1:6379> hmset user age 20 name xiaohetao country china
OK
~~~

5. hmget key filed filed ...一次性获得多个值

~~~java
127.0.0.1:6379> hmget user age name
1) "20"
2) "xiaohetao"
~~~

6. hgetall key 获得hash的所有信息，包括key和value

~~~java
127.0.0.1:6379> hgetall user
1) "age"
2) "20"
3) "name"
4) "xiaohetao"
5) "country"
6) "china"
~~~

7. hexists key filed 判断字段是否存在。存在返回1.不存在返回0

~~~~java
字段存在：
127.0.0.1:6379> hexists user age
(integer) 1
字段不存在：
127.0.0.1:6379> hexists user aaa
(integer) 0
~~~~

8. hincryby 原子递增

~~~java
127.0.0.1:6379> hincrby user age 1
(integer) 21
~~~

9. hsetnx 是否存在改属性和值

~~~java
存在属性：
127.0.0.1:6379> hsetnx user age 111
(integer) 0
不存在属性：
127.0.0.1:6379> hsetnx user mobile 13261222781
(integer) 1
~~~

10. hdel key filed [filed...]删除一个或多个字段

~~~java
127.0.0.1:6379> hdel user mobile country
(integer) 2
~~~

#### 4 列表类型

list,可以存储一个有序的字符串列表

1. LPUSH/RPUSH：从左边或者右边push数据

~~~java
规则：LPUSH/RPUSH key value value ...{17 12 20 18 16 13}
127.0.0.1:6379> LPUSH fb sh wj dn tt
(integer) 4
~~~

2. llen num 获得列表长度

~~~java
127.0.0.1:6379> llen fb
(integer) 4
~~~

3. lrange key start stop ;索引可以是负数，-1表示最右边的一个元素

~~~java
127.0.0.1:6379> lrange fb 0 -1 
1) "tt"
2) "dn"
3) "wj"
4) "sh"
~~~

4. lrem key count value 根据参数 COUNT 的值，移除列表中与参数 VALUE 相等的元素。

~~~java
count > 0 : 从表头开始向表尾搜索，移除与 VALUE 相等的元素，数量为 COUNT
count = 0 : 移除表中所有与 VALUE 相等的值。
127.0.0.1:6379> lrem fb 1 tt
(integer) 1
~~~

5. lset key index value 将列表 key 下标为 index 的元素的值设置为 value

~~~java
127.0.0.1:6379> lset fb 2 sj
OK
~~~

6. LPOP/RPOP：从左/从右去掉数据

~~~java
应用场景：可以用来做分布式消息队列
127.0.0.1:6379> LPOP fb
"dn"
127.0.0.1:6379> RPOP fb
"sj"
~~~

#### 5 集合类型

set跟list不一样的点。集合类型不能存在重复的数据，而且是无序的。

1. sadd key member[member...] 增加数据

~~~java
如果value已经存在，则会忽略存在的值，并且返回成功加入的元素的数量
127.0.0.1:6379> sadd score 88 89 92 83 10
(integer) 5
127.0.0.1:6379> sadd score 88 77
(integer) 1
~~~

2. srem key member 删除元素

~~~java
127.0.0.1:6379> srem score 88
(integer) 1
~~~

3. smembers key 获得所有数据

~~~java
127.0.0.1:6379> smembers score
1) "10"
2) "77"
3) "83"
4) "88"
5) "89"
6) "92"
~~~

4. sdiff key key ... 对多个集合执行差集运算

~~~java
127.0.0.1:6379> srem score 88
(integer) 1
127.0.0.1:6379> sadd aa 1 2 3
(integer) 3
127.0.0.1:6379> sadd bb 2 3 4
(integer) 3
127.0.0.1:6379> sdiff aa bb
1) "1"
127.0.0.1:6379> sdiff bb aa
1) "4"
~~~

5. sunion 对多个集合执行并集操作，同时存在在两个集合里的所有制

~~~java
127.0.0.1:6379> sunion aa bb
1) "1"
2) "2"
3) "3"
4) "4"
~~~

#### 6 有序集合

1. zadd key score member

~~~java
127.0.0.1:6379> zadd teacher 10 mic 8 james 12 tom
(integer) 3
~~~

2. zrange key start stop [withscores] 去获得元素

~~~java
withscores是可以获得元素的分数，如果两个元素的score是相同的话，那么根据（0<9<A<Z<a<z）方式从小到大
127.0.0.1:6379> zrange teacher 0 -1 
1) "james"
2) "mic"
3) "tom"
127.0.0.1:6379> zrange teacher 0 -1 withscores
1) "james"
2) "8"
3) "mic"
4) "10"
5) "tom"
6) "12"
    
应用场景：
网站访问的前10名 
~~~

### 四 事务处理、过期时间、发布订阅

#### 1 事务处理

1. MULTI 去开启事务
2. EXEC 去执行事务
3. 有种情况事务不能回滚（运行时错误，不能进行回滚）：

~~~java
127.0.0.1:6379> MULTI
OK
127.0.0.1:6379> set user:name huhao
QUEUED
127.0.0.1:6379> set user:age 20
QUEUED
127.0.0.1:6379> exec
1) OK
2) OK

运行时错误：
127.0.0.1:6379> multi
OK
127.0.0.1:6379> sadd member "huhao" "zhaofengwei"
QUEUED
127.0.0.1:6379> rpush huhao "a" "b"
QUEUED
127.0.0.1:6379> sadd password "111" "222"
QUEUED
127.0.0.1:6379> exec
1) (integer) 2
2) (error) WRONGTYPE Operation against a key holding the wrong kind of value
3) (integer) 2
~~~

#### 2 过期时间

1. expire key seconds
2. ttl 获得key的过期时间：当键不存在时，TTL命令会返回-2，而对于没有给指定键设置过期时间的，通过TTL命令会返回-1
3. 如果想取消键的过期时间设置(使该键恢复成为永久的)，可以使用PERSIST命令，如果该命令执行成功或者成功清除了过期时间，则返回1 。 否则返回0(键不存在或者本身就是永久的)
4. EXPIRE命令的seconds命令必须是整数，所以最小单位是1秒，如果向要更精确的控制键的过期时间可以使用
   PEXPIRE命令，当然实际过程中用秒的单位就够了。 PEXPIRE命令的单位是毫秒。即PEXPIRE key 1000与EXPIRE key 1相等；对应的PTTL以毫秒单位获取键的剩余有效时间。
5. 还有一个针对字符串独有的过期时间设置方式：`setex(String key,int seconds,String value)`

~~~java
127.0.0.1:6379> set aaa bbb
OK
127.0.0.1:6379> expire aaa 5
(integer) 1
127.0.0.1:6379> ttl aaa
(integer) 3
127.0.0.1:6379> get aaa
"bbb"
127.0.0.1:6379> get aaa
(nil)
~~~

##### 2.1 过期删除的原理

Redis 删除失效主键的方法主要有两种:

- 消极方法(passive way)

在主键被访问时如果发现它已经失效，那么就删除它

- 积极方法(active way)

周期性地从设置了失效时间的主键中选择一部分失效的主键删除。

对于那些从未被查询的key，即便它们已经过期，被动方式也无法清除。因此Redis会周期性地随机测试一些key，
已过期的key将会被删掉。Redis每秒会进行10次操作，具体的流程:

1. 随机测试 20 个带有timeout信息的key；
2. 删除其中已经过期的key；
3. 如果超过25%的key被删除，则重复执行步骤1;

这是一个简单的概率算法(trivial probabilistic algorithm)，基于假设我们随机抽取的key代表了全部的key空间。 

#### 3 发布订阅

（外部访问需要修改配置文件：bind绑定ID、protected-mode受保护的）

Redis提供了发布订阅功能，可以用于消息的传输，Redis提供了一组命令可以让开发者实现“发布/订阅”模式
(publish/subscribe) . 该模式同样可以实现进程间的消息传递，它的实现原理是：

发布/订阅模式包含两种角色，分别是发布者和订阅者。订阅者可以订阅一个或多个频道，而发布者可以向指定的
频道发送消息，所有订阅此频道的订阅者都会收到该消息。

发布者发布消息的命令是PUBLISH， 用法是：`PUBLISH channel message`，比如向channel.1发一条消息:hello，`PUBLISH channel.1 “hello”`。

这样就实现了消息的发送，该命令的返回值表示接收到这条消息的订阅者数量。因为在执行这条命令的时候还没有
订阅者订阅该频道，所以返回为0. 另外值得注意的是消息发送出去不会持久化，如果发送之前没有订阅者，那么后续再有订阅者订阅该频道，之前的消息就收不到了。

订阅者订阅消息的命令是：`SUBSCRIBE channel [channel ...]`，该命令同时可以订阅多个频道，比如订阅channel.1的频道。 `SUBSCRIBE channel.1`，执行SUBSCRIBE命令后客户端会进入订阅状态。

channel分两类，一个是普通channel、另一个是pattern channel(规则匹配)， producer1发布了一条消息
【publish abc hello】,redis server发给abc这个普通channel上的所有订阅者，同时abc也匹配上了pattern
channel的名字，所以这条消息也会同时发送给pattern channel *bc上的所有订阅者。

1. publish channel message —— 发布

~~~java
127.0.0.1:6379> publish wjg flesh
(integer) 1
~~~

2. subscribe channel [ …] —— 订阅

~~~java
127.0.0.1:6379> subscribe wjg
Reading messages... (press Ctrl-C to quit)
1) "subscribe"
2) "wjg"
3) (integer) 1
1) "message"
2) "wjg"
3) "reflesh"
~~~

**注意：先启动订阅在进行发布**

### 五 分片策略

codis . twmproxy

<http://blog.51cto.com/quenlang/1636441>

有时间看下

### 六 实现分布式锁

#### 1 锁是来解决什么问题的

1. 一个进程中的多个线程，多个线程并发访问同一个资源的时候，如何解决线程安全问题。
2. 一个分布式架构系统中的两个模块同时去访问一个文件对文件进行读写操作。
3. 多个应用对同一条数据做修改的时候，如何保证数据的安全性。

在单进程中，我们可以用到synchronized、lock之类的同步操作去解决，但是对于分布式架构下多进程的情况下如何做到跨进程的锁。就需要借助一些第三方手段来完成。

#### 2 实现分布式锁的方式

- 数据库可以做 activemq
- 缓存 -redis setnx
- zookeeper

#### 3 分布式锁的实现

分布式锁的解决方案：怎么去获取锁

##### 3.1 数据库实现

~~~java
1.通过唯一约束
lock(
  id  int(11)
  methodName  varchar(100),
  memo varchar(1000)
  modifyTime timestamp
  unique key mn (method)  --唯一约束
)
2.获取锁的伪代码
try{
	exec  insert into lock(methodName,memo) values(‘method’,’desc’);    method
	return true;
}Catch(DuplicateException e){
    return false;
}
3.释放锁
delete from lock where methodName=’’;


存在的需要思考的问题
○ 锁没有失效时间，一旦解决操作失败，就会导致锁记录一直在数据库中，其他线程无法再获得到锁。
○ 锁是非阻塞的，数据的insert操作，一旦插入失败就会直接报错。没有获得锁的线程并不会进入排队队列，再想再次获得锁就要再次触发获得锁操作。
○ 锁事非重入的，同一个线程在没有释放锁之前无法再次获得该锁。
~~~

##### 3.2 zookeeper 实现分布式锁

1. 利用zookeeper的唯一节点特性或者有序临时节点特性获得最小节点作为锁。zookeeper的实现相对简单，通过curator客户端，已经对锁的操作进行了封装。
2. zookeeper的优势
   1. 可靠性高、实现简单
   2. zookeeper因为临时节点的特性，如果因为其他客户端因为异常和zookeeper链接中断了，那么接点会被删除，意味着锁会被自动释放
   3. zookeeper本身提供了一套很好的集群方案，比较稳定
   4. 释放锁操作，会有watch通知机制，也就是服务器端会主动发送消息给客户端这个锁已经被释放了

详细代码见zookeeper

##### 3.3 基于缓存的分布式锁实现

redis中有一个setNx命令，这个命令只有在key不存在的情况下为key设置值。所以可以利用这个特性来实现分布式锁的操作。

### 七 持久化机制

Redis支持两种方式的持久化，一种是RDB方式、另一种是AOF(append-only-file)方式。前者会根据指定的规
则“定时”将内存中的数据存储在硬盘上，而后者在每次执行命令后将命令本身记录下来。两种持久化方式可以单独
使用其中一种，也可以将这两种方式结合使用。

#### 1 RDB持久化策略

RDB的持久化策略：按照规则定时讲内存中的数据同步到地盘

snapshot

redis在指定的情况下会触发快照：

1. 自己配置的快照规则：

2. 1. 在配置文件中修改：

   2. 1. save <seconds> <changes>
      2. save 900 1  当在900秒内被更改的key的数量大于1的时候，就执行快照
      3. save 300 10 当在300秒内被更改的key的数量大于10的时候，就执行快照
      4. save 60 10000 当在60秒内被更改的key的数量大于10000的时候，就执行快照

3. save或者bgsave

4. 1. save：执行内存的数据同步到磁盘的操作，这个操作会阻塞客户端的请求
   2. bgsave：再后台异步执行快照操作，这个操作不会阻塞客户端的请求

5. 执行flushall的时候

6. 1. 清除内存的所有数据，只要快照的规则不为空，也就是第一个规则存在。那么redis会执行快照。

7. 执行复制的时候

##### 1.1 RDB快照的实现原理

1. redis使用fork函数复制一份当前进程的副本（子进程）
2. 父进程继续接收并处理客户端发来的命令，而子进程开始将内存中的数据写入硬盘中的临时文件。
3. 当子进程写入完所有数据后会用该临时文件替换旧的RDB文件，至此，一次快照操作完成。

**注意：**redis再进行快照的过程中，不会修改RDB文件，只有快照结束后才会将旧的文件替换成新的，也就是说任何时候RDB快照都是完整的。这就使得我们可以通过定时备份RDB文件来实现redis数据库的备份，RDB文件是经过压缩的二进制文件，占用的空间会小于内存中的数据包，更加利于传输。

##### 1.2 RDB的优缺点

1. 使用RDB方式实现持久化，一旦redis异常退出，就会丢失最后一次快照以后更改的所有数据。这个时候我们就需要根据具体的应用场景，通过组合设置自动快照条件的方式来将可能发生的数据损失控制在能够接受的范围。如果数据相对来说比较重要，希望将损失降到最小，则可以使用AOF方式进行持久化。
2. RDB可以最大化redis的性能：父进程在保存RDB文件时唯一要做的就是fork出一个子进程，然后这个子进程就会处理接下来的所有保存工作，父进程无需执行任何磁盘I/O操作。同时这个也是一个缺点，如果数据集比较大的时候，fork可能比较耗时，造成服务器一段时间内停止处理客户端的请求。

#### 2 AOF持久化策略

AOF可以将redis执行的每一条**写命令**追加到磁盘文件中，这一过程显然会降低redis的性能，但大部分情况下这个影响是能够接受的，另外使用较快的磁盘可以提高AOF的性能。

实践：

默认情况下redis没有开启AOF（append only file）方式的持久化，可以通过appendonly参数启用，在redis.conf中找到 appendonly yes。

开启AOF持久化后每执行一条会更改redis中的数据的命令后，redis就会将该命令写入磁盘中的AOF文件。AOF文件的保存位置和RDB文件的位置相同，都是通过 dir 参数设置的，默认的文件名是 appendonly.aof。可以在 redis.conf 中的属性 appendfilename appendonly.aof 修改。

1. 修改redis.conf中的appendonly yes ; 重启后执行对数据的变更命令， 会在bin目录下生成对应的.aof文件， aof文件中会记录所有的写操作命令。

2. 如下两个参数可以去对aof文件做优化

3. 1. auto-aof-rewrite-percentage 100
   2. 1. 表示当前aof文件大小超过上一次aof文件大小的百分之多少的时候会进行重写。如果之前没有重写过，以启动时aof文件大小为准。
   3. auto-aof-rewrite-min-size 64mb
      1. 限制允许重写最小aof文件大小，也就是文件大小小于64mb的时候，不需要进行优化。

   4. 另外，还可以通过BGREWRITEAOF 命令手动执行AOF，执行完以后冗余的命令已经被删除了

4. 在启动时，Redis会逐个执行AOF文件中的命令来将硬盘中的数据载入到内存中，载入的速度相对于RDB会慢一些

##### 2.1 AOF重写的原理

redis可以在AOF文件体积变得过大时，自动地再后台对AOF进行重写。重写后的新AOF文件包含了恢复当前数据集所需的最小命令集合。整个重写操作是绝对安全的。因为redis在创建新AOF文件的过程中，会继续将命令追加到现有的AOF文件里面，即使重写过程中发生停机，现有的AOF文件也不会丢失。而一旦新AOF文件创建完毕，redis就会从旧AOF文件切换到新AOF文件，并开始对新AOF文件进行追加操作。AOF文件有序地保存了对数据库执行的所有写入操作，这些写入操作以redis协议的格式保存，因此AOF文件内容非常容易被人读懂，对文件分析（parse）也很轻松。

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-redis/img/redis1.jpg?raw=true)

##### 2.2 AOF 同步磁盘数据

redis每次更改数据的时候，aof机制都会将命令记录到aof文件中，但是实际上由于操作系统的缓存机制，数据并没有实时写入到磁盘，而是进入磁盘缓存。再通过磁盘缓存机制去刷新保存到文件中。

相关规则配置：

1. appendfsync always 每次执行写入都会进行同步 ， 这个是最安全但是是效率比较低的方式。
2. appendfsync everysec 每一秒执行。
3. appendfsync no 不主动进行同步操作，由操作系统去执行，这个是最快但是最不安全的方式。

##### 2.3 AOF 文件损坏修复

服务器可能在程序正在对AOF进行写入时停机，如果停机造成了AOF文件出错（corrupt），那么redis在重启时会拒绝载入这个aof文件，从而确保数据的一致性不会被破坏。

当发生这种情况时，可以用一下方法来修复出错的AOF文件：

1. 为现有的AOF文件创建一个备份

2. 使用redis附带的redis-check-aof程序，对原来的AOF文件进行修复。

3. 1. redis-check-aof --fix

4. 重启redis服务器，等待服务器载入修复后的AOF文件，并进行数据恢复。

#### 3 RDB 和 AOF 如何选择

一般来说，如果对数据的安全性要求非常高的话，应该同时使用两种持久化功能，如果可以承受数分钟以内的数据丢失，那么可以只使用RDB持久化。

有很多用户都只使用AOF持久化，但并不推荐这种方式：因为定时生成RDB快照（snapshot）非常便于进行数据库备份，并且RDB恢复数据集的速度要比AOF恢复的速度要快。

两种持久化策略可以同时使用，也可以使用其中一种。如果同时使用的话，那么redis重启时，会优先使用AOF文件来还原数据。

#### 4 redis 内存回收策略

Redis中提供了多种内存回收策略，当内存容量不足时，为了保证程序的运行，这时就不得不淘汰内存中的一些对 象，释放这些对象占用的空间，那么选择淘汰哪些对象呢? 

其中，默认的策略为noeviction策略，当内存使用达到阈值的时候，所有引起申请内存的命令会报错

allkeys-lru：从数据集(server.db[i].dict)中挑选最近最少使用的数据淘汰

适应的场景：如果我们的应用对缓存的访问都是相对热点数据，那么可以选择这个策略。

allkeys-random：随机移除某个key。

适应的场景：如果我们的应用用于缓存key的访问概率相等，则可以使用这个策略。

volatile-random：从已设置过期时间的数据集(server.db[i].expires)中任意选择数据淘汰。 

volatile-lru：从已设置过期时间的数据集(server.db[i].expires)中挑选最近最少使用的数据淘汰。 

volatile-ttl：从已设置过期时间的数据集(server.db[i].expires)中挑选将要过期的数据淘汰 

适应的场景：这种策略使得我们可以向Redis提示那些 key 更适合被淘汰，我们可以自己控制。

### 八 集群

虽然redis有持久化功能能够保障redis服务器宕机也能恢复并且只有少量的数据损失，但是由于所有数据在一台服务器上，如果这台服务器出现硬盘故障，那就算是有备份也仍然不可避免数据丢失的问题。

在实际生产环境中，我们不可能只使用一台redis服务器作为我们的缓存服务器，必须要多台实现集群，避免出现
单点故障；

#### 1 主从复制（master、slave）

复制的作用是把redis的数据库复制多个副本部署在不同的服务器上，如果其中一台服务器出现故障，也能快速迁
移到其他服务器上提供服务。 复制功能可以实现当一台redis服务器的数据更新后，自动将新的数据同步到其他服
务器上。

主从复制就是我们常见的master/slave模式， 主数据库可以进行读写操作，当写操作导致数据发生变化时会自动将数据同步给从数据库。而一般情况下，从数据库是只读的，并接收主数据库同步过来的数据。 一个主数据库可以有多个从数据库。

master：192.168.11.138、slave：192.168.11.140/192.168.11.141

##### 1.1如何配置

在redis中配置master/slave是非常容易的，只需要在从数据库的配置文件中加入slaveof 主数据库地址端口。而
master 数据库不需要做任何改变。

1. master不要动，只需要更改slave就可以了
2. 修改11.140和11.141的redis.conf文件
3. 增加slaveof master.ip master.port
   1. slaveof 192.168.11.138 6379

4. 启动salve服务
5. 访问slave的redis客户端，输入 INFO replication
6. 通过在master机器上输入命令，比如set foo bar 、 在slave服务器就能看到该值已经同步过来了

##### 1.2 实现原理

- **全量复制**

Redis全量复制一般发生在Slave初始化阶段，这时Slave需要将Master上的所有数据都复制一份。具体步骤：

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-redis/img/redis-2.jpg?raw=true)

完成上面几个步骤后就完成了slave服务器数据初始化的所有操作，savle服务器此时可以接收来自用户的读请求。

master/slave 复制策略是采用乐观复制，也就是说可以容忍在一定时间内master/slave数据的内容是不同的，但是两者的数据会最终同步。具体来说，redis的主从同步过程本身是异步的，意味着master执行完客户端请求的命令后会立即返回结果给客户端，然后异步的方式把命令同步给slave。

这一特征保证启用master/slave后，master的性能不会受到影响。

但是另一方面，如果在这个数据不一致的窗口期间，master/slave因为网络问题断开连接，而这个时候，master
是无法得知某个命令最终同步给了多少个slave数据库。不过redis提供了一个配置项来限制只有数据至少同步给多
少个slave的时候，master才是可写的:

`min-slaves-to-write 3` 表示只有当3个或以上的slave连接到master，master才是可写的。

`min-slaves-max-lag 10` 表示允许slave最长失去连接的时间，如果10秒还没收到slave的响应，则master认为该 slave 已经断开。

- **增量复制** —— PSYNC master run id. offset

从redis 2.8开始，就支持主从复制的断点续传，如果主从复制过程中，网络连接断掉了，那么可以接着上次复制的 地方，继续复制下去，而不是从头开始复制一份 

master node会在内存中创建一个backlog，master和slave都会保存一个replica offset还有一个master id，offset 就是保存在backlog中的。如果master和slave网络连接断掉了，slave会让master从上次的replica offset开始继续复制，但是如果没有找到对应的offset，那么就会执行一次全量同步 

- **无硬盘复制** —— repl-diskless-sync no

前面我们说过，Redis复制的工作原理基于RDB方式的持久化实现的，也就是master在后台保存RDB快照，slave接收到rdb文件并载入，但是这种方式会存在一些问题。

1. 当master禁用RDB时，如果执行了复制初始化操作，Redis依然会生成RDB快照，当master下次启动时执行该 RDB文件的恢复，但是因为复制发生的时间点不确定，所以恢复的数据可能是任何时间点的。就会造成数据出现问题。 

2. 当硬盘性能比较慢的情况下(网络硬盘)，那初始化复制过程会对性能产生影响 

因此2.8.18以后的版本，Redis引入了无硬盘复制选项，可以不需要通过RDB文件去同步，直接发送数据，通过以下配置来开启该功能：repl-diskless-sync yes。

master 会在内存中直接创建rdb，然后发送给 slave，不会在自己本地落地磁盘了。

#### 2 哨兵机制

在前面讲的master/slave模式，在一个典型的一主多从的系统中，slave在整个体系中起到了数据冗余备份和读写
分离的作用。当master遇到异常终端后，需要从slave中选举一个新的master继续对外提供服务，这种机制在前面
提到过N次，比如在zk中通过leader选举、kafka中可以基于zk的节点实现master选举。所以在redis中也需要一种
机制去实现master的决策，redis并没有提供自动master选举功能，而是需要借助一个哨兵来进行监控。

##### 2.1 什么是哨兵

顾名思义，哨兵的作用就是监控Redis系统的运行状况，它的功能包括两个：

1. 监控master和slave是否正常运行
2. master出现故障时自动将slave数据库升级为master

哨兵是一个独立的进程，使用哨兵后的架构图：

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-redis/img/sentinel-1.jpg?raw=true)

为了解决master选举问题，又引出了一个单点问题，也就是哨兵的可用性如何解决，在一个一主多从的Redis系统
中，可以使用多个哨兵进行监控任务以保证系统足够稳定。此时哨兵不仅会监控master和slave，同时还会互相监
控；这种方式称为哨兵集群，哨兵集群需要解决故障发现、和 master 决策的协商机制问题。

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-redis/img/sentinel-2.jpg?raw=true)

sentinel之间的相互感知，sentinel节点之间会因为共同监视同一个master从而产生了关联，一个新加入的sentinel节点需要和其他监视相同master节点的sentinel相互感知，首先：

1. 需要相互感知的sentinel都向他们共同监视的master节点订阅channel:sentinel:hello 

2. 新加入的sentinel节点向这个channel发布一条消息，包含自己本身的信息，这样订阅了这个channel的sentinel 就可以发现这个新的sentinel 

3. 新加入得sentinel和其他sentinel节点建立长连接

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-redis/img/sentinel-3.jpg?raw=true)

##### 2.2 master的故障发现

sentinel 节点会定期向 master 节点发送心跳包来判断存活状态，一旦 master 节点没有正确响应，sentinel 会把 master 设置为“主观不可用状态”，然后它会把“主观不可用”发送给其他所有的 sentinel 节点去确认，当确认的 sentinel 节点数大于>quorum时，则会认为master是“客观不可用”，接着就开始进入选举新的master流程；

但是 这里又会遇到一个问题，就是sentinel中，本身是一个集群，如果多个节点同时发现master节点达到客观不可用状态，那谁来决策选择哪个节点作为maste呢？这个时候就需要从sentinel集群中选择一个leader来做决策。而这里用到了一致性算法Raft算法、它和Paxos算法类似，都是分布式一致性算法。但是它比Paxos算法要更容易理解；Raft和Paxos算法一样，也是基于投票算法，只要保证过半数节点通过提议即可; 

动画演示地址:http://thesecretlivesofdata.com/raft/ 

##### 2.3 配置实现

1. cp ../redis-3.2.8/sentinel.conf sentinel.conf 复制哨兵配置文件到redis中
2. 修改配置文件 port
3. 修改监控master节点配置：sentinel monitor mymaster 192.168.11.138 6379 2
4. 多少秒之内mymaster没有响应就认为down掉：sentinel down-after-milliseconds mymaster 30000
5. 修改配置文件后，./redis-sentinel ../sentinel.conf

通过在这个配置的基础上增加哨兵机制。在其中任意一台服务器上创建一个sentinel.conf文件。

文件内容：`sentinel monitor name ip port quorum`

其中name表示要监控的master的名字，这个名字是自己定义。 ip和port表示master的ip和端口号。 最后一个1表示最低通过票数，也就是说至少需要几个哨兵节点统一才可以。

port 6040

sentinel monitor mymaster 192.168.11.131 6379 1 

sentinel down-after-milliseconds mymaster 5000 --表示如果5s内mymaster没响应，就认为SDOWN 

sentinel failover-timeout mymaster 15000 --表示如果15秒后,mysater仍没活过来，则启动failover，从剩下的 slave中选一个升级为master 

两种方式启动哨兵：

1. redis-sentinel sentinel.conf
2.  redis-server /path/to/sentinel.conf --sentinel 

哨兵监控一个系统时，只需要配置监控master即可，哨兵会自动发现所有slave；

这时候，我们把master关闭，等待指定时间后(默认是30秒)，会自动进行切换，会输出如下消息：

~~~txt
+sdown表示哨兵主管认为master已经停止服务了，+odown表示哨兵客观认为master停止服务了。关于主观和客 观，后面会给大家讲解。接着哨兵开始进行故障恢复，挑选一个slave升级为master 

+try-failover表示哨兵开始进行故障恢复 +failover-end 表示哨兵完成故障恢复 

+slave表示列出新的master和slave服务器，我们仍然可以看到已经停掉的master，哨兵并没有清楚已停止的服务 的实例，这是因为已经停止的服务器有可能会在某个时间进行恢复，恢复以后会以slave角色加入到整个集群中。
~~~

#### 3 集群 —— Redis-Cluster（redis3.0以后的功能）

即使是使用哨兵，此时的Redis集群的每个数据库依然存有集群中的所有数据，从而导致集群的总数据存储量受限
于可用存储内存最小的节点，形成了木桶效应。而因为Redis是基于内存存储的，所以这一个问题在redis中就显得
尤为突出了。

在redis3.0之前，我们是通过在客户端去做的分片，通过hash环的方式对key进行分片存储。分片虽然能够解决各
个节点的存储压力，但是导致维护成本高、增加、移除节点比较繁琐。因此在redis3.0以后的版本最大的一个好处
就是支持集群功能，集群的特点在于拥有和单机实例一样的性能，同时在网络分区以后能够提供一定的可访问性以
及对主数据库故障恢复的支持。

哨兵和集群是两个独立的功能，当不需要对数据进行分片使用哨兵就够了，如果要进行水平扩容，集群是一个比较
好的方式。

##### 3.1 拓扑结构

一个Redis Cluster由多个Redis节点构成。不同节点组服务的数据没有交集，也就是每一个节点组对应数据
sharding的一个分片。节点组内部分为主备两类节点，对应master和slave节点。两者数据准实时一致，通过异步
化的主备复制机制来保证。一个节点组有且只有一个master节点，同时可以有0到多个slave节点，在这个节点组中只有master节点对用户提供些服务，读服务可以由master或者slave提供。

redis-cluster是基于gossip协议实现的无中心化节点的集群，因为去中心化的架构不存在统一的配置中心，各个节
点对整个集群状态的认知来自于节点之间的信息交互。在Redis Cluster，这个信息交互是通过Redis Cluster Bus来完成的。

##### 3.2 Redis的数据分区

分布式数据库首要解决把整个数据集按照分区规则映射到多个节点的问题，即把数据集划分到多个节点上，每个节点负责整个数据的一个子集，Redis Cluster采用哈希分区规则，采用虚拟槽分区。 

虚拟槽分区巧妙地使用了哈希空间，使用分散度良好的哈希函数把所有的数据映射到一个固定范围内的整数集合，
整数定义为槽(slot)。比如Redis Cluster槽的范围是0 ~ 16383。槽是集群内数据管理和迁移的基本单位。采用
大范围的槽的主要目的是为了方便数据的拆分和集群的扩展，每个节点负责一定数量的槽。

计算公式:slot = CRC16(key)%16383。每一个节点负责维护一部分槽以及槽所映射的键值数据。

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-redis/img/cluster-1.jpg?raw=true)

##### 3.3 HashTags

通过分片手段，可以将数据合理的划分到不同的节点上，这本来是一件好事。但是有的时候，我们希望对相关联的
业务以原子方式进行操作。举个简单的例子：

我们在单节点上执行MSET , 它是一个原子性的操作，所有给定的key会在同一时间内被设置，不可能出现某些指定
的key被更新另一些指定的key没有改变的情况。但是在集群环境下，我们仍然可以执行MSET命令，但它的操作不
在是原子操作，会存在某些指定的key被更新，而另外一些指定的key没有改变，原因是多个key可能会被分配到不
同的机器上。

所以，这里就会存在一个矛盾点，及要求key尽可能的分散在不同机器，又要求某些相关联的key分配到相同机器。这个也是在面试的时候会容易被问到的内容。怎么解决呢?

从前面的分析中我们了解到，分片其实就是一个hash的过程，对key做hash取模然后划分到不同的机器上。所以为了解决这个问题，我们需要考虑如何让相关联的key得到的hash值都相同呢?如果key全部相同是不现实的，所以
怎么解决呢？在redis中引入了HashTag的概念，可以使得数据分布算法可以根据key的某一个部分进行计算，然后
让相关的key落到同一个数据分片。

举个简单的例子：加入对于用户的信息进行存储， user:user1:id、user:user1:name/ 那么通过hashtag的方式，
user:{user1}:id、user:{user1}.name；表示当一个key包含 {} 的时候，就不对整个key做hash，而仅对 {} 包括的字符串做hash。

##### 3.4 重定向客户端

Redis Cluster并不会代理查询，那么如果客户端访问了一个key并不存在的节点，这个节点是怎么处理的呢?比如
我想获取key为msg的值，msg计算出来的槽编号为254，当前节点正好不负责编号为254的槽，那么就会返回客户
端下面信息：`-MOVED 254 127.0.0.1:6381`

表示客户端想要的254槽由运行在IP为127.0.0.1，端口为6381的Master实例服务。如果根据key计算得出的槽恰好 由当前节点负责，则当期节点会立即返回结果。

##### 3.5 分片迁移

在一个稳定的Redis cluster下，每一个slot对应的节点是确定的，但是在某些情况下，节点和分片对应的关系会发
生变更。

1. 新加入master节点
2. 某个节点宕机

也就是说当动态添加或减少node节点时，需要将16384个槽做个再分配，槽中的键值也要迁移。当然，这一过程，在目前实现中，还处于半自动状态，需要人工介入。

新增一个主节点

新增一个节点D，redis cluster的这种做法是从各个节点的前面各拿取一部分slot到D上。大致就会变成这样:

节点A覆盖1365-5460
节点B覆盖6827-10922
节点C覆盖12288-16383
节点D覆盖0-1364,5461-6826,10923-12287	

删除一个主节点

先将节点的数据移动到其他节点上，然后才能执行删除

##### 3.6 槽迁移的过程

槽迁移的过程中有一个不稳定状态，这个不稳定状态会有一些规则，这些规则定义客户端的行为，从而使得Redis
Cluster不必宕机的情况下可以执行槽的迁移。下面这张图描述了我们迁移编号为1、2、3的槽的过程中，他们在
MasterA节点和MasterB节点中的状态。

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-redis/img/cluster-2.jpg?raw=true)

**简单的工作流程：**

1. 向MasterB发送状态变更命令，把Master B对应的slot状态设置为IMPORTING
2. 向MasterA发送状态变更命令，将Master对应的slot状态设置为MIGRATING

**MIGRATING状态：**

1. 如果客户端访问的Key还没有迁移出去，则正常处理这个key
2. 如果key已经迁移或者根本就不存在这个key，则回复客户端ASK信息让它跳转到MasterB去执行

**IMPORTING状态：**

当MasterB的状态设置为IMPORTING后，表示对应的slot正在向MasterB迁入，即使Master仍然能对外提供该slot 的读写服务，但和通常状态下也是有区别的。

当来自客户端的正常访问不是从ASK跳转过来的，说明客户端还不知道迁移正在进行，很有可能操作了一个目前
还没迁移完成的并且还存在于MasterA上的key，如果此时这个key在A上已经被修改了，那么B和A的修改则会发生
冲突。所以对于MasterB上的slot上的所有非ASK跳转过来的操作，MasterB都不会处理，而是通过MOVED命令让客户端跳转到MasterA上去执行。

这样的状态控制保证了同一个key在迁移之前总是在源节点上执行，迁移后总是在目标节点上执行，防止出现两边
同时写导致的冲突问题。而且迁移过程中新增的key一定会在目标节点上执行，源节点也不会新增key，是的整个迁移过程既能对外正常提供服务，又能在一定的时间点完成slot的迁移。

##### 3.7 老的笔记

根据key的hash值取模服务器的数量 。

集群的原理：

​	Redis Cluster中，Sharding采用slot(槽)的概念，一共分成16384个槽，这有点儿类似前面讲的pre sharding思路。对于每个进入Redis的键值对，根据key进行散列，分配到这16384个slot中的某一个中。使用的hash算法也比较简单，就是CRC16后16384取模。

Redis集群中的每个node(节点)负责分摊这16384个slot中的一部分，也就是说，每个slot都对应一个node负责处理。当动态添加或减少node节点时，需要将16384个槽做个再分配，槽中的键值也要迁移。当然，这一过程，在目前实现中，还处于半自动状态，需要人工介入。

Redis集群，要保证16384个槽对应的node都正常工作，如果某个node发生故障，那它负责的slots也就失效，整个集群将不能工作。为了增加集群的可访问性，官方推荐的方案是将node配置成主从结构，即一个master主节点，挂n个slave从节点。这时，如果主节点失效，Redis Cluster会根据选举算法从slave节点中选择一个上升为主节点，整个集群继续对外提供服务。这非常类似服务器节点通过Sentinel监控架构成主从结构，只是Redis Cluster本身提供了故障转移容错的能力。

slot（槽）的概念，在redis集群中一共会有16384个槽，根据key 的CRC16算法，得到的结果再对16384进行取模。

假如有3个节点

node1  0 5460

node2  5461 10922

node3  10923 16383

节点新增

node4  0-1364,5461-6826,10923-12287

删除节点

先将节点的数据移动到其他节点上，然后才能执行删除

#### 4 市面上提供了集群方案

1. redis shardding而且jedis客户端就支持shardding操作SharddingJedis

2. 1. 增加和减少节点的问题；
   2. pre shardding3台虚拟机redis。但是我部署了9个节点 。每一台部署3个redis增加cpu的利用率。9台虚拟机单独拆分到9台服务器

3. codis基于redis2.8.13分支开发了一个codis-server 用得比较多

4. twemproxy twitter提供的开源解决方案