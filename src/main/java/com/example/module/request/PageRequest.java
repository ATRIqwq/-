package com.example.module.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageRequest implements Serializable {


    private static final long serialVersionUID = 5921066328732591666L;
    /**
     * 页面大小
     */
    protected int pageSize = 8;

    /**
     * 当前是第几页
     */
    protected int pageNum = 1;
}