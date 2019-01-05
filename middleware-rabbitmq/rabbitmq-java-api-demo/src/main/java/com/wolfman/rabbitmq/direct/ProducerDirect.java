package com.wolfman.rabbitmq.direct;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 生产者
 */
public class ProducerDirect {


  private final static String QUEUE_NAME = "origin_queue_direct";

  public static void main(String[] args) throws IOException, TimeoutException {
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

    //建立连接
    Connection conn = factory.newConnection();
    //创建消息通道
    Channel channel = conn.createChannel();

    String msg = "Hello world, Rabbit MQ aaaaa";

    // 发送消息(发送到默认交换机AMQP Default，Direct)
    // 如果有一个队列名称跟Routing Key相等，那么消息会路由到这个队列
    // String exchange, String routingKey, BasicProperties props, byte[] body
    channel.basicPublish("", QUEUE_NAME, null, msg.getBytes());


    channel.close();
    conn.close();
  }






}
