package com.example.module.request;


import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class JoinTeamRequest implements Serializable {
    private static final long serialVersionUID = -2400112719316292591L;


    @ApiModelProperty(value = "队伍ID")
    @TableField("teamId")
    private Long teamId;

    @ApiModelProperty(value = "密码")
    @TableField("password")
    private String password;
}
