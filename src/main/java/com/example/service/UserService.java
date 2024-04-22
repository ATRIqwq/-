package com.example.service;

import com.example.module.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.http.HttpRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author 86136
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2024-03-14 21:12:31
*/
public interface UserService extends IService<User> {

    /**
     *
     * @param userAccount   用户账户
     * @param password  用户密码
     * @param checkPassword 校验密码
     * @return 用户新ID
     */
    long userRegister(String userAccount,String password,String checkPassword,String planetCode);


    /**
     *
     * @param userAccount   用户账户
     * @param password  用户密码
     * @return  脱敏后的用户信息
     */
    User userLogin(String userAccount, String password, HttpServletRequest request);

    User getSafetyUser(User originUser);

    int userLogout(HttpServletRequest request);

    /**
     *   根据标签搜索用户。(内存过滤版)
     * @param tagNameList  用户要搜索的标签
     * @return
     */
    List<User> searchUsersByTags(List<String> tagNameList);
}
