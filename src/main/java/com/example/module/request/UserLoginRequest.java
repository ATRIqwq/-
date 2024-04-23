package com.example.module.request;


import lombok.Data;

/**
 *
 * 用户登录实体类
 *
 * @author :kano
 */

@Data
public class UserLoginRequest {
    private String userAccount;

    private String userPassword;
}
