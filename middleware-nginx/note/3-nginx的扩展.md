

## nginx的扩展

### 一 进程模型

#### 1 master 进程

充当整个进程组与用户的交互接口，同时对进程进行监控。它不需要处理网络事件，不负责业务的执行，只会通过管理work进程来实现重启服务、平滑升级、更换日志文件、配置文件实时生效等功能。

主要是用来管理woker进程。

1. 接收来自外界的信号（前面提到的 kill -HUP 信号等）
2. 1. 我们要控制nginx，只需要通过kill向master进程发送信号就行了。
   2. 比如kill -HUP pid，则告诉nginx，从容地重启nginx，我们一般用这个信号来重启nginx，或重新加载配置，因为是从容地从其，因此服务是不中断的。master进程在接收到HUP信号后是怎么做的呢？首先master进程在接收到信号后，会先重新加载配置文件，然后再启动新的worker进程，并向所有老的woker进程发送新型号，告诉他们可以光荣退休了。新的worker在启动后，就开始接收新的请求，而老的worker在收到来自master的信号后，就不再接收新的请求，并且当前进程中的所有未处理完的请求处理成功后，再退出。
3. 向各个woker进程发送信号
4. 监控worker进程的运行状态
5. 当worker进程退出后（异常情况下），会自动重新启动新的worker进程

#### 2 worker 进程

主要是完成具体的任务逻辑。他的主要关注点是客户端和后端真实服务器之间的数据可读、可写等I/O交互事件。

各个worker进程之间是对等且相互独立的，他们同等竞争来自客户端的请求，一个请求只可能在一个worker进程中处理，worker进程个数一般设置为CPU核数。

master进程先建好需要listen的socket后，然后再fork出多个worker进程，这样每个work进程都可以去accept这个socket。当要个client连接到来时，所有accept的worker进程会收到通知，但只有一个进程可以accept成功，其他的则会accept失败。

```nginx
设置线程数
worker_processes 4;
worker_cpu_affinity 0001 0010 0100 1000;
```

### 二 高可用方案

Nginx 作为反向代理服务器，所有的流量都会经过 Nginx，所以 Nginx 本身的可靠性是我们首先要考虑的问题。

#### 1 keepalived

Keepalived 是 Linux 下一个轻量级别的高可用解决方案，Keepalived 软件起初是专为 LVS 负载均衡软件设计的，用来管理并监控 LVS 集群系统中各个服务节点的状态，后来又加入了可以实现高可用的 VRRP 功能。因此，Keepalived 除了能够管理 LVS 软件外，还可以作为其他服务(例如:Nginx、Haproxy、MySQL 等)的高可用解决方案软件。

Keepalived 软件主要是通过 VRRP 协议实现高可用功能的。

VRRP 全称 Virtual Router Redundancy Protocol，即虚拟路由器冗余协议。可以认为他是实现路由器高可用的容错协议，即将N台提供相同功能的路由器组成一个路由器组（Router Group），这个组里面有一个master和多个backup，但是外界看来就像一台一样，构成虚拟路由器，拥有一个虚拟IP（vip，也就是路由器所在局域网内其他机器默认路由），占有这个IP的master实际负责ARP响应和转发IP数据包，组中的其他路由器作为备份的角色处于待命状态。master会发组播消息，当backup在超时时间内收不到vrrp包时，就认为master宕掉了，这时就需要根据VRRP的优先级来选举一个backup当master，保证路由器的高可用。

所以，Keepalived 一方面具有配置管理 LVS 的功能，同时还具有对 LVS 下面节点进行健康检查的功能，另一方面也可实现系统网络服务的高可用功能。

LVS 是 Linux Virtual Server 的缩写，也就是 Linux 虚拟服务器，在 linux2.4 内核以后，已经完全内置了 LVS 的各个功能模块。

它是工作在四层的负载均衡，类似于 Haproxy, 主要用于实现对服务器集群的负载均衡。

关于四层负载，我们知道 osi 网络层次模型的 7 层模型(应用层、表示层、会话层、传输层、网络层、数据链路层、物理层);四层负载就是基于传输层，也就是ip+端口的负载;而七层负载就是需要基于 URL 等应用层的信息来做负载，同时还有二层负载(基于 MAC)、三层负载(IP);

常见的四层负载有：LVS、F5; 七层负载有:Nginx、HAproxy; 在软件层面，Nginx/LVS/HAProxy 是使用得比较广泛的三种负载均衡软件。

对于中小型的 Web 应用，可以使用 Nginx、大型网站或者重要的服务并且服务比较多的时候，可以考虑使用 LVS。

轻量级的高可用解决方案：

LVS 四层负载均衡软件(Linux virtual server)

监控 lvs 集群系统中的各个服务节点的状态

VRRP 协议(虚拟路由冗余协议)

linux2.4 以后，是内置在 linux 内核中的

lvs(四层) -> HAproxy 七层

lvs(四层) -> Nginx(七层)

#### 2 安装 keepalived

