package com.wolfman.middleware.kafka.simple;

import com.wolfman.middleware.kafka.KafkaProperties;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.io.IOException;
import java.util.Properties;

public class KafkaSimpleProducer {

    private KafkaProducer<Integer,String> producer;

    public KafkaSimpleProducer() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", KafkaProperties.KAFKA_BROKER_LIST);
        properties.put("key.serializer","org.apache.kafka.common.serialization.IntegerSerializer");
        properties.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer");
        properties.put("client.id","producer-mhxy");
        this.producer = new KafkaProducer<Integer, String>(properties);
    }

    public void sendMsg(){
        //发送消息
        producer.send(new ProducerRecord<Integer, String>(KafkaProperties.TOPIC,1,"message"), new Callback() {
            @Override
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                //回掉
                System.out.println("message send to:["
                        +metadata.partition()+"],offset:["
                        +metadata.offset()+"]");
            }
        });
    }

    public static void main(String[] args) throws IOException {
        KafkaSimpleProducer producer = new KafkaSimpleProducer();
        producer.sendMsg();
        System.in.read();

    }







}
