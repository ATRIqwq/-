package com.example.controller;


import com.example.comment.BaseResponse;
import com.example.comment.ErrorCode;
import com.example.comment.ResultUtils;
import com.example.exception.BusinessException;
import com.example.module.entity.User;
import com.example.module.request.TeamAddRequest;
import com.example.module.request.TeamQuery;
import com.example.module.vo.TeamUserVO;
import com.example.service.TeamService;
import com.example.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

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

        Long teamId =  teamService.addTeam(teamAddRequest,request);
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

        User currentUser = userService.getCurrentUser(request);
        boolean isAdmin = userService.isAdmin(currentUser);
        List<TeamUserVO> teamList = teamService.listTeams(teamQuery,isAdmin);

        return ResultUtils.success(teamList);
    }

}
