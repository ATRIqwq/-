package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.comment.ErrorCode;
import com.example.constant.TeamStatusEnum;
import com.example.exception.BusinessException;
import com.example.mapper.TeamMapper;
import com.example.module.entity.Team ;
import com.example.module.entity.User;
import com.example.module.entity.UserTeam;
import com.example.module.request.*;
import com.example.module.vo.TeamUserVO;
import com.example.module.vo.UserVO;
import com.example.service.TeamService;
import com.example.service.UserService;
import com.example.service.UserTeamService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.time.LocalTime.now;

/**
 * <p>
 * 队伍 服务实现类
 * </p>
 *
 * @author author
 * @since 2024-04-25
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> implements TeamService {

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
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addTeam(TeamAddRequest teamAddRequest, HttpServletRequest request) {

        //1
        //请求参数是否为空？
        Team team = new Team();
        BeanUtils.copyProperties(teamAddRequest,team);
        //2
        //是否登录，未登录不允许创建
        User loginUser = userService.getCurrentUser(request);
        if (loginUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        //3
        //校验信息
        //a
        //队伍人数 > 1 且 <= 20
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if (maxNum < 1 || maxNum > 20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍人数不满足要求");
        }
        //b
        //队伍标题 <= 20
        String teamName = team.getName();
        if (StringUtils.isBlank(teamName) || teamName.length() > 20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍标题不满足要求");
        }

        //c
        //描述 <= 512
        String description = team.getDescription();
        if (description.length() >512){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍描述不满足要求");
        }
        //d
        //status 是否公开（int）不传默认为 0（公开）
        int status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValues(status);
        if (statusEnum == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍状态不满足要求");
        }
        //e
        //如果 status 是加密状态，一定要有密码，且密码 <= 32
        if (TeamStatusEnum.SECRET.equals(statusEnum)){
            if (team.getPassword().length() > 32 || StringUtils.isBlank(team.getPassword())){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码设置不正确");
            }
        }
        //f
        //超时时间 > 当前时间
        Date expireTime = team.getExpireTime();
        if (new Date().after(expireTime)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"超时时间 > 当前时间");
        }

        //g
        //校验用户最多创建 5 个队伍
        // todo 有bug，可能同时创建100个队伍
        Long userId = loginUser.getId();
        QueryWrapper<Team> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(Team::getUserId,userId);
        long hasTeam = count(wrapper);
        if (hasTeam >= 5 ){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户最多创建 5 个队伍");
        }

        //4
        //插入队伍信息到队伍表
        boolean saveTeamResult = this.save(team);
        if (!saveTeamResult){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"新增队伍失败");
        }
        Long teamId = team.getId();
        //5
        //插入用户 => 队伍关系到关系表
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        boolean saveUserTeamResult = userTeamService.save(userTeam);
        if (!saveUserTeamResult){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"创建队伍失败");
        }

        return teamId;
    }

    /**
     * 查询队伍
     * @param teamQuery
     * @param isAdmin
     * @return
     */
    @Override
    public List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin) {
        QueryWrapper<Team> wrapper = new QueryWrapper<>();
        //构造查询条件
        Long teamId = teamQuery.getId();
        if (teamId!=null && teamId > 0){
            wrapper.lambda().eq(Team::getId,teamId);
        }
        String teamName = teamQuery.getName();
        if (StringUtils.isNotBlank(teamName)){
            wrapper.lambda().like(Team::getName,teamName);
        }
        String description = teamQuery.getDescription();
        if (StringUtils.isNotBlank(description)){
            wrapper.lambda().like(Team::getDescription,description);
        }
        Integer maxNum = teamQuery.getMaxNum();
        if (maxNum != null && maxNum > 0){
            wrapper.lambda().eq(Team::getMaxNum,maxNum);
        }
        //普通用户只能查询公开的队伍
        Integer status = teamQuery.getStatus();
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValues(status);
        if (statusEnum == null){
            statusEnum = TeamStatusEnum.PUBLIC;
        }
        if (!isAdmin && !statusEnum.equals(TeamStatusEnum.PUBLIC)){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        wrapper.lambda().eq(Team::getStatus,statusEnum);

        //过期队伍不展示  empire > now() or empireTime == null
        wrapper.and(w -> w.gt("expireTime",new Date())).or().isNull("expireTime");

//        wrapper.lambda().gt(Team::getExpireTime,new Date()).or().isNull(Team::getExpireTime);


        String searchText = teamQuery.getSearchText();
        wrapper.and(w -> w.like("name",searchText)).or().like("description",searchText);

        List<Team> teamList = list(wrapper);
        if (teamList == null){
            return null;
        }


        List<TeamUserVO> teamUserVOList = new ArrayList<>();
        //关联查询用户表
        for (Team team : teamList) {
            Long userId = team.getUserId();
            if (userId == null){
                continue;
            }
            User user = userService.getById(userId);
            TeamUserVO teamUserVO = new TeamUserVO();

            User safetyUser = userService.getSafetyUser(user);
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user,userVO);
            BeanUtils.copyProperties(team,teamUserVO);
            teamUserVO.setCreateUser(userVO);
            teamUserVOList.add(teamUserVO);
        }

        return teamUserVOList;
    }


    /**
     * 更新队伍
     * @param team
     * @param loginUser
     * @return
     */
    public boolean updateTeam(TeamUpdateRequest team,User loginUser){
        if (team == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long teamId = team.getId();
        Team oldTeam = this.getTeamById(teamId);

        //只有队长和管理员可以修改队伍
        if (!oldTeam.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }

//        //如果用户传入的新值和老值一致，就不用 update 了
//        TeamUpdateRequest oldTeamData = new TeamUpdateRequest();
//        BeanUtils.copyProperties(oldTeam,oldTeamData);

        Integer status = team.getStatus();
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValues(status);
        if (teamStatusEnum == TeamStatusEnum.SECRET ){
            if (StringUtils.isBlank(team.getPassword())){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"加密房间必须设置密码");
            }
        }

        Team updateTeam = new Team();
        BeanUtils.copyProperties(team,updateTeam);
        return this.updateById(updateTeam);
    }


    /**
     * 用户加入队伍
     * @param joinTeamRequest
     * @param loginUser
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean joinTeam(JoinTeamRequest joinTeamRequest,User loginUser){
        if (joinTeamRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long teamId = joinTeamRequest.getTeamId();
        Team team = this.getTeamById(teamId);
        Date expireTime = team.getExpireTime();
        if (expireTime != null && expireTime.before(new Date()) ){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍已过期");
        }
        Integer status = team.getStatus();
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValues(status);
        if (teamStatusEnum.equals(TeamStatusEnum.PRIVATE)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"私密队伍不允许加入");
        }
        String password = joinTeamRequest.getPassword();
        if (teamStatusEnum.equals(TeamStatusEnum.SECRET)){
            if (StringUtils.isBlank(password) || !password.equals(team.getPassword())){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码错误");
            }
        }


        Long userId = loginUser.getId();
        QueryWrapper<UserTeam> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(UserTeam::getUserId,userId);
        long hasJoinNum = userTeamService.count(wrapper);
        if (hasJoinNum >= 5 ){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"最多运行加入5个队伍");
        }

        QueryWrapper<UserTeam> userTeamWrapper = new QueryWrapper<>();
        userTeamWrapper.lambda().eq(UserTeam::getTeamId,teamId)
                .eq(UserTeam::getUserId,userId);
        long teamUserHasJoinNum = userTeamService.count(userTeamWrapper);
        if (teamUserHasJoinNum > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"不允许重复加入已加入队伍");
        }

        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserTeam::getTeamId,teamId);
        long teamHasJoinNum = userTeamService.count(queryWrapper);
        if (teamHasJoinNum > team.getMaxNum()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍已满");
        }

        UserTeam userTeam = new UserTeam();
        userTeam.setJoinTime(new Date());
        userTeam.setTeamId(teamId);
        userTeam.setUserId(userId);
        return userTeamService.save(userTeam);
    }


    /**
     * 用户退出队伍
     * @param teamQuitRequest
     * @param currentUser
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean quitTeam(TeamQuitRequest teamQuitRequest, User currentUser) {
        //请求参数：队伍 id
        //1
        // 校验请求参数
        Long teamId = teamQuitRequest.getTeamId();
        Team team = getTeamById(teamId);
        //3
        // 校验我是否已加入队伍
        Long userId = currentUser.getId();
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserTeam::getUserId,userId).eq(UserTeam::getTeamId,teamId);
        long count = userTeamService.count(queryWrapper);
        if (count == 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户未加入队伍");
        }
        //4
        // 如果队伍
            //a
            // 只剩一人，队伍解散
            //b
            // 还有其他人
                //ⅰ
                // 如果是队长退出队伍，权限转移给第二早加入的用户 —— 先来后到
                //只用取 id 最小的 2 条数据
                // ⅱ.  非队长，自己退出队伍

        QueryWrapper<UserTeam> countTeamUserWrapper = new QueryWrapper<>();
        countTeamUserWrapper.lambda().eq(UserTeam::getTeamId,teamId);
        long teamHasJoinNum = userTeamService.count(countTeamUserWrapper);
        // 只剩一人，队伍解散
        if (teamHasJoinNum == 1){
            this.removeById(teamId);
        }
        else {
            // 如果是队长退出队伍，权限转移给第二早加入的用户 —— 先来后到
            if (team.getUserId().equals(userId)){
                countTeamUserWrapper.last("order by id asc limit 2");
                List<UserTeam> userTeamList = userTeamService.list(countTeamUserWrapper);
                if (CollectionUtils.isEmpty(userTeamList) || userTeamList.size() <= 1){
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                }
                UserTeam nextUserTeam = userTeamList.get(1);
                Long nextTeamLeaderId = nextUserTeam.getUserId();
                //权限转移给第二早加入的用户
                Team nextTeam = new Team();
                nextTeam.setId(teamId);
                nextTeam.setUserId(nextTeamLeaderId);
                boolean result = this.updateById(nextTeam);
                if (!result) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                }
            }
        }
        return userTeamService.remove(queryWrapper);
    }

    /**
     * 解散队伍
     * @param teamQuitRequest
     * @param currentUser
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTeam(TeamQuitRequest teamQuitRequest, User currentUser) {
        //请求参数：队伍 id
        //业务流程：
        //1
        //校验请求参数
        //2
        //校验队伍是否存在
        Long teamId = teamQuitRequest.getTeamId();
        Team team = this.getTeamById(teamId);
        //3
        //校验你是不是队伍的队长
        if (!team.getUserId().equals(currentUser.getId())){
            throw new BusinessException(ErrorCode.NO_AUTH,"无访问权限");
        }
        //4
        //移除所有加入队伍的关联信息
        QueryWrapper<UserTeam> TeamUserWrapper = new QueryWrapper<>();
        TeamUserWrapper.lambda().eq(UserTeam::getTeamId,teamId);
        boolean result = userTeamService.remove(TeamUserWrapper);
        if (!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"删除队伍关联信息失败");
        }
        //5
        //删除队伍
        return this.removeById(teamId);
    }

    /**
     * 根据ID获取队伍信息
     * @param teamId
     * @return
     */
    private Team getTeamById(Long teamId) {
        if (teamId == null || teamId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = this.getById(teamId);
        if (team == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍不存在");
        }
        return team;
    }


}
