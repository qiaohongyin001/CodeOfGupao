## 分布式服务治理 - dubbo

### 一 前瞻

**官方网站：**<http://dubbo.apache.org/>

#### 1 dubbo 能解决什么问题

1. 怎么去维护url

2. 1. 通过注册中心去维护url（zookeeper、redis、memcache...）

3. F5硬件负载均衡的单点压力比较大

4. 1. 软负载均衡

5. 怎么去整理出服务器之间的依赖管理

6. 1. 自动去整理各个服务之间的依赖管理

7. 如果服务器的调用量越来越大，服务器的容量问题如何去评估？扩容的指标？

8. 1. 需要一个监控平台，可以监控调用量、响应时间

#### 2 dubbo 是什么

dubbo是一个分布式的服务框架、提供高性能的以及透明化的RPC远程服务调用解决方案、以及SOA服务治理方案。

#### 3 dubbo 的核心部分

1. 远程通信
2. 集群容错
3. 服务的自动发现
4. 负载均衡

#### 4 dubbo 的架构

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-dubbo/img/dubbo1.jpg?raw=true)

#### 5 dubbo 的核心角色

1. Container：容器
2. Provider：提供者
3. Registry：注册
4. Consumer：消费者
5. Monitor：监控

### 二 dubbo 入门

dubbo是为了解决：各个应用节点中的url管理维护很困难、依赖关系很模糊，每个应用节点的性能、访问量、响应时间、没法评估。

#### 1 dubbo 源码下载

<https://github.com/apache/incubator-dubbo>

#### 2 dubbo 使用入门

1. 控制台打印出的日志-注册进入zookeeper的信息

~~~java
dubbo://177.1.1.82:20880/com.gupao.vip.mic.dubbo.order.IOrderServices?anyhost=true&application=order-provider&dubbo=2.5.3&interface=com.gupao.vip.mic.dubbo.order.IOrderServices&methods=doOrder&owner=mic&pid=12500&side=provider&timestamp=1502889986089, dubbo version: 2.5.3, current host: 127.0.0.1
~~~

2. 从zookeeper中获取provider接点get到的信息

~~~java
dubbo://177.1.1.82/20880/com.gupao.vip.mic.dubbo.order.IOrderServices%3Fanyhost%3Dtrue%26application%3Dorder-provider%26dubbo%3D2.5.3%26interface%3Dcom.gupao.vip.mic.dubbo.order.IOrderServices%26methods%3DdoOrder%26owner%3Dmic%26pid%3D10804%26side%3Dprovider%26timestamp%3D1502890818766
~~~

#### 3 单点通信

**详细代码见项目**

#### 4 zookeeper 通信

**详细代码见项目**

#### 5 日志是怎么集成的

| Log4j                                 | 最高（默认就用这个）                     |
| ------------------------------------- | ---------------------------------------- |
| SLF4J                                 | 次高（上面没有采用这个）                 |
| Common Logging(jcl就是common logging) | 次低(Log4j和SLF4J在项目中均没有就用这个) |
| JDK log                               | 最低（最后的选择）                       |

#### 6 admin 控制台的安装

1. 下载dubbo源码
2. 找到dubbo-admin
3. 修改webapp/WEB-INF/dubbo.properties的dubbo.registry.address=zookeeper的集群地址

控制中心是用来做服务治理的，不如控制服务的权重，服务的路由。。。

dubbo版本的不同，可能高版本不会带 admin 这个模块项目了

学习地址：<https://www.cnblogs.com/shengulong/p/8303454.html>

#### 7 simple 监控中心

Monitor也是一个dubbo服务，所以也会有端口和url

1.  修改/conf目录下dubbo.properties

2. 1. dubbo.registry.address=zookeeper://192.168.11.129:2181?backup=192.168.11.137:2181,192.168.11.138:2181,192.168.11.139:2181

3. 监控注册

