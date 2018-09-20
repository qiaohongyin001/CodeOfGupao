## 分布式缓存技术 - redis 

### 一 初识 redis

#### 1 redis 存储结构

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
2. ttl 获得key的过期时间

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

#### 3 发布订阅

（外部访问需要修改配置文件：bind绑定ID、protected-mode受保护的）

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

   4. 1. 限制允许重写最小aof文件大小，也就是文件大小小于64mb的时候，不需要进行优化。

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

### 八 集群

#### 1 复制（master、slave）

master：192.168.11.138、slave：192.168.11.140/192.168.11.141

**如何配置：**

1. master不要动，只需要更改slave就可以了
2. 修改11.140和11.141的redis.conf文件
3. 增加salveof master.ip master.port
   1. slaveof 192.168.11.138 6379

##### 1.1 实现原理

1. slave第一次或者重连到master上以后，会向master发送一个SYNC命令

2. master收到SYNC的时候，会做两件事

3. 1. 执行bgsave（rdb的快照文件）
   2. master会把新收到的命令存入到缓存区

缺点：没有办法对master进行动态选举

##### 1.2 复制的方式

1. 基于rdb文件的复制（第一次链接或者重连的时候）

2. 无硬盘复制

3. 1. repl-diskless-sync no

4. 增量复制

5. 1. PSYNC master run id. offset

#### 2 哨兵机制

1. 作用

2. 1. 监控master和slave是否正常运行
   2. 如果master出现故障，那么会把其中一台slave数据升级为master

3. 配置过程

4. 1. cp ../redis-3.2.8/sentinel.conf sentinel.conf 复制哨兵配置文件到redis中
   2. 修改配置文件 port
   3. 修改监控master节点配置：sentinel monitor mymaster 192.168.11.138 6379 2
   4. 多少秒之内mymaster没有响应就认为down掉：sentinel down-after-milliseconds mymaster 30000
   5. 修改配置文件后，./redis-sentinel ../sentinel.conf

#### 3 集群（redis3.0以后的功能）

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

