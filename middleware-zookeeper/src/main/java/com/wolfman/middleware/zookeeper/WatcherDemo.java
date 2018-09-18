package com.wolfman.middleware.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class WatcherDemo {

    private final static String CONNECTSTRING = "39.107.31.208:2181,39.107.32.43:2181,47.95.39.176:2181";

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        final ZooKeeper zooKeeper = new ZooKeeper(CONNECTSTRING, 5000, new Watcher() {
            public void process(WatchedEvent event) {
                System.out.println("默认事件" + event.getType() + "->" + event.getPath());
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    countDownLatch.countDown();
                    System.out.println("SyncConnected:" + event.getState());
                }
                System.out.println(event.getState());
            }
        });
        countDownLatch.await();

        zooKeeper.create("/huhao-learn2", "1".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        // exists getData getChildren
        //通过exists绑定事件
        Stat stat = zooKeeper.exists("/huhao-learn2", new Watcher() {
            public void process(WatchedEvent event) {
                System.out.println(event.getType() + "->" + event.getPath());
                try {
                    //再次绑定事件
                    zooKeeper.exists(event.getPath(),true);
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        //通过修改的事务类型操作来触发监听事件
        stat = zooKeeper.setData("/huhao-learn2","2".getBytes(),stat.getVersion());

        Thread.sleep(1000);

        zooKeeper.delete("/huhao-learn2",stat.getVersion());

        System.in.read();


    }



}