4. 1. 修改提供服务方的XML：order-provider.xml
   2. <dubbo:monitor protocol="registry"/>

5. 在bin目录下启动后。访问<http://127.0.0.1:8080/>即可看到监控界面

6. 1. 监控服务的调用次数、调用关系、响应时间

#### 8 给dubbo添加白名单

<https://blog.csdn.net/mj158518/article/details/47379799>

### 三 dubbo 高级操作

**dubbo 配置文件详解：**<https://www.cnblogs.com/linjiqin/p/5859153.html>

#### 1 启动服务检索

如果提供方没有启动的时候，默认会去检测所依赖的服务是否正常提供服务。

如果check为false，表示启动的时候不去检查。当服务出现循环依赖的时候，check设置成false。

1. dobbo:reference	属性：check：true,false	默认值是true
2. dubbo:consumer	check="false"
3. dubbo:registry check=false
4. dubbo:provider

#### 2 多协议支持

dubbo支持的协议：dubbo、RMI、hessian、webservice、http、Thrift

**代码中有hessian协议演示**

#### 3 多注册中心

~~~java
<dubbo:registry id ="zkOne" protocol="zookeeper" address="39.107.31.208:2181,39.107.32.43:2181,47.95.39.176:2181"/>

<dubbo:registry id ="zkTwo" protocol="zookeeper" address="39.107.31.208:2181"/>

<dubbo:service async="true" interface="com.wolfman.middleware.dubbo.api.HelloWorldService"
                   ref="helloWorldService" protocol="hessian,dubbo" register="zkOne,zkTwo" />
~~~

#### 4 多版本支持

**服务端调用**

~~~java
<dubbo:service interface="com.wolfman.middleware.dubbo.api.HelloWorldService"
    ref="helloWorldService" protocol="dubbo" version="1.0" />

<dubbo:service interface="com.wolfman.middleware.dubbo.api.HelloWorldService"
    ref="helloWorldServiceV2" protocol="dubbo" version="2.0" />
~~~

**客户端调用**

~~~java
<dubbo:reference id="orderServices" interface="com.wolfman.middleware.dubbo.api.HelloWorldService" protocol="hessian" version="1.0"/>
~~~

#### 5 异步调用

async="true"表示接口异步返回

hessian协议，使用async异步回调会报错

**服务端配置**

~~~java
<dubbo:service async="true" interface="com.wolfman.middleware.dubbo.api.HelloWorldService"
            ref="helloWorldService" protocol="hessian,dubbo"/>
~~~

**客户端调用**

~~~java
Future<DoOrderResponse> response = RpcContext.getContext().getFuture();
DoOrderResponse response1 = response.get();
//详细代码见项目中代码
~~~

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-dubbo/img/dubbo2.jpg?raw=true)

#### 6 主机绑定

provider://177.1.1.82:20880

1. 通过<dubbo:protocol host配置的地址去找
2. host = InetAddress.getLocalHost().getHostAddress();
3. 通过socket发起连接连接到注册中心的地址。再获取连接过去以后本地的ip地址
4. host = NetUtils.getLocalHost();

#### 7 dubbo 服务只订阅

只调用其他服务，不提供服务

~~~java
<dubbo:registry  protocol="zookeeper" address="39.107.31.208:2181,39.107.32.43:2181,47.95.39.176:2181" register="false" />
~~~

#### 8 dubbo 服务只注册

只提供服务，不调用其他服务

~~~java
<dubbo:registry subscribe="false" protocol="zookeeper" address="39.107.31.208:2181,39.107.32.43:2181,47.95.39.176:2181"/>
~~~

#### 9 连接超时

必须要设置服务的处理的超时时间

~~~java
<!--服务发布的配置，需要暴露的服务接口-->
<dubbo:service timeout="20" interface="com.dubbo.order.IOrderServices" ref="orderService" protocol="hessian"/>
~~~

### 四 负载均衡

在集群负载均衡时，Dubbo提供了多种均衡策略，缺省为random随机调用。可以自行扩展负载均衡策略。

