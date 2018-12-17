package com.wy.springbootredis.test;

import com.wy.springbootredis.redisutils.RedisUtils;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author wangy
 * @Description:
 * @date 2018/11/6
 */
@SpringBootTest
@RunWith(SpringRunner.class)
//@WebAppConfiguration
public class Test {

    @Autowired
    private RedisUtils redisUtils;

    @org.junit.Test
    public void test(){

//        boolean a = redisUtils.set("1", "A");

//        System.out.println(a);

        Object o = redisUtils.get("d5481e4a4d544c08b4e6aab5231988fc");

        System.out.println(o);

//        redisUtils.remove("1");

    }

}
