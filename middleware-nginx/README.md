## 反向代理 - nginx

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

