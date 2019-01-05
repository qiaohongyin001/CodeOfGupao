package com.wolfman.rabbitmq.topic;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ConsumerTopic {

  private final static String QUEUE_NAME = "test_queue_topic_work";

  private final static String EXCHANGE_NAME = "test_exchange_topic";

  public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
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

    // 声明队列
    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
    // 绑定队列到交换机
    channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "key.#");
    // 同一时刻服务器只会发一条消息给消费者
    channel.basicQos(1);
    // 定义队列的消费者
    QueueingConsumer consumer = new QueueingConsumer(channel);
    // 监听队列，手动返回完成
    channel.basicConsume(QUEUE_NAME, false, consumer);
    // 获取消息
    while (true) {
      QueueingConsumer.Delivery delivery = consumer.nextDelivery();
      String message = new String(delivery.getBody());
      System.out.println(" [x] Received '" + message + "'");
      Thread.sleep(10);
      channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
    }

  }


}
