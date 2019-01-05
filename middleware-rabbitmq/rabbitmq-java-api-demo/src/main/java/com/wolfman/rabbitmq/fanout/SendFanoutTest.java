package com.wolfman.rabbitmq.fanout;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class SendFanoutTest {

  private final static String EXCHANGE_NAME = "test_exchange_fanout";

  public static void main(String[] argv) throws Exception {
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
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();
    // 声明exchange
    channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
    // 消息内容
    String message = "Hello World!";
    channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes());
    System.out.println(" [x] Sent '" + message + "'");
    channel.close();
    connection.close();
  }




}
