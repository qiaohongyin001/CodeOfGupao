package com.wolfman.middleware.zookeeper.distributelock;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class Test {

    public static void main(String[] args) throws IOException {
        CountDownLatch countDownLatch = new CountDownLatch(10);
        for (int i =0 ;i<10;i++){
            new Thread(()->{
                try {
                    countDownLatch.await();
//                    DistributeLockDemo distributeLockDemo = new DistributeLockDemo();
//                    distributeLockDemo.lock();
//                    distributeLockDemo.unlock();
                    ZookeeperDistributeLock zookeeperDistributeLock = new ZookeeperDistributeLock();
                    zookeeperDistributeLock.lock();
                    zookeeperDistributeLock.unlock();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            },"Thread-"+i).start();
            countDownLatch.countDown();

        }
        System.in.read();
    }

}
