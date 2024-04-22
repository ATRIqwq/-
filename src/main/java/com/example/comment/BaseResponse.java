package com.example.comment;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.Data;

import java.io.Serializable;


/**
 * 通用返回类
 * @author kano
 * @param <T>
 */
@Data
public class BaseResponse<T> implements Serializable {

    private int code;

    private T data;

    private String massage;

    private String description;

    public BaseResponse(int code, T data, String massage, String description) {
        this.code = code;
        this.data = data;
        this.massage = massage;
        this.description = description;
    }


    public BaseResponse(int code, T data, String massage) {
        this.code = code;
        this.data = data;
        this.massage = massage;
        this.description = "";
    }
    public BaseResponse(int code, T data){
        this(code,data,"","");
    }

    public BaseResponse(ErrorCode errorCode){
        this(errorCode.getCode(),null,errorCode.getMessage(),errorCode.getDescription());
    }

    public BaseResponse(ErrorCode errorCode,String massage,String description){
        this(errorCode.getCode(),null,massage,description);
    }

    public BaseResponse(ErrorCode errorCode,String description){
        this(errorCode.getCode(),null,errorCode.getMessage(),description);
    }





}
