## 反向代理 - nginx

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

