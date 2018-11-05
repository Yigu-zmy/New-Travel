package com.rookie.util;

import java.util.Properties;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisPoolUtil {
    private static JedisPool jedisPool = null;
    private static String redisConfigFile = "redis.properties";
    private static ThreadLocal<Jedis> local = new ThreadLocal<Jedis>();

    private RedisPoolUtil() {
    }

    public static void initalPool() {
        try {
            Properties properties = new Properties();
            properties.load(RedisPoolUtil.class.getClassLoader().getResourceAsStream(redisConfigFile));
            JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
            jedisPoolConfig.setMaxTotal(Integer.valueOf(properties.getProperty("jedis.pool.maxActive", "8")));
            jedisPoolConfig.setMaxIdle(Integer.valueOf(properties.getProperty("jedis.pool.maxIdle", "8")));
            jedisPoolConfig.setMaxWaitMillis(Long.valueOf(properties.getProperty("jedis.pool.maxWait", "30")));
            jedisPoolConfig.setTestOnBorrow(Boolean.valueOf(properties.getProperty("jedis.pool.testOnBorrow", "false")));
            jedisPoolConfig.setTestOnReturn(Boolean.valueOf(properties.getProperty("jedis.pool.testOnReturn", "false")));

            jedisPool = new JedisPool(jedisPoolConfig, properties.getProperty("redis.ip", "127.0.0.1"),
                    Integer.valueOf(properties.getProperty("redis.port")),
                    Integer.valueOf(properties.getProperty("redis.timeout")));
            System.out.println("redis 线程池初始化成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Jedis getConn() {
        Jedis jedis = local.get();
        if (jedis == null) {
            if (jedisPool == null) {
                initalPool();
            }
            jedis = jedisPool.getResource();
            local.set(jedis);
        }
        return jedis;
    }

    public void closeConn() {
        Jedis jedis = local.get();
        if (jedis != null) {
            jedis.close();
        }
        local.set(null);
    }

    public static void closePool() {
        if (jedisPool != null) {
            jedisPool.close();
        }
    }
}
