package com.example.module.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class TeamUpdateRequest implements Serializable {
    private static final long serialVersionUID = -7340748904430225853L;

    /**
     * id
     */
    private Long id;

    @ApiModelProperty(value = "队伍名称")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "描述")
    @TableField("description")
    private String description;



    @ApiModelProperty(value = "过期时间")
    @TableField("expireTime")
    private Date expireTime;

    @ApiModelProperty(value = "0 - 公开，1 - 私有，2 - 加密")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "密码")
    @TableField("password")
    private String password;

}

