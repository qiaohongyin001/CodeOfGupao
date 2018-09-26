package com.wolfman.middleware.kafka.subarea;

import kafka.server.ReplicaManager;
import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.utils.Time;

import java.util.List;
import java.util.Map;

public class MyPartition implements Partitioner  {


    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        List<PartitionInfo> partitionInfoList = cluster.partitionsForTopic(topic);
        int numPart = partitionInfoList.size();//获得所有的分区
        int hashCode=key.hashCode(); //获得key的 hashcode
        return 0;
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {

    }


}
