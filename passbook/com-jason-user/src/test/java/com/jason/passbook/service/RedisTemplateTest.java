package com.jason.passbook.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 客户端测试: Spring redis starter
 *
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTemplateTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void testRedisTemplate(){
        //redis flushall <- 慎重!!
        /*redisTemplate.execute((RedisCallback<Object>) connection ->{
            connection.flushAll();
            return null;
        });*/

        //assert redisTemplate.opsForValue().get("name") == null;

        redisTemplate.opsForValue().set("name", "jason");

        assert redisTemplate.opsForValue().get("name") != null;

        System.out.println(
                redisTemplate.opsForValue().get("name")
        );
    }
}
