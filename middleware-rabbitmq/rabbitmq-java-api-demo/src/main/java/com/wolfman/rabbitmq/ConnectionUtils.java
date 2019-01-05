package com.wolfman.rabbitmq;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ConnectionUtils {

  public static Connection getConnection(){
    ConnectionFactory factory = new ConnectionFactory();
    //连接ip
    factory.setHost("39.107.31.208");
    //连接端口
    factory.setPort(5672);
    //虚拟机
    factory.setVirtualHost("/");
    //用户
    factory.setUsername("guest");
    factory.setPassword("guest");
    // 获取到连接以及mq通道
    try {
      return factory.newConnection();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (TimeoutException e) {
      e.printStackTrace();
    }
    return null;
  }




}
