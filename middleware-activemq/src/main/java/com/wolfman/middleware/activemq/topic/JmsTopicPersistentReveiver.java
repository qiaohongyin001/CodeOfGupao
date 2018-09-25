package com.wolfman.middleware.activemq.topic;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.ConnectionFactory;
import javax.jms.*;

/**
 * 持久化订阅
 */
public class JmsTopicPersistentReveiver {

    public static void main(String[] args) {

        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("" +
                "tcp://39.107.31.208:61616");
        Connection connection = null;

        try {
            connection = connectionFactory.createConnection();
            connection.setClientID("wjt"); //设置持久订阅
            connection.start();
            Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
            //创建队列（如果队列已经存在则不会创建， first-queue是队列名称）
            //destination表示目的地
            Topic topic = session.createTopic("bangpai-xs");
            //创建消息接收者
//            MessageConsumer consumer = session.createConsumer(destination);
            MessageConsumer consumer = session.createDurableSubscriber(topic,"DUBBO-ORDER");
            while (true){
                TextMessage textMessage = (TextMessage) consumer.receive();
                System.out.println(textMessage.getText());
            }
//            TextMessage textMessage = (TextMessage) consumer.receive();
//            System.out.println(textMessage.getText());
//            session.close();

        } catch (JMSException e) {
            e.printStackTrace();
        }finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }


    }

}
