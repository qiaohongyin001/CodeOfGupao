## 反向代理 - nginx

**Nginx官方网站：**<http://nginx.org/>

Nginx安装包下载 http://nginx.org/download/nginx-1.13.6.tar.gz

keepalived 安装包下载http://www.keepalived.org/software/keepalived-1.3.2.tar.gz

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

### 三 location 的语法和配置规则

#### 1 语法

~~~java
location [~|=|^~|~*] /uri {   }
  1. =：精准匹配
  2. ^~：普通匹配
  3. ~：执行一个正则规则匹配，区分大小写
  4. ~*：正则匹配，不区分大小写
~~~

#### 2 匹配规则

1. 精准匹配

2. 1. localhost=/uri{	}
   2. 优先级最高的匹配规则

3. 一般匹配

4. 1. localhost /uri{	}
   2. 普通匹配的优先级要高于正则匹配，如果存在多个相同的前缀的一般匹配，那么最终会按照最大长度来做匹配。

5. 正则匹配

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-nginx/img/nginx3.jpg?raw=true)

### 四 rewrite 的使用

rewrite通过ngx_http_rewrite_module模块支持url重写、支持if判断，但不支持else。

rewrite功能就是使用nginx提供的全局变量或自己设置的变量，结合正则表达式和标志位实现url重写以及重定向。

rewrite只能放在server{ }，location{ }，if{ }中，并且只能对域名后边的除去传递的参数外的字符串起作用。

#### 1 常用指令

1. if 空格（条件）{设定条件进行重写}

2. 1. 条件的语法

   2. 1. “=” 来判断相等，用于字符比较
      2. “~” 用正则来匹配（表示区分大小写），“~*” 不区分大小写
      3. “-f -d -e” 来判断是否为文件、目录、是否存在

3. return指令

4. 1. 语法：return code
   2. 停止处理并返回指定状态码给客户端

~~~java
if ($request_uri ~ *\.sh ){
    return 403
}
~~~

3. set指令

1. 1. set variable value;
   2. 定义一个变量并复制，值可以是文本、变量或者文本变量混合体

4. rewrite指令
   1. 语法：rewrite regex replacement [flag]{last / break/redirect返回临时302/ permant 返回永久302}
   2. last: 停止处理后续的rewrite指令集、 然后对当前重写的uri在rewrite指令集上重新查找
   3. break; 停止处理后续的rewrite指令集 ,并不会重新查找

5. 综合实例

配置对于：/images/ttt/test.png 会重写到 /mic?file=test.png，于是匹配到 location /mic；通过try_files获取存在的文件进行返回。最后由于文件不存在所以直接返回404错误。

~~~nginx
location / {
    rewrite '^/images/([a-z]{3})/(.*)\.(png|jpg)$' /mic?file=$2.$3;
    set $image_file $2;
    set $image_type $3;
}
location /mic {
    root html;
    try_files /$arg_file/image404.html;
}
location =/image404.html{
    return 404 "image not found exception"
}
~~~

#### 2 rewrite 匹配规则

表面看rewrite和location功能有点像，都能实现跳转，主要区别在于rewrite是在同一域名内更改获取资源的路径，而location是对一类路径做控制访问或反向代理，可以proxy_pass到其他机器。

很多情况下rewrite也会写在location里，它们的执行顺序是：

1. 执行server块的rewrite指令
2. 执行location匹配
3. 执行选定的location中的rewrite指令

如果其中某步URI被重写，则重新循环执行1-3，直到找到真实存在的文件；循环超过10次，则返回500 Internal Server Error错误。

### 五 浏览器本地缓存配置及动静分离

#### 1 语法

expires 60s|m|h|d

#### 2 操作步骤

1. 在html目录下创建一个images文件夹，在该文件中放一张图片
2. 修改index.html，增加<img src="图片">
3. 修改nginx.conf配置。配置两个location实现动静分离，并且在静态文件中增加expires的缓存期限。

~~~nginx
server{
    listen 80；
    server_name localhost;
    location / {
        root html;
        index index.html index.htm;
    }
    location ~ \.(png|jpg|js|css|gif)$ {
        root html/images;
        expires 5m;
    }
}
~~~

### 六 Gzip压缩策略

浏览器请求 -> 告诉服务端当前浏览器可以支持压缩类型->服务端会把内容根据浏览器所支持的压缩策略去进行压缩返回->浏览器拿到数据以后解码；

常见的压缩方式：gzip、deflate 、sdch

