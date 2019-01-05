package com.wolfman.rabbitmq.topic;

import com.rabbitmq.client.*;
import com.wolfman.rabbitmq.ConnectionUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ConsumerTopicTwo {

  private final static String QUEUE_NAME = "test_queue_topic_work2";

  private final static String EXCHANGE_NAME = "test_exchange_topic";

  public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
    //建立连接
    Connection conn = ConnectionUtils.getConnection();
    //创建消息通道
    Channel channel = conn.createChannel();
    // 声明队列
    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
    // 绑定队列到交换机
    channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "key.1.2");
    // 同一时刻服务器只会发一条消息给消费者
    channel.basicQos(1);


    // 声明队列
    // String queue, boolean durable, boolean exclusive, boolean autoDelete,Map<String, Object> arguments
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
