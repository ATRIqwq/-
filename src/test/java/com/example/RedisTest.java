package com.example;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;

@SpringBootTest
public class RedisTest {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void testRedis(){

        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.set("kano","鹿乃");


    }
}
