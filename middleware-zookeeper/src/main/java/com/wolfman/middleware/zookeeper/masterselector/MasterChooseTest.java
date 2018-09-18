package com.wolfman.middleware.zookeeper.masterselector;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MasterChooseTest {

    private final static String connectUrl = "39.107.31.208:2181,39.107.32.43:2181,47.95.39.176:2181";



    public static void main(String[] args) throws IOException {
        List<MasterSelector> selectorLists=new ArrayList<>();
        try {
            for(int i=0;i<10;i++) {
                ZkClient zkClient = new ZkClient(connectUrl, 100000,
                        5000,
                        new SerializableSerializer());
                UserCenter userCenter = new UserCenter();
                userCenter.setMc_id(i);
                userCenter.setMc_name("客户端：" + i);
                MasterSelector selector = new MasterSelector(userCenter,zkClient);
                selectorLists.add(selector);
                selector.start();//触发选举操作
                TimeUnit.SECONDS.sleep(1);
            }
            //TimeUnit.SECONDS.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.in.read();
    }


}
