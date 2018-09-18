package com.wolfman.middleware.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ApiOperatorWathcerDemo implements Watcher {

    private static final String connectString = "39.107.31.208:2181,39.107.32.43:2181,47.95.39.176:2181";

    private static CountDownLatch countDownLatch=new CountDownLatch(1);

    private static ZooKeeper zookeeper;

    private static Stat stat=new Stat();

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {

        zookeeper=new ZooKeeper(connectString, 5000, new ApiOperatorWathcerDemo());
        countDownLatch.await();
//        ACL acl=new ACL(ZooDefs.Perms.ALL,new Id("ip","192.168.11.129"));
//        List<ACL> acls=new ArrayList<ACL>();
//        acls.add(acl);
//        zookeeper.exists("/authTest",true);
//        zookeeper.create("/authTest","111".getBytes(),acls,CreateMode.PERSISTENT);
//        System.in.read();

        String path="/nodeThr881e";
        //创建节点
        String result=zookeeper.create(path,"123".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        zookeeper.getData(path,new ApiOperatorWathcerDemo(),stat); //增加一个
        System.out.println("创建成功："+result);
        TimeUnit.SECONDS.sleep(1);

        Stat stat=zookeeper.exists(path+"/node1",true);
        if(stat==null){//表示节点不存在
            //创建子节点
            String result2 = zookeeper.create(path+"/node1","123".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
            System.out.println("创建成功，得到的结果" + zookeeper.getData(path+"/node1",true,stat));

            TimeUnit.SECONDS.sleep(1);
        }
        //修改子路径
        zookeeper.setData(path+"/node1","mic123".getBytes(),-1);
        System.out.println("修改子路径" + zookeeper.getData(path+"/node1",true,stat));
        TimeUnit.SECONDS.sleep(1);

        //修改子路径
        zookeeper.setData(path+"/node1","mic1222223".getBytes(),-1);
        System.out.println("修改子路径" + zookeeper.getData(path+"/node1",true,stat));
        TimeUnit.SECONDS.sleep(1);

        //获取指定节点下的子节点
        List<String> childrens=zookeeper.getChildren(path,true);
        System.out.println(childrens);
        Thread.sleep(100000);

    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        //如果当前的连接状态是连接成功的，那么通过计数器去控制
        try {
            if (watchedEvent.getState() == Event.KeeperState.SyncConnected){
                countDownLatch.countDown();
                if (watchedEvent.getType() == Event.EventType.NodeDataChanged){
                    //数据变更
                    System.out.println("数据变更触发路径："+watchedEvent.getPath()+"->改变后的值："+
                            zookeeper.getData(watchedEvent.getPath(),true,stat));
                }else if(watchedEvent.getType()== Event.EventType.NodeChildrenChanged){
                    //子节点的数据变化会触发
                    //子节点发生变更： 子节点删除、新增的时候，才会触发
                    System.out.println("子节点数据变更路径："+watchedEvent.getPath()+"->节点的值："+
                            zookeeper.getData(watchedEvent.getPath(),true,stat));
                }else if(watchedEvent.getType()== Event.EventType.NodeCreated) {
                    //创建节点的时候会触发
                    System.out.println("节点创建路径："+watchedEvent.getPath()+"->节点的值："+
                            zookeeper.getData(watchedEvent.getPath(),true,stat));
                }else if(watchedEvent.getType()== Event.EventType.NodeDeleted){
                    //子节点删除会触发
                    System.out.println("节点删除路径："+watchedEvent.getPath());
                }
            }
            System.out.println("state:"+watchedEvent.getState() + " || type:" + watchedEvent.getType());
        }catch (Exception e){
            e.printStackTrace();
        }





    }




}
