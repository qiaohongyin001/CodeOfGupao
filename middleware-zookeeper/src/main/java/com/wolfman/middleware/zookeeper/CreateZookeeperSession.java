package com.wolfman.middleware.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * 创建连接，对节点进行增删改查
 */
public class CreateZookeeperSession {

    private static final String connectString = "39.107.31.208:2181,39.107.32.43:2181,47.95.39.176:2181";

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    public static void main(String[] args) {
        try {
            ZooKeeper zooKeeper = new ZooKeeper(connectString, 5000, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if (watchedEvent.getState() == Event.KeeperState.SyncConnected){
                        countDownLatch.countDown();
                        System.out.println("我已经连接到zookeeper，状态为：" + watchedEvent.getState());
                    }
                }
            });
            countDownLatch.await();
            //添加节点
            zooKeeper.create("/huhao-learn","save".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            Thread.sleep(1000);
            Stat stat = new Stat();
            //获取节点
            byte[] data = zooKeeper.getData("/huhao-learn",null,stat);
            System.out.println("获取到的数据为：" + new String(data));

            //修改节点
            zooKeeper.setData("/huhao-learn","update".getBytes(),stat.getVersion());
            //获取节点
            byte[] data1 = zooKeeper.getData("/huhao-learn",null,stat);
            System.out.println("获取到的数据为：" + new String(data1));
            //删除节点
            zooKeeper.delete("/huhao-learn",stat.getVersion());
            System.out.println(zooKeeper.getState());
            zooKeeper.close();
            System.out.println(zooKeeper.getState());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }


    }













}