~~~nginx
server{
    listen 80；
    server_name localhost;
    gzip on;
    gzip_buffers 4 16k;
    gzip_comp_level 7;
    gzip_min_length 500;
    gzip_types text/css text/xml application/javascript;
    
    location / {
        root html;
        index index.html index.htm;
    }
    location ~ \.(png|jpg|js|css|gif)$ {
        root html/images;
        expires 5m;
    }
}
~~~

#### 1 指令说明

1. Gzip on|off

2. 1. 是否开启gzip压缩

3. Gzip_buffers 4 16k 

4. 1. 设置系统获取几个单位的缓存用于存储gzip的压缩结果数据流。4 16k代表以16k为单位，安装原始数据大小以16k为单位的4倍申请内存。

5. Gzip_comp_level[1-9] 

6. 1. 压缩级别， 级别越高，压缩越小，但是会占用CPU资源

7. Gzip_disable

8. 1. 正则匹配UA 表示什么样的浏览器不进行gzip

9. Gzip_min_length

10. 1. 开始压缩的最小长度（小于多少就不做压缩）

11. Gzip_http_version 1.0|1.1

12. 1. 表示开始压缩的http协议版本

13. Gzip_proxied（nginx 做前端代理时启用该选项，表示无论后端服务器的headers头返回什么信息，都无条件启用压缩）

14. Gzip_type text/pliain,application/xml 

15. 1. 对那些类型的文件做压缩 （conf/mime.conf）

16. Gzip_vary on|off 

17. 1. 是否传输gzip压缩标识

#### 2 注意点

1. 图片、mp3这样的二进制文件，没必要做压缩处理，因为这类文件压缩比很小，压缩过程会耗费CPU资源。
2. 太小的文件没必要压缩，因为压缩以后会增加一些头信息，反而导致文件变大。
3. Nginx默认只对text/html进行压缩 ，如果要对html之外的内容进行压缩传输，我们需要手动来配置。

### 七 反向代理

配置：proxy_pass

通过反向代理把请求转发到百度上

~~~nginx
server{
    listen 80；
    server_name www.gupao.com;
   
    location =/s {
        proxy_pass http://www.baidu.com
    }
    
}
~~~

1. proxy_pass 即可以是ip地址，也可以是域名，同时还可以指定端口。

2. proxy_pass 指定的地址携带了URI，看我们前面的配置【/s】，那么这里的URI将会替换请求URI中匹配location参数部分

3. 如上代码将会访问到<http://www.baidu.com/s>

4. interface_version

5. 动态代理提取出来的内容，需要conf通过include进行加载

