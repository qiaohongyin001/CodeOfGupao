package com.wolfman.middleware.activemq.spring;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;

import java.io.IOException;

public class SpringJmsReceiver {

    public static void main(String[] args) throws IOException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:service-jms.xml");

        JmsTemplate jmsTemplate = (JmsTemplate) context.getBean("jmsTemplate");
        System.in.read();
        /*while (true){
            String msg=(String)jmsTemplate.receiveAndConvert();
            System.out.println(msg);
        }*/


    }




}
