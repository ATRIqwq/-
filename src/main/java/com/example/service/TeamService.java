package com.example.service;

import com.example.module.entity.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.module.entity.User;
import com.example.module.request.*;
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
    Long addTeam(TeamAddRequest teamAddRequest, User loginUser);


    /**
     * 查询队伍
     * @param teamQuery
     * @param isAdmin
     * @return
     */
    List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin);

    /**
     * 修改队伍信息
     * @param team
     * @param loginUser
     * @return
     */
    public boolean updateTeam(TeamUpdateRequest team, User loginUser);

    /**
     * 用户加入队伍
     * @param joinTeamRequest
     * @param loginUser
     * @return
     */
     boolean joinTeam(JoinTeamRequest joinTeamRequest, User loginUser);


    /**
     * 用户退出队伍
     * @param teamQuitRequest
     * @param currentUser
     * @return
     */
    boolean quitTeam(TeamQuitRequest teamQuitRequest, User currentUser);

    /**
     * 解散队伍
     * @param teamQuitRequest
     * @param currentUser
     * @return
     */
    boolean deleteTeam(TeamQuitRequest teamQuitRequest, User currentUser);
}