- Random LoadBalance（默认）：随机

1. 按权重设置随机概率。
2. 在一个截面上碰撞的概率高，但调用量越大分布越均匀，而且按概率使用权重后也比较均匀，有利于动态调整提供者权重。

- RoundRobin LoadBalance：轮循

1. 按公约后的权重设置轮循比率。
2. 存在慢的提供者累积请求的问题，比如：第二台机器很慢，但没挂，当请求调到第二台时就卡在那，久而久之，所有请求都卡在调到第二台上。

- LeastActive LoadBalance：最少活跃调用数

1. 相同活跃数的随机，活跃数指调用前后计数差。
2. 使慢的提供者收到更少请求，因为越慢的提供者的调用前后计数差会越大。

- Consistent LoadBalance：一致性Hash

1. 相同参数的请求总是发到同一提供者。

~~~java
<dubbo:service async="true" interface="com.wolfman.middleware.dubbo.api.HelloWorldService"
            ref="helloWorldService" protocol="hessian,dubbo" loadbalance="roundrobin" />
~~~

### 五 集群容错 配置优先级

- Failover cluster 

1. 失败的时候自动切换并重试其他服务器。通过retries=2来设置重试次数。
2. **默认**

- failfast cluster 

1. 快速失败，只发起一次调用；写操作。比如新增记录的时候，非幂等请求。

- failsafe cluster 

1. 失败安全，出现异常时，直接忽略异常

- failback cluster

1. 失败自动恢复。后台记录失败请求，定时重发

- forking cluster

1. forks：设置并行数
2. 并行调用多个服务器，只要一个成功就返回

- broadcast cluster

1. 广播调用所有提供者，逐个调用，其中一台报错就会返回异常

~~~java
<dubbo:reference id="orderServices" protocol="dubbo" 
    	cluster="failfast" interface="com.dubbo.order.IOrderServices"/>
~~~

#### 1. 配置的优先级

消费端>服务端

~~~java
reference method > service method > reference > service > consumer > provider
~~~

### 六 服务的最佳实践

1. 分包
   1. 服务接口、请求服务模型、异常信息都放在API里边，符合重用发布等价原则，共同重用
   2. api里面放入spring的引用配置。也可以放在模块的包目录下。
      1. com.huhao.order/***-reference.xml
2. 粒度
   1. 尽可能把接口设置成粗粒度，每个服务方法代表一个独立的功能，而不是某个功能的步骤。否则就会涉及到分布式事务。
   2. 服务接口建议以业务场景为单位划分。并对相近业务做抽象、防止接口暴增。
   3. 不建议使用过于抽象的通用接口 T T<泛型>，接口没有明确的予以，带来后期的维护。
3. 版本
   1. 每个接口都应该定义版本，为后续的兼容性提供前瞻性的考虑 version。
   2. 建议使用两位版本号，因为第三位版本号标识的兼容性升级，只有不兼容时才需要变更服务版本。
   3. 当接口做到不兼容升级的时候，先升级一半或者一台提供者为新版本，再将消费者全部升级新版本，然后再将剩下的一半提供者升级新版本。例如预发布版本。

#### 1 推荐用法

1. 在provider端尽可能配置consumer端的属性
   1. 比如：timeout/retires/线程池大小/loadBalance
2. 配置管理员信息
   1. application上面配置的owner、owner建议配置2个人以上

#### 2 配置dubbo缓存文件

注册中心的列表

服务提供者列表

<dubbo:registryfile=”${user.home}/output/dubbo.cache” />

### 七 dubbo 注册到 zookeeper 中的节点

dubbo注册服务到zookeeper

例如：

dubbo

—— com.mall.user.dubbo.CityProviderService

—— consumers, configurators, routers, providers

这些节点都是持久化节点。在consumers, configurators, routers, providers里边的节点才为临时节点。

### 八 源码分析

有时间多看下源码