1. 下载 keepalived 的安装包
2. tar -zxvf keepalived.tar.gz
3. 在/data/program/目录下创建一个 keepalived 的文件
4.  cd 到 keepalived-2.0.7 目录下，执行 ./configure --prefix=/data/program/keepalived --sysconf=/etc
5. 如果缺少依赖库，则 yum install gcc; yum install openssl-devel ; yum install libnl
   libnl-devel
6. 编译安装 make && make install
7. 进入安装后的路径 cd /data/program/keepalived, 创建软连接: ln -s sbin/keepalived /sbin
8. cp /data/program/keepalived-2.0.7/keepalived/etc/init.d/keepalived/etc/init.d
9. 添加到系统服务
   1. chkconfig --add keepalived
   2. chkconfig keepalived on
   3. Service keepalived start

#### 3 基于 keepalived + nginx 的配置 

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-nginx/img/nginx4.jpg?raw=true)

##### 3.1 master keepalived.conf

```nginx
! Configuration File for keepalived

global_defs {
   router_id LVS_DEVEL #运行 keepalived 服务器的标识，在一个网络内应该是唯 一的
}

vrrp_instance VI_1 { #vrrp 实例定义部分
    state MASTER	#设置lvs的状态，MASTER和BACKUP两种，必须大写
    interface ens33 #设置对外服务的接口
    virtual_router_id 51 #设置虚拟路由标示，这个标示是一个数字，同一个 vrrp 实例使用唯一标示
    priority 100	#定义优先级，数字越大优先级越高，在一个 vrrp——instance 下，master 的优先级必须大于 backup
    advert_int 1	#设定 master 与 backup 负载均衡器之间同步检查的时间间隔，单位是秒
    authentication {	#设置验证类型和密码
        auth_type PASS
        auth_pass 1111	#验证密码，同一个 vrrp_instance 下 MASTER 和 BACKUP 密码必须相同
    }
    virtual_ipaddress {	#设置虚拟 ip 地址，可以设置多个，每行一个
        192.168.11.100
    }
}

virtual_server 192.168.11.100 80 {#设置虚拟服务器，需要指定虚拟 ip 和服务 端口
    delay_loop 6	#健康检查时间间隔
    lb_algo rr		#负载均衡调度算法
    lb_kind NAT		#负载均衡转发规则
    persistence_timeout 50	#设置会话保持时间
    protocol TCP	#指定转发协议类型，有 TCP 和 UDP 两种

    real_server 192.168.11.160 80 {	#配置服务器节点 1，需要指定 real serve r 的真实 IP 地址和端口
        weight 1	#设置权重，数字越大权重越高
        TCP_CHECK {	#realserver的状态监测设置部分单位秒
          connect_timeout 3	#超时时间
          delay_before_retry 3	#重试间隔
          connect_port 80	#监测端口
        }
    }
}
```

##### 3.2 backup keepalived.conf

```nginx
! Configuration File for keepalived
global_defs {
   router_id LVS_DEVEL
}
vrrp_instance VI_1 {
    state BACKUP
    interface ens33
    virtual_router_id 51
    priority 50
    advert_int 1
    authentication {
        auth_type PASS
        auth_pass 1111
    }
    virtual_ipaddress {
        192.168.11.100
	} 
}
virtual_server 192.168.11.100 80 {
    delay_loop 6
    lb_algo rr
    lb_kind NAT
    persistence_timeout 50
    protocol TCP
    real_server 192.168.11.161 80 {
        weight 1
        TCP_CHECK {
           connect_timeout 3
           delay_before_retry 3
           connect_port 80
		} 
    }
}
```

##### 3.3 keepalived 日志文件配置

1. 首先看一下/etc/sysconfig/keepalived 文件
   1. vi /etc/sysconfig/keepalived
   2. KEEPALIVED_OPTIONS="-D -d -S 0"
   3. “-D” 就是输出日志的选项
   4. 这里的“-S 0”表示 local0.* 具体的还需要看一下/etc/syslog.conf 文件

2. 在/etc/rsyslog.conf 里添加:local0.* /var/log/keepalived.log
3. 重新启动 keepalived 和 rsyslog 服务:
   1. service rsyslog restart
   2. service keepalived restart

#### 4 通过脚本实现动态切换

1. 在 master 和 slave 节点的 /data/program/nginx/sbin/nginx-ha-check.sh 目录下增加一个脚本
   1. –no-headers 不打印头文件
   2. Wc –l 统计行数

~~~sh
#!bin/sh #! /bin/sh 是指此脚本使用/bin/sh 来执行
A=`ps -C nginx --no-header |wc -l`
if [ $A -eq 0 ]
   then
   echo 'nginx server is died'
   service keepalived stop
fi
~~~

2. 修改 keepalived.conf 文件，增加如下配置
   1. track_script: #执行监控的服务。
   2. chknginxservice #
   3. 引用 VRRP 脚本，即在 vrrp_script 部分指定的名字。定期运行它们来改变优先级，并最终引发主备切换。

![]()

















































