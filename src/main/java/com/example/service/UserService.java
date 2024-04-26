package com.example.service;

import com.example.module.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

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

    /**
     * 用户脱敏
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);

    /**
     * 用户退出登录
     * @param request
     * @return
     */
    int userLogout(HttpServletRequest request);

    /**
     *   根据标签搜索用户。(内存过滤版)
     * @param tagNameList  用户要搜索的标签
     * @return
     */
    List<User> searchUsersByTags(List<String> tagNameList);

    /**
     * 获取当前登录用户
     * @param request
     * @return
     */
    User getCurrentUser(HttpServletRequest request);

    /**
     * 判断是否为管理员
     * @param request
     * @return
     */
    public boolean isAdmin(HttpServletRequest request);


    /**
     * 判断是否为管理员
     * @param loginUser
     * @return
     */
    public boolean isAdmin(User loginUser);


    /**
     * 更新用户
     * @param user
     * @param loginUser
     * @return
     */
    int updateUser(User user, User loginUser);


    /**
     * 主页分页展示用户
     * @param  pageSize, pageNum
     * @return userList
     */
    List<User> pageQuery(long pageSize, long pageNum,HttpServletRequest request);
}
