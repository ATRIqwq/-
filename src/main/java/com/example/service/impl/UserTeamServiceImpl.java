package com.example.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.module.entity.UserTeam;
import com.example.service.UserTeamService;
import com.example.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;


/**
* @author 86136
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2024-04-26 15:46:23
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




