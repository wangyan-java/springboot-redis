package com.wy.springbootredis.tran;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashMap;
import java.util.Map;

public class RedisUtil {
    //将JedisPool放入map缓存起来
    private static Map<String, JedisPool> jedisPoolMap = new HashMap<String, JedisPool>();
    
    /**
     * 创建连接池
     * @return
     */
    private static void createJedisPool(String url,int port){
         
        JedisPoolConfig config = new JedisPoolConfig();
        //设置连接参数
        config.setMaxIdle(10);  
        // 最大连接数, 默认8个  
        config.setMaxTotal(200);  
        // 表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；  
        config.setMaxWaitMillis(1000); 
        //将连接池放入map缓存
        jedisPoolMap.put(url+port, new JedisPool(config, url, port));
    }
    
    /**
     * 多线程初始化redis线程池
     */
    private static synchronized void initPool(String url,int port){
        if (!jedisPoolMap.containsKey(url+port)){
            createJedisPool(url,port);
        }
    }
    
    /**
     * 获取jedis实例
     */
    public static Jedis getJedis(String url, int port, String auth){
        initPool(url, port);
        try {
            Jedis jedis = jedisPoolMap.get(url + port).getResource();
            if (auth != null){
                jedis.auth(auth);
            }
            return jedis;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
     /** 
     * 释放jedis资源 
     *  
     * @param jedis 
     */  
    public static void close(Jedis jedis) {  
        if (null != jedis) {  
            jedis.close();  
        }  
    }
    
    /**
     * 
     * @param args String
     * @throws Exception Exception
     */
    public static void main(String[] args) throws Exception  {

        Jedis jedis = RedisUtil.getJedis("10.10.50.50", 6379, "peiWWU3y!Zc#giKp");
        //jedis.hgetAll("kpclientcfg");
        System.out.println(jedis.keys("*"));
    }
}