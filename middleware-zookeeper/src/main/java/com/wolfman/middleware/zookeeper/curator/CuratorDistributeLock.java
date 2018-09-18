package com.wolfman.middleware.zookeeper.curator;

import com.wolfman.middleware.zookeeper.distributelock.ZookeeperDistributeLock;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * curator 分布式锁
 */
public class CuratorDistributeLock {

    private static final String connectString = "39.107.31.208:2181,39.107.32.43:2181,47.95.39.176:2181";

    CuratorFramework curatorFramework = null;
    public CuratorDistributeLock() {
        this.curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(connectString)
                .sessionTimeoutMs(4000)
                .retryPolicy(new ExponentialBackoffRetry(1000,3))
                .namespace("curator").build();
        curatorFramework.start();
    }

    public void lock() throws Exception {
        InterProcessMutex interProcessMutex = new InterProcessMutex(curatorFramework,"/locks");
        System.out.println(Thread.currentThread().getName() + "===" + interProcessMutex.getParticipantNodes() + "。尝试竞争锁。");
        interProcessMutex.acquire();
        System.out.println(Thread.currentThread().getName() + "===" + "竞争到了");
        interProcessMutex.release();
        System.out.println(Thread.currentThread().getName() + "===" + "释放了到了");

    }


    public static void main(String[] args) throws IOException {

        CountDownLatch countDownLatch = new CountDownLatch(10);
        for (int i =0 ;i<10;i++){
            new Thread(()->{
                try {

                    CuratorDistributeLock curatorDistributeLock = new CuratorDistributeLock();
                    curatorDistributeLock.lock();
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
