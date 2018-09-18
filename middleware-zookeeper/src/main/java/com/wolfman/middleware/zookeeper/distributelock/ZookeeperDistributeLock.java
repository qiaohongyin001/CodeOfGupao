package com.wolfman.middleware.zookeeper.distributelock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * zookeeper分布式锁
 */
public class ZookeeperDistributeLock implements Lock, Watcher {

    private final static String connectString = "39.107.31.208:2181,39.107.32.43:2181,47.95.39.176:2181";

    private ZooKeeper zooKeeper = null;
    private String ROOT_LOCK = "/locks";    //定义根节点
    private String WAIT_LOCK;   //等待前一个锁
    private String CURRENT_LOCK;    //表示当前的锁

    private CountDownLatch countDownLatch;


    public ZookeeperDistributeLock(){
        try {
            this.zooKeeper = new ZooKeeper(connectString,4000,this);
            //判断根节点是否存在
            Stat stat = zooKeeper.exists(ROOT_LOCK,false);
            if(stat == null){
                zooKeeper.create(ROOT_LOCK,"0".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void lock() {
        if (this.tryLock()){
            System.out.println(Thread.currentThread().getName() + "===" + CURRENT_LOCK + "。获得锁成功。");
            return;
        }
        //没有获得锁，继续等待获得锁
        try {
            waitForLock(WAIT_LOCK);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private boolean waitForLock(String prev) throws KeeperException, InterruptedException {
        //监听当前节点的上一个节点
        Stat stat = zooKeeper.exists(prev,true);
        if (stat!=null){
            System.out.println(Thread.currentThread().getName() + "=== 等待锁：" + ROOT_LOCK + "/" + prev + "释放。");
            countDownLatch = new CountDownLatch(1);
            countDownLatch.await();
            System.out.println(Thread.currentThread().getName() + "=== 获得锁：" + ROOT_LOCK + "/" + prev + "成功。");
        }
        return true;
    }


    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        try {
            //创建临时有序节点
            CURRENT_LOCK = zooKeeper.create(ROOT_LOCK + "/","0".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
            System.out.println(Thread.currentThread().getName() + "===" + CURRENT_LOCK + "。尝试竞争锁。");
            //获得根节点下所有子节点
            List<String> childrens = zooKeeper.getChildren(ROOT_LOCK,false);
            //定义一个集合进行排序
            SortedSet<String> sortedSet = new TreeSet();
            for (String children : childrens) {
                sortedSet.add(ROOT_LOCK + "/" + children);
            }
            //获得当前所有子节点中最小的节点
            String firstNode = sortedSet.first();
            //判断当前节点是否是最小节点
            SortedSet<String> lessThanMe = sortedSet.headSet(CURRENT_LOCK);
            //通过当前的节点和子节点中最小的节点进行比较，如果相等，表示获得锁成功
            if (CURRENT_LOCK.equals(firstNode)){
                return true;
            }
            if (!lessThanMe.isEmpty()){
                //获得比当前节点更小的最后一个节点，这只给WAIT_LOCK
                WAIT_LOCK = lessThanMe.last();
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {
        System.out.println(Thread.currentThread().getName() + "=== 释放锁：" + CURRENT_LOCK);
        try {
            zooKeeper.delete(CURRENT_LOCK,-1);
            CURRENT_LOCK = null;
            zooKeeper.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Condition newCondition() {
        return null;
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if (this.countDownLatch != null){
            this.countDownLatch.countDown();
        }
    }



}
