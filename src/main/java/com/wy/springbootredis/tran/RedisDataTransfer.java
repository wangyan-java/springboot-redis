package com.wy.springbootredis.tran;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 单机 redis 迁移
 */
public class RedisDataTransfer {
    /**
     * @param srcDbIndex 源数据库索引
     * @param targetDbIndex 目标数据库索引
     */
    public static void copyRedisData(int srcDbIndex,int targetDbIndex){
        Jedis jedisSrc = RedisUtil.getJedis("13.14.100.65", 6379, null);
        Jedis jedisTarget = RedisUtil.getJedis("127.0.0.1", 6379, null);
        //这里选择数据库db0
        jedisSrc.select(srcDbIndex);
        jedisTarget.select(targetDbIndex);
        //获取源数据库所有“表”
        Set<String> allKeys = jedisSrc.keys("*");
        //System.out.println(allKeys);
        
        Iterator<String> it = allKeys.iterator();
        int count = 0;
        int successCount=0;
        while(it.hasNext()){
            count ++;
            String key = it.next();

            //获取key（“表对应的类型”）我们知道，redis数据有五种类型，分别是string,hash,list,set,zset
            String keyType = jedisSrc.type(key);
            if (keyType.equals("string")){
                String data = jedisSrc.get(key);
                jedisTarget.set(key, data);
                successCount++;
            }
            else if (keyType.equals("hash")){
                Map<String, String> data = jedisSrc.hgetAll(key);
                for (Map.Entry<String, String> entry: data.entrySet()){
                    jedisTarget.hset(key, entry.getKey(),entry.getValue());
                }
                successCount ++;
            }
            else if (keyType.equals("zset")){
                Set<Tuple> value = jedisSrc.zrangeWithScores(key, 0, -1);
                for (Tuple entry : value) {
                    jedisTarget.zadd(key, entry.getScore(), entry.getElement());
                }
                successCount ++;
            }
            else if (keyType.equals("set")){
                 Set<String> smembers = jedisSrc.smembers(key);
                 for (String member : smembers){
                     jedisTarget.sadd(key, member);
                 }
                 successCount ++;
            }
            else if (keyType.equals("list")){
                List<String> list = jedisSrc.lrange(key, 0 ,-1);
                for (String value : list){
                    jedisTarget.lpush(key, value);
                }
                successCount ++;
            }
            System.out.println("总复制数："+count+","+"复制成功数："+successCount);
        }
        System.out.println(count+","+successCount);
    }
    public static void main(String[] args) {
        copyRedisData(0,0);
    }
}