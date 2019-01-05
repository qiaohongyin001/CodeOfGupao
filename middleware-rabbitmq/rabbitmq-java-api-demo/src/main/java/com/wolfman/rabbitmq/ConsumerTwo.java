package com.wolfman.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ConsumerTwo {

  private final static String QUEUE_NAME = "origin_queue_topic";

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

    String msg = "Hello world, Rabbit MQ";

    // 声明队列
    // String queue, boolean durable, boolean exclusive, boolean autoDelete,Map<String, Object> arguments
    channel.queueDeclare(QUEUE_NAME,false,false,false,null);
    System.out.println(" Waiting for message....");

    // 创建消费者
    DefaultConsumer consumer = new DefaultConsumer(channel) {
      @Override
      public void handleDelivery(String consumerTag, Envelope envelope,
                                 AMQP.BasicProperties properties, byte[] body) throws IOException {
        String msg = new String(body, "UTF-8");
        System.out.println("Received message : '" + msg + "'");
      }
    };
    // 开始获取消息
    // String queue, boolean autoAck, Consumer callback
    channel.basicConsume(QUEUE_NAME, true, consumer);
  }


}
