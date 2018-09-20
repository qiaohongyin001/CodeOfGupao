package com.wolfman.middleware.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisManager {


    private static JedisPool jedisPool;


    static {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(10);
        jedisPoolConfig.setMinIdle(10);
        jedisPool = new JedisPool(jedisPoolConfig,"39.107.32.43",6379);
    }

    public static Jedis getJedis() throws Exception {
        if ( null != jedisPool ){
            return  jedisPool.getResource();
        }
        throw new Exception("JedisPoll was not init!");
    }



}
