## nginx 应用实战

### 一 反向代理



配置：proxy_pass

通过反向代理把请求转发到百度上

```nginx
server{
    listen 80；
    server_name www.gupao.com;
   
    location =/s {
        proxy_pass http://www.baidu.com
    }
    
}
```

1. proxy_pass 即可以是ip地址，也可以是域名，同时还可以指定端口。
2. proxy_pass 指定的地址携带了URI，看我们前面的配置【/s】，那么这里的URI将会替换请求URI中匹配location参数部分
3. 如上代码将会访问到<http://www.baidu.com/s>
4. interface_version
5. 动态代理提取出来的内容，需要conf通过include进行加载
6. 1. include /etc/nginx/conf.d/*.conf
7. 本地网IP

```java
String remoteAddr = request.getRemoteAddr();
String ngip = request.getHeader("X-Real_IP");
```

1. nginx配置真实IP

```nginx
proxy_set_header X-Real_IP $remote_addr;
```

1. 允许header参数中带下划线，设置再http段中

```nginx
undrscores_in_header on
```

### 