package com.wolfman.rabbitmq.direct;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.wolfman.rabbitmq.ConnectionUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 生产者
 */
public class ProducerDirect {

  public static void main(String[] args) throws IOException, TimeoutException {
    //建立连接
    Connection conn = ConnectionUtils.getConnection();
    //创建消息通道
    Channel channel = conn.createChannel();

    String msg = "Hello world, this is my first direct exchange";

    // 发送消息(发送到默认交换机AMQP Default，Direct)
    // 如果有一个队列名称跟Routing Key相等，那么消息会路由到这个队列
    // String exchange, String routingKey, BasicProperties props, byte[] body
    channel.basicPublish("simple_exchange", "simple.first", null, msg.getBytes());

    channel.close();
    conn.close();
  }






}
