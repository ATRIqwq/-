package com.example.service;

import com.example.module.entity.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.module.request.TeamAddRequest;
import com.example.module.request.TeamQuery;
import com.example.module.vo.TeamUserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 队伍 服务类
 * </p>
 *
 * @author author
 * @since 2024-04-25
 */
public interface TeamService extends IService<Team> {

    /**
     * 添加队伍
     * @param teamAddRequest
     * @param request
     * @return
     */
    Long addTeam(TeamAddRequest teamAddRequest, HttpServletRequest request);


    /**
     * 查询队伍
     * @param teamQuery
     * @param isAdmin
     * @return
     */
    List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin);
}
