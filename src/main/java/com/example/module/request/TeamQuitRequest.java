package com.example.module.request;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class TeamQuitRequest implements Serializable {
    private static final long serialVersionUID = 7182587215532481643L;
    @ApiModelProperty(value = "队伍ID")
    @TableField("teamId")
    private Long teamId;
}
