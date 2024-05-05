package com.example.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.comment.BaseResponse;
import com.example.comment.ErrorCode;
import com.example.comment.ResultUtils;
import com.example.exception.BusinessException;
import com.example.module.entity.Team;
import com.example.module.entity.User;
import com.example.module.entity.UserTeam;
import com.example.module.request.*;
import com.example.module.vo.TeamUserVO;
import com.example.service.TeamService;
import com.example.service.UserService;
import com.example.service.UserTeamService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 队伍 前端控制器
 * </p>
 *
 * @author author
 * @since 2024-04-25
 */
@RestController
@RequestMapping("/team")
@Slf4j
@Api(tags = "队伍相关接口")
@CrossOrigin(origins = "http://localhost:5173",allowCredentials = "true")
public class TeamController {

    @Resource
    private TeamService teamService;
    @Resource
    private UserService userService;

    @Resource
    private UserTeamService userTeamService;

    /**
     * 添加队伍
     * @param teamAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @ApiOperation("添加队伍")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request){
        if (teamAddRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User currentUser = userService.getCurrentUser(request);
        Long teamId =  teamService.addTeam(teamAddRequest,currentUser);
        return ResultUtils.success(teamId);
    }


    /**
     * 查询队伍
     * @param teamQuery
     * @param request
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("查询队伍")
    public BaseResponse<List<TeamUserVO>> listTeams(TeamQuery teamQuery,HttpServletRequest request){
        if (teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean isAdmin = userService.isAdmin(request);
        // 1、查询队伍列表
        List<TeamUserVO> teamList = teamService.listTeams(teamQuery,isAdmin);
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        try {
            User currentUser = userService.getCurrentUser(request);
            List<Long> teamIdList = teamList.stream().map(TeamUserVO::getId).collect(Collectors.toList());
            // 2、查询已加入队伍
            userTeamQueryWrapper.lambda().eq(UserTeam::getUserId,currentUser.getId())
                    .in(UserTeam::getTeamId,teamIdList);
            List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);

            // 已加入的队伍 id 集合
            Set<Long> hasJoinTeamIdSet = userTeamList.stream().map(UserTeam::getTeamId).collect(Collectors.toSet());
            teamList.forEach(team ->{
                boolean hasJoin = hasJoinTeamIdSet.contains(team.getId());
                team.setHasJoin(hasJoin);
            });


        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return ResultUtils.success(teamList);
    }

    /**
     * 根据ID查询队伍
     * @param id
     * @return
     */
    @GetMapping("/get")
    @ApiOperation("根据ID查询队伍")
    public BaseResponse<Team> getTeamById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = teamService.getById(id);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR,"队伍不存在");
        }
        return ResultUtils.success(team);
    }


    /**
     * 修改队伍信息
     * @param team
     * @param request
     * @return
     */
    @PostMapping("/update")
    @ApiOperation("修改队伍")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest team, HttpServletRequest request){
        if (team == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User currentUser = userService.getCurrentUser(request);
        if (currentUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        boolean result = teamService.updateTeam(team, currentUser);
        return ResultUtils.success(result);
    }

    /**
     * 用户加入队伍
     * @param joinTeamRequest
     * @param request
     * @return
     */
    @PostMapping("/join")
    @ApiOperation("用户加入队伍")
    public BaseResponse<Boolean> joinTeam(@RequestBody JoinTeamRequest joinTeamRequest,HttpServletRequest request){
        if (joinTeamRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User currentUser = userService.getCurrentUser(request);
        if (currentUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        boolean result = teamService.joinTeam(joinTeamRequest, currentUser);
        return ResultUtils.success(result);
    }


    /**
     * 用户退出队伍
     * @param teamQuitRequest
     * @param request
     * @return
     */
    @PostMapping("/quit")
    @ApiOperation("用户退出队伍")
    public BaseResponse<Boolean> quitTeam(@RequestBody TeamQuitRequest teamQuitRequest,HttpServletRequest request){
        if (teamQuitRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User currentUser = userService.getCurrentUser(request);
        boolean result =  teamService.quitTeam(teamQuitRequest,currentUser);
        return ResultUtils.success(result);
    }

    /**
     * 解散队伍
     * @param teamQuitRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @ApiOperation("解散队伍")
    public BaseResponse<Boolean> deleteTeam(@RequestBody TeamQuitRequest teamQuitRequest,HttpServletRequest request){
        if (teamQuitRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User currentUser = userService.getCurrentUser(request);
        boolean result =  teamService.deleteTeam(teamQuitRequest,currentUser);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败");
        }
        return ResultUtils.success(result);
    }

    /**
     * 查询我创建的队伍
     * @param teamQuery
     * @param request
     * @return
     */
    @GetMapping("/list/my/create")
    @ApiOperation("查询自己创建的队伍")
    public BaseResponse<List<TeamUserVO>> listMyCreateTeams(TeamQuery teamQuery,HttpServletRequest request){
        if (teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User currentUser = userService.getCurrentUser(request);
        teamQuery.setUserId(currentUser.getId());
        List<TeamUserVO> teamList = teamService.listTeams(teamQuery,true);
        return ResultUtils.success(teamList);
    }

    /**
     * 查询我加入的队伍
     * @param teamQuery
     * @param request
     * @return
     */
    @GetMapping("/list/my/join")
    @ApiOperation("查询已加入的队伍")
    public BaseResponse<List<TeamUserVO>> listMyJoinTeams(TeamQuery teamQuery,HttpServletRequest request){
        if (teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User currentUser = userService.getCurrentUser(request);
        Long userId = currentUser.getId();
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserTeam::getUserId,userId);
        List<UserTeam> userTeamList = userTeamService.list(queryWrapper);

        Map<Long, List<UserTeam>> listMap = userTeamList.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
        List<Long> idList = new ArrayList<>(listMap.keySet());
        teamQuery.setIdList(idList);
        List<TeamUserVO> teamList = teamService.listTeams(teamQuery,true);
        teamList.forEach(team ->{
            team.setHasJoin(true);
        });
        return ResultUtils.success(teamList);
    }

}
