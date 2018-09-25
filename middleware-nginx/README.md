## 反向代理 - nginx

**Nginx官方网站：**<http://nginx.org/>

- 反向代理和正向代理

1. 正向代理
   1. 正向代理的对象是客户端
2. 反向代理
   1. 反向代理的对象是服务器

### 一 常见 web 服务器

apache、Nginx、tomcat、weblogic、iis、jboss、websphere、jetty、netty、lighttpd、glassfish、resin。。。

#### 1 Apache

Apache仍然是时长占用量最高的web服务器，据最新数据统计，市场占有率目前是50%左右。主要优势在于一个是比较早出现的一个Http静态资源服务器，同时又是开源的。所以在技术上的支持以及市面上的各种解决方案都比较成熟。Apache支持的模块非常丰富。

#### 2 Lighttpd

Lighttpd其设计目标是提供一个专门针对高性能网站、安全、快速、兼容性好并且灵活的web server环境。特点是：内存开销低、CPU占用率低、性能好、模块丰富。Lighttpd跟Nginx一样，是一款轻量级的Web服务器。跟Nginx的定位类似。

#### 3 tomcat

Tomcat大家都比较熟悉，是一个开源的JSP Servlet容器。

#### 4 Nginx

Nginx是俄罗斯人编写的一款高性能的HTTP和反向代理服务器，在高连接并发的情况下，它能够支持高达50000个并发连接数的响应，但是内存、CPU等系统资源消耗却很低，运行很稳定。目前Nginx在国内很多大型企业都有应用，据最新统计，Nginx的市场占有率已经到33%左右了。而Apache的市场占有率虽然仍然是最高的，但是是呈下降趋势。而Nginx的势头很明显。选择Nginx的理由也很简单：第一，它可以支持5W高并发连接；第二，内存消耗少；第三，成本低，如果采用F5、NetScaler等硬件负载均衡设备的话，需要大几十万。而Nginx是开源的，可以免费使用并且能用于商业用途。

#### 5 Nginx服务器、Apache Http Server、Tomcat之间的关系

1. HTTP服务器本质上也是一种应用程序——它通常运行在服务器之上，绑定服务器的IP地址并监听某一个tcp端口来接收并处理HTTP请求，这样客户端（一般来说是IE, Firefox，Chrome这样的浏览器）就能够通过HTTP协议来获取服务器上的网页（HTML格式）、文档（PDF格式）、音频（MP4格式）、视频（MOV格式）等等资源。

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-nginx/img/nginx1.jpg?raw=true)

2. Apache Tomcat是Apache基金会下的一款开源项目，Tomcat能够动态的生成资源并返回到客户端。
3. Apache HTTP server 和Nginx都能够将某一个文本文件的内容通过HTTP协议返回到客户端，但是这些文本文件的内容是固定的，也就是说什么情况下访问该文本的内容都是完全一样的，这样的资源我们称之为静态资源。动态资源则相反，不同时间、不同客户端所得到的内容是不同的 ； （虽然apache和nginx本身不支持动态页面，但是他们可以集成模块来支持，比如PHP、Python）
4. 如果想要使用java程序来动态生成资源内容，使用apache server和nginx这一类的http服务器是基本做不到。而Java Servlet技术以及衍生出来的（jsp）Java Server Pages技术可以让Java程序也具有处理HTTP请求并且返回内容的能力，而Apache Tomcat正是支持运行Servlet/JSP应用程序的容器

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-nginx/img/nginx2.jpg?raw=true)

5. Tomcat运行在JVM之上，它和HTTP服务器一样，绑定IP地址并监听TCP端口，同时还包含以下职责：
   1. 管理Servlet程序的生命周期
   2. 将URL映射到指定的Servlet进行处理
   3. 与Servlet程序合作处理HTTP请求——根据HTTP请求生成HttpServletResponse对象并传递给Servlet进行处理，将Servlet中的HttpServletResponse对象生成的内容返回给浏览器
   4. 虽然Tomcat也可以认为是HTTP服务器，但通常它仍然会和Nginx配合在一起使用：
   5. 动静态资源分离——运用Nginx的反向代理功能分发请求：所有动态资源的请求交给Tomcat，而静态资源的请求（例如图片、视频、CSS、JavaScript文件等）则直接由Nginx返回到浏览器，这样能大大减轻Tomcat的压力。
   6. 负载均衡，当业务压力增大时，可能一个Tomcat的实例不足以处理，那么这时可以启动多个Tomcat实例进行水平扩展，而Nginx的负载均衡功能可以把请求通过算法分发到各个不同的实例进行处理。

#### 6 Apache HTTP Server和Nginx的关系

