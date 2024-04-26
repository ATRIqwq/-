package com.example.mapper;

import com.example.module.entity.User;
import com.example.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import javax.annotation.Resource;

@SpringBootTest
class UserMapperTest {
    @Resource
    private UserService userService;


    @Test
    void testAddUser() {
        User user = new User();
        user.setUsername("Tom");
        user.setUserAccount("tom123");
        user.setUserPassword("123456");
        user.setAvatarUrl("https://acg.qingjuacg.top/wp-content/uploads/2023/02/29658824de201630.png");
        user.setPhone("132");
        user.setEmail("2586300785@qq.com");


        boolean save = userService.save(user);
        Assert.isTrue(save);
    }
}