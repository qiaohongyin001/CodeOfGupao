package com.wolfman.middleware.zookeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * zookeeper 创建会话连接
 */
public class CreateSessionDemo {

    private static final String connectString = "39.107.31.208:2181,39.107.32.43:2181,47.95.39.176:2181";

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    public static void main(String[] args) throws IOException, InterruptedException {
        ZooKeeper zookeeper = new ZooKeeper(connectString, 5000, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                //如果当前的连接状态是连接成功的，那么通过计数器去控制
                if (watchedEvent.getState() == Event.KeeperState.SyncConnected){
                    countDownLatch.countDown();
                    System.out.println(watchedEvent.getState());
                }
            }
        });
        countDownLatch.await();
        System.out.println(zookeeper.getState());
        zookeeper.close();
        System.out.println(zookeeper.getState());
    }

}
