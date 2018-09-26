package com.wolfman.middleware.kafka.batchcommit;

import com.wolfman.middleware.kafka.KafkaProperties;
import org.apache.kafka.clients.producer.*;

import java.io.IOException;
import java.util.Properties;

public class KafkaCommitProducer implements Runnable  {

    private KafkaProducer<Integer,String> producer;

    public KafkaCommitProducer() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaProperties.KAFKA_BROKER_LIST);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.IntegerSerializer");
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, "producer-commit");
        this.producer = new KafkaProducer<Integer, String>(properties);
    }

    @Override
    public void run() {
        int messageNo = 0;
        while (true){
            String messageStr="mesage-"+messageNo;
            producer.send(new ProducerRecord<Integer, String>(KafkaProperties.TOPIC, messageNo, messageStr), new Callback() {
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    System.out.println("message send to:["+recordMetadata.partition()+"],offset:["+recordMetadata.offset()+"]");
                }
            });
            ++messageNo;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }



        }
    }


    public static void main(String[] args) throws IOException {
        KafkaCommitProducer producer=new KafkaCommitProducer();
        new Thread(producer).start();
    }



}
