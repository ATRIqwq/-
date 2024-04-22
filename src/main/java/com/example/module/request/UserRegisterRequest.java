package com.example.module.request;

import lombok.Data;

import java.io.Serializable;


/**
 *
 * 用户请求实体类
 *
 * @author :kano
 */
@Data
public class UserRegisterRequest  {

    private String userAccount;

    private String password;

    private String checkPassword;

    private String planetCode;

}
