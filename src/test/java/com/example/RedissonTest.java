package com.example;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@Slf4j

public class RedissonTest {
    @Resource
    private RedissonClient redissonClient;


    @Test
    void watchDogTest(){
        RLock lock = redissonClient.getLock("mysoul:precachejob:docache:lock");

        try {
            // 只有一个线程能获取到锁
            if (lock.tryLock(0,-1, TimeUnit.MILLISECONDS)){
                System.out.println(Thread.currentThread().getName() + "抢到锁了");
                log.info("开始定时任务");
                Thread.sleep(300000);

            }
        } catch (InterruptedException e) {
            log.error("doCacheRecommendUser error ",e);
        } finally {
            // 只能释放自己的锁
            if (lock.isHeldByCurrentThread()){
                System.out.println(Thread.currentThread().getName() + "释放锁了");
                lock.unlock();
            }
        }
    }


}


