package com.wolfman.rabbitmq.dlx;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.wolfman.rabbitmq.ConnectionUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 生产者 根据消息发送 过期时间测试死信队列
 */
public class DlxProducer {

  public static void main(String[] args) throws IOException, TimeoutException {
    //建立连接
    Connection conn = ConnectionUtils.getConnection();
    //创建消息通道
    Channel channel = conn.createChannel();

    // 设置属性，消息10秒钟过期
    AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
        .deliveryMode(2) // 持久化消息
        .contentEncoding("UTF-8")
        .expiration("100") // TTL
        .build();
    String msg = "Hello world, Rabbit MQ, DLX MSG";

    // 发送消息
    for (int i=0; i<10; i++){
      channel.basicPublish("kawayi", "ka", properties, msg.getBytes());
    }

    channel.close();
    conn.close();
  }






}
