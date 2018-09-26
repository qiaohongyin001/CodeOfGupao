package com.wolfman.middleware.kafka.batchcommit;

import com.wolfman.middleware.kafka.KafkaProperties;
import kafka.utils.ShutdownableThread;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class KafkaCommitConsumer extends ShutdownableThread {

    private final KafkaConsumer<Integer,String> consumer;

    Logger log = LoggerFactory.getLogger(this.getClass());

    private List<ConsumerRecord> buffer=new ArrayList<ConsumerRecord>();


    public KafkaCommitConsumer() {
        super("KafkaConsumer-WDD",false);
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaProperties.KAFKA_BROKER_LIST);
        //GroupId 消息所属的分组，不同消费者设置不同消费分组则为发布订阅
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "wdd");
        //是否自动提交消息：offset
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,"false");
        //自动提交的间隔时间
        properties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG,"1000");
        //设置使用最开始的offset偏移量为当前group.id的最早消息
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");
        //设置心跳时间
        properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG,"30000");
        //对key和value设置反序列化对象
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.IntegerDeserializer");
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        this.consumer = new KafkaConsumer<Integer, String>(properties);
    }

    @Override
    public void doWork() {
        consumer.subscribe(Collections.singletonList(KafkaProperties.TOPIC));
        ConsumerRecords<Integer,String> records=consumer.poll(1000);
        for(ConsumerRecord record:records){
            System.out.println("["+record.partition()+"]receiver message:" +
                    "["+record.key()+"->"+record.value()+"],offset:"+record.offset()+"");
            buffer.add(record);
        }
        //每隔多少条进行一次异步提交
        if(buffer.size()>=5){
            System.out.println("Begin Excecute Commit Offset Operation");
            //log.info("Begin Excecute Commit Offset Operation");
            consumer.commitAsync();
            buffer.clear();
        }
    }

    public static void main(String[] args) {
        KafkaCommitConsumer consumer = new KafkaCommitConsumer();
        consumer.start();

    }

}
