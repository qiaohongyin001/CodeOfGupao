package com.wolfman.rabbitmq.fanout;

import com.rabbitmq.client.*;
import com.wolfman.rabbitmq.ConnectionUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ConsumerFanoutTwo {


  private final static String EXCHANGE_NAME = "test_exchange_fanout";

  public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
    //建立连接
    Connection conn = ConnectionUtils.getConnection();
    //创建消息通道
    Channel channel = conn.createChannel();

    channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

    //声明默认的队列
    String queue = channel.queueDeclare().getQueue();
    //将队列与交换机绑定，最后一个参数为routingKey,与发送者指定的一样""
    channel.queueBind(queue, EXCHANGE_NAME, "");


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
    channel.basicConsume(queue, true, consumer);

  }


}