6. 1. include /etc/nginx/conf.d/*.conf

7. 本地网IP

~~~java
String remoteAddr = request.getRemoteAddr();
String ngip = request.getHeader("X-Real_IP");
~~~

7. nginx配置真实IP

~~~nginx
proxy_set_header X-Real_IP $remote_addr;
~~~

8. 允许header参数中带下划线，设置再http段中

~~~nginx
undrscores_in_header on
~~~

### 八 负载均衡

upstream 是 Nginx 的 HTTP Upstream 模块，这个模块通过一个简单的调度算法来实现客户端IP到后端服务器的负载均衡。

1. **语法：**server address [parameters]

2. 关键字server必选

3. address也必选，可以是主机名、域名、ip或unix socket，也可以指定端口号。
4. parameters是可选参数，可以是如下参数
   1. down：表示当前server已停用
   2. backup：表示当前server是备用服务器，只有其他非backup后端度武器都挂了或者很忙才会分配到请求。
   3. weight：表示当前server负载权重，权重越大被请求几率越大。默认是1
   4. max_fails和fail_timeout一般会关联使用：如果某台server在fail_timeout时间内出现了max_fails次连接失败，那么nginx会认为其已经挂掉了，从而fail_timeout时间内不再去请求它，fail_timeout默认是10s，max_fails默认是1，即默认情况是只要发生错误就认为服务器挂掉了，如果将max_fails设置为0，则表示取消这项检查。
   5. ups支持的调度算法
   6. ip_hash

~~~nginx
1.根据ip的hash值来做转发
upstream tomcatserver{
    ip_hash;
    server 192.168.11.140:8080;
    server 192.168.11.142:8080;
}
2.默认是轮询机制：可以加权重 weight=x
upstream tomcatserver{
    ip_hash;
    server 192.168.11.140:8080;
    server 192.168.11.142:8080 weight = 4;
}
~~~

- 7. fair：根据服务器的响应时间来分配请求
  8. url_hash

### 九 实战演练

gupao-protal 首页

tomcat1 / tomcat2

nginx配置对应的文件 ; /etc/nginx/conf.d/*.conf

upstream.conf  用来配置负载均衡的服务

[www.gupao.com.conf](http://www.gupao.com.conf) 用来配置host信息

配置信息请参考对应的文件，在git上

1. upstream.conf
2. [www.gupao.com.conf](http://www.gupao.com.conf)
3. [nginx.conf](http://www.gupao.com.conf)

详见代码中配置文件

~~~nginx
配置说明
worker_rlimit_nofile 10240;  #too many open files
main：nginx最大文件打开数
use epoll;#select pool epoll kqueue
events：IO模型
worker_connections  10240;
events：最大并发连接数
accept_mutex off;
events：惊群效应。只有一个worker来处理这个请求
include  mime.types;
default_type  application/octet-stream;
http：加载文件映射表，如果找不到，则为default_type
~~~

### 十 进程模型

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

~~~nginx
设置线程数
worker_processes 4;
worker_cpu_affinity 0001 0010 0100 1000;
~~~

### 十一 配置 https 请求

1. https基于SSL/TLS这个协议
2. 非对称加密、对称解密、hash算法
3. crt的证书->返回给浏览器

#### 1 创建证书

1. 创建服务器私钥

2. 1. openssl genrsa -des3 -out server.key 1024

3. 创建签名请求的证书（csr）; csr核心内容是一个公钥

4. 1. openssl req -new -key server.key -out server.csr

5. 去除使用私钥时的口令验证

6. 1.  cp server.key server.key.org
   2. openssl rsa -in server.key.org -out server.key

7. 标记证书使用私钥和csr

8. 1. openssl x509 -req -days 365 -in server.csr -signkey server.key -out server.crt
   2. x509是一种证书格式

9. server.crt就是我们需要的证书

#### 2 nginx 中的配置

配置详见代码中：www.gupao.com.conf 配置文件

#### 3 tomcat 增加对 https 的支持

1. Connector 8080节点加入 redirectPort="443" proxyPort="443"
2. redirectPort ：当http请求有安全约束才会转到443端口使用ssl传输

### 十二 nginx+keepalived 高可用

keepalived -> VRRP（虚拟路由器冗余协议）

VRRP 全称 Virtual Router Redundancy Protocol，即虚拟路由器冗余协议。可以认为他是实现路由器高可用的容错协议，即将N台提供相同功能的路由器组成一个路由器组（Router Group），这个组里面有一个master和多个backup，但是外界看来就像一台一样，构成虚拟路由器，拥有一个虚拟IP（vip，也就是路由器所在局域网内其他机器默认路由），占有这个IP的master实际负责ARP响应和转发IP数据包，组中的其他路由器作为备份的角色处于待命状态。master会发组播消息，当backup在超时时间内收不到vrrp包时，就认为master宕掉了，这时就需要根据VRRP的优先级来选举一个backup当master，保证路由器的高可用。

#### 1 安装 keepalived

1. tar -zxvf keepalived.tar.gz

2. ./configure --prefix=/mic/data/program/keepalived --sysconf=/etc

3. 缺少依赖

4. 1. yum install gcc； 
   2. yum install openssl-devel；
   3. yum -y install libnl libnl-devel；

5. 编译安装

6. 1. make && make install

7. cd 到解压的包 /parker/data/program/keepalived-1.3.9

8. ln -s /mic/data/program/keepalived/sbin/keepalived /sbin --建立软链接

9. cp /mic/data/program/keepalived-1.3.9/keepalived/etc/init.d/keepalived /etc/init.d/

10. 添加到系统服务

11. 1. chkconfig --add keepalived
    2. chkconfig keepalived on
    3. Service keepalived start

#### 2 基于 keepalived + nginx 的配置 

keepalived.conf：

~~~nginx
! Configuration File for keepalived

global_defs {
   router_id LVS_DEVEL
}

vrrp_instance VI_1 {
    state MASTER
    interface ens33
    virtual_router_id 51
    priority 100
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

    real_server 192.168.11.140 80 {
        weight 1
        TCP_CHECK {
          connect_timeout 3
          delay_before_retry 3
          connect_port 80
        }
    }
}
~~~

![](https://github.com/wolfJava/wolfman-middleware/blob/master/middleware-nginx/img/nginx4.jpg?raw=true)











