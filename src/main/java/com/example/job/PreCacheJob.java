package com.example.job;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.module.domain.User;
import com.example.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Slf4j
public class PreCacheJob {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Resource
    private UserService userService;
    @Resource
    private RedissonClient redissonClient;


    //核心用户
    private  List<Long> mainUserList = Arrays.asList(1L,2L);



    //每天执行，预热推荐用户 秒-分-时-日-月-年
    @Scheduled(cron = "0 7 16 * * *")
    public void doCacheRecommendUsers(){
        RLock lock = redissonClient.getLock("mysoul:precachejob:docache:lock");

        try {
            // 只有一个线程能获取到锁
            if (lock.tryLock(0,-1,TimeUnit.MILLISECONDS)){
                log.info("开始定时任务");
                System.out.println(Thread.currentThread().getName() + "抢到锁了");
                for (Long userId : mainUserList) {
                    //每个核心用户，主页列表展示20个数据
                    Page<User> pageResult = userService.page(new Page<>(1, 20), null);
                    String redisKey = String.format("partner:user:recommend:%s", userId);
                    ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
                    List<User> records = pageResult.getRecords();
                    List<User> userList = records.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
                    //写缓存,30s过期
                    try {
                        opsForValue.set(redisKey,userList,30000, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        log.error("redis set key error",e);
            }

                }
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
