## 分布式消息通讯 - activemq

**官方网站：**<http://activemq.apache.org/>

### 一 基本介绍

#### 1 简介

ActiveMQ是Apache开源基金会研发的消息中间件。是完全支持JMS1.1和J2EE1.4规范的 JMS provider实现。

ActiveMQ主要应用在分布式系统架构中，帮助构建高可用、高性能、可伸缩的企业级面向消息服务的系统。

#### 2 应用场景

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-activemq/img/activemq1.jpg?raw=true)

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-activemq/img/activemq2.jpg?raw=true)

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-activemq/img/activeme3.jpg?raw=true)

#### 3 安装

1. 下载activeMQ安装包

2. tar -zxvf ***.tar.gz

3. sh /bin/activemq start 启动activeMQ服务

4. 校验是否启动成功

5. 1. ip:8161 admin=admin

#### 4 什么是MOM

<http://activemq.apache.org/mom.html>

面向消息的中间件，使用消息传送提供者来协调消息传输操作。MOM需要提供API和管理工具。客户端调用API。把消息发送到消息传送提供者指定的目的地。在消息发送之后，客户端会继续执行其他的工作。并且在接收方收到这个消息确认之前。提供者一直保留该消息。

#### 5 activemq 依赖包

~~~java
<dependency>
  <groupId>org.apache.activemq</groupId>
  <artifactId>activemq-all</artifactId>
  <version>5.15.0</version>
</dependency>
~~~

### 二 JMS

#### 1 基本概念

1. JMS概念

2. 1. java消息服务（Java Message Service）是java平台中关于面向消息中间件的API，用于再两个应用之间或者分布式系统中发送消息，进行异步通信。
   2. JMS是一个与具体平台无关的API，绝大多数MOM（Message Oriented Middleware 以消息为目的中间件）提供商都对JMS提供了支持。

3. 其他开源的JMS提供商

4. 1. JbossMQ(jboss4)、jboss messaging(jboss5)、joram、ubermq、mantamq、openjms

5. JMS模型

























