package com.example;

import com.example.module.entity.User;
import com.example.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

@SpringBootTest
class mySoulBackApplicationTests {

    @Autowired
    private UserService userService;

    @Test
    void insertUsers(){
        ArrayList<User> userList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            User user = new User();
            user.setUsername("假数据");
            user.setUserAccount("fakeaccount");
            user.setAvatarUrl("http://5b0988e595225.cdn.sohucs.com/images/20171115/d36fc25ebb4b4c3ab0405cb977a5cb0f.jpeg");
            user.setGender(0);
            user.setUserPassword("231313123");
            user.setPhone("1231312");
            user.setEmail("12331234@qq.com");
            user.setUserStatus(0);
            user.setUserRole(0);
            user.setPlanetCode("213123");
            user.setTags("[]");
            userList.add(user);
        }
        userService.saveBatch(userList);
    }

}
