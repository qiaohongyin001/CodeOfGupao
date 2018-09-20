package com.wolfman.middleware.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;
import java.util.UUID;

public class RedisDistributeLock {

    private String key;

    private int timeout;

    public RedisDistributeLock(String key, int timeout) {
        this.key = key;
        this.timeout = timeout;
    }

    public String tryLock() {
        try {
            Jedis jedis = RedisManager.getJedis();
            String value = UUID.randomUUID().toString().replace("-","");
            long end = System.currentTimeMillis() + this.timeout;
            while (true){
                if (jedis.setnx(this.key,value) == 1){
                    System.out.println(Thread.currentThread().getName()+"获得了锁，这个锁的值为：" + value);
                    return value;
                }
                System.out.println(Thread.currentThread().getName()+"继续请求获取锁");
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean releaseLock(String value) {
        try {
            Jedis jedis = RedisManager.getJedis();
            while (true){
                jedis.watch(key);
                if (value.equals(jedis.get(key))){
                    Transaction transaction = jedis.multi();
                    transaction.del(key);
                    List<Object> list = transaction.exec();
                    if (list == null){
                        continue;
                    }
                    System.out.println(Thread.currentThread().getName()+"释放了锁，这个锁的值为：" + value);
                    return true;
                }
                jedis.unwatch();
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }





}