1. Apache Http Server是使用比较广泛也是资格最老的web服务器，是Apache基金会下第一个开源的WEB服务器。在Nginx出现之前，大部分企业使用的都是Apache。
2. 在互联网发展初期，流量不是特别大的时候，使用Apache完全满足需求。但是随着互联网的飞速发展，网站的流量以指数及增长，这个时候除了提升硬件性能以外，Apache Http server也开始遇到瓶颈了，于是这个时候Nginx的出现，就是为了解决大型网站高并发设计的，所以对于高并发来说，Nginx有先天的优势。因此Nginx也在慢慢取代Apache Http server。 而Nginx另一个强大的功能就是反向代理，现在大型网站分工详细，哪些服务器处理数据流，哪些处理静态文件，这些谁指挥，一般都是用nginx反向代理到内网服务器，这样就起到了负载均衡分流的作用。再次nginx高度模块化的设计，编写模块相对简单。

### 二 安装及配置

#### 1 安装

1. tar -zxvf 安装包

2. ./configure --prefix=/data/application/nginx 默认安装到/usr/local/nginx

3. make & make install

4. 安装过程中可能出现的问题

5. 1. 缺少pcre的依赖

   2. 1. yum install pcre-devel

   3. 缺少openssl的依赖

   4. 1. yum install openssl-devel

   5.  yum install zlib-devel

#### 2 启动停止

1. ./nginx -c /data/application/nginx/conf/nginx.conf 启动 nginx

2. 1. -c表示指定nginx.conf的文件。如果不指定，默认为home/conf/nginx.conf

3. ./nginx -t

4. 1. 检车nginx配置文件是否有问题

5. 发送信号的方式停止nginx

6. 1. kill -QUIT 进程号 ：优雅的关闭nginx

   2. 1. kill -QUIT 19359

   3. kill -TERM 进程号 ：快速关系nginx

   4. 1. kill -TERM 19359

7. 命令方式停止nginx

8. 1. ./nginx -s stop 停止
   2. ./nginx -s quit 退出
   3. ./nginx -s reload 重新加载nginx.conf

#### 3 nginx 核心配置分析

nginx的核心配置文件包括：Main、Event、Http

#### 4 虚拟主机配置

1. 基于域名的虚拟主机
   1. 修改windows/system32/drivers/etc/hosts
      1. 47.95.39.176 www.abc.com
   2. 修改nginx.conf文件，在http段中增加如下内容

~~~java
server {
    listen       80;
    server_name  www.abc.com;
    location / {
        root   html/domain;
        index  index.html index.htm;
    }
}
~~~

2. 基于端口的虚拟主机

~~~java
server {
    listen       8080;
    server_name  localhost;
    location / {
        root   html/domain;
        index  index.html index.htm;
    }
}
~~~

3. 基于ip的虚拟主机

#### 5 Nginx的日志配置

通过access_log进行日志记录，nginx中有两条是配置日志的：一条是log_format 来设置日志格式 ； 另外一条是access_log。

1. 设置日志格式

~~~java
log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
'$status $body_bytes_sent "$http_referer" '
'"$http_user_agent" "$http_x_forwarded_for"';
~~~

2. 设置日志：log声明 路径及文件名 日志标识

~~~java
access_log  logs/access.log  main;
~~~

3. 例如

~~~java
access_log myformat '$remote_addr - $remote_user';
access_log logs/my.log myformat;
~~~

4. access_log off;关掉日志

#### 6 nginx日志切割

1. 配置好nginx日志
2. Nginx日志按天切割，编写Shell脚本splitLog.sh(记住为splitLog.sh添加可执行权限)

~~~java
# /bin/bash

# 日志保存位置
base_path='/opt/nginx/logs'
# 获取当前年信息和月信息
log_path=$(date -d yesterday +"%Y%m")
# 获取昨天的日信息
day=$(date -d yesterday +"%d")
# 按年月创建文件夹
mkdir -p $base_path/$log_path
# 备份昨天的日志到当月的文件夹
mv $base_path/access.log $base_path/$log_path/access_$day.log
# 输出备份日志文件名
# echo $base_path/$log_path/access_$day.log
# 通过Nginx信号量控制重读日志
kill -USR1 `cat /opt/nginx/logs/nginx.pid`
~~~

3. 添加Linux定时任务

~~~java
crontab -e
# 每天0时1分进行日志分割(建议在02-04点之间,系统负载小)
01 00 * * * /opt/nginx/logs/splitLog.sh  

重启Linux定时任务
crond restart

如果提示以下错误
crond: can't lock /var/run/crond.pid, otherpid may be 4141: 资源暂时不可用
删除/var/run/crond.pid 重新执行命令即可
~~~

4. 手动切割日志

~~~java
mv access.log access.log.20171206
kill -USR1 Nginx 主进程号  让nginx重新生成一个日志文件access.log
~~~















































