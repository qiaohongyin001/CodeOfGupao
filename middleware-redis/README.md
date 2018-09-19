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



















