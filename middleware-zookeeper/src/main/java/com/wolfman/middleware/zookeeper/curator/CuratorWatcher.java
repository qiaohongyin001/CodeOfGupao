package com.wolfman.middleware.zookeeper.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;


/**
 * curator 监听事件
 */
public class CuratorWatcher {

    private final static String CONNECTSTRING = "39.107.31.208:2181,39.107.32.43:2181,47.95.39.176:2181";

    public static void main(String[] args) throws Exception {

        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(CONNECTSTRING)
                .sessionTimeoutMs(4000)
                .retryPolicy(new ExponentialBackoffRetry(1000,3))
                .namespace("curator").build();
        curatorFramework.start();

        /**
         * PathChildCache 监听一个节点下子节点的创建、删除、更新
         * NodeCache    监听一个节点的更新、创建时间
         * TreeCache    综合PathChildCache和NodeCache特性
         */

        //当前节点的创建和删除事件监听
        addListnerWithNodeCache(curatorFramework,"/wolf");



        //自己点的增加、修改、删除的事件监听
//        addListnerWithPathChildCache(curatorFramework,"/wolf");

        //综合节点监听事件
//        addListnerWithTreeCache(curatorFramework,"/aaa");
        System.in.read();


    }

    public static void addListnerWithTreeCache(CuratorFramework curatorFramework, String path) throws Exception {
        final TreeCache treeCache = new TreeCache(curatorFramework,path);

        TreeCacheListener treeCacheListener = new TreeCacheListener() {
            public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
                System.out.print(event.getType() + "-》");
                if (event.getData() != null){
                    System.out.println(event.getData().getPath());
                }
            }
        };
        treeCache.getListenable().addListener(treeCacheListener);
        treeCache.start();
    }


    public static void addListnerWithPathChildCache(CuratorFramework curatorFramework, String path) throws Exception {
        final PathChildrenCache pathChildrenCache = new PathChildrenCache(curatorFramework,path,true);

        PathChildrenCacheListener pathChildrenCacheListener = new PathChildrenCacheListener() {
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                System.out.println("Receive Event:" + event.getType());
            }
        };
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        pathChildrenCache.start(PathChildrenCache.StartMode.NORMAL);
    }




    public static void addListnerWithNodeCache(CuratorFramework curatorFramework, String path) throws Exception {
        final NodeCache nodeCache = new NodeCache(curatorFramework,path,false);
        NodeCacheListener nodeCacheListener = new NodeCacheListener() {
            public void nodeChanged() throws Exception {
                System.out.println("Receive Event:" + nodeCache.getCurrentData().getPath());
            }
        };
        nodeCache.getListenable().addListener(nodeCacheListener);
        nodeCache.start();
    }







}
