package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.comment.BaseResponse;
import com.example.comment.ErrorCode;
import com.example.comment.ResultUtils;
import com.example.exception.BusinessException;
import com.example.module.entity.User;
import com.example.module.request.UserLoginRequest;
import com.example.module.request.UserRegisterRequest;
import com.example.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.constant.UserConstant.ADMIN_ROLE;
import static com.example.constant.UserConstant.USER_LOGIN_STATE;


/**
 * 用户接口
 *
 * @author :kano
 */

@RestController
@RequestMapping("/user")
@Slf4j
@Api(tags = "用户相关接口")
@CrossOrigin(origins = "http://localhost:5173",allowCredentials = "true")

public class UserController {

    @Resource
    private UserService userService;


    /**
     * 用户注册功能
     * @param registerRequest
     * @return
     */
    @PostMapping("/register")
    @ApiOperation("用户注册")
    BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest registerRequest){
        if (registerRequest == null ){
            log.info("registerRequest is null");
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String userAccount = registerRequest.getUserAccount();
        String password = registerRequest.getPassword();
        String checkPassword = registerRequest.getCheckPassword();
        String planetCode = registerRequest.getPlanetCode();

        if (StringUtils.isAnyBlank(userAccount,password,checkPassword,planetCode)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long register = userService.userRegister(userAccount, password, checkPassword, planetCode);
        return ResultUtils.success(register);
    }

    /**
     * 用户登录功能
     * @param loginRequest
     * @param request
     * @return
     */
    @ApiOperation("用户登录")
    @PostMapping("/login")
    BaseResponse<User> userLogin(@RequestBody UserLoginRequest loginRequest, HttpServletRequest request){
        if (loginRequest == null ){
            log.info("loginRequest is null");
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String userAccount = loginRequest.getUserAccount();
        String password = loginRequest.getUserPassword();

        if (StringUtils.isAnyBlank(userAccount,password)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.userLogin(userAccount, password, request);
        return ResultUtils.success(user);
    }

    /**
     * 返回脱敏的用户数据
     * @param username
     * @param request
     * @return
     */
    @GetMapping("/search")
    @ApiOperation("用户脱敏")
    BaseResponse<List<User>> userGetByUsername(String username,HttpServletRequest request){

        if (!isAdmin(request)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("username",username);
        List<User> list = userService.list(queryWrapper);

        /**
         * 返回脱敏后的用户数据
         */
        List<User> userList = list.stream().map(user -> {
            return userService.getSafetyUser(user);
        }).collect(Collectors.toList());

        return ResultUtils.success(userList);
    }

    /**
     * 删除用户
     * @param id
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @ApiOperation("删除用户")
    BaseResponse<Boolean> deleteUserById(Long id,HttpServletRequest request){
        if (!isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }

        if (id < 0){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }

        boolean b = userService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 退出登录
     * @param request
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation("退出登录")
     BaseResponse<Integer> userLogout(HttpServletRequest request){
        if (request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = userService.userLogout(request);
        return ResultUtils.success(result);

    }

    /**
     * 获取用户当前信息
     * @return 脱敏后的用户
     */
    @GetMapping("/current")
    @ApiOperation("获取用户当前信息")
     BaseResponse<User> getCurrentUser(HttpServletRequest request){
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;

        if (currentUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }

        // TODO 校验用户是否合法
        long id = currentUser.getId();
        User user = userService.getById(id);
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);

    }

    /**
     * 判断是否为管理员
     *
     * @return 是否为管理员是返回1
     */
    private boolean isAdmin(HttpServletRequest request){
        //1.判断是否是管理员
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        if ( user == null  || user.getUserRole() == ADMIN_ROLE){
            return false;
        }
        return true;
    }

    /**
     * 按标签搜索用户
     * @param tagNameList
     * @return
     */
    @ApiOperation("按标签搜索用户")
    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchUsersByTags(@RequestParam(required = false) List<String> tagNameList){
        if (CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        List<User> userList = userService.searchUsersByTags(tagNameList);
        return ResultUtils.success(userList);
    }

    /**
     * 更新用户信息
     * @param user
     * @param request
     * @return
     */
    @PostMapping("/update")
    @ApiOperation("更新用户信息")
    public BaseResponse<Integer> updateUser(@RequestBody User user,HttpServletRequest request){
        if (user == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        User loginUser = userService.getCurrentUser(request);

        int result = userService.updateUser(user,loginUser);
        return ResultUtils.success(result);

    }

    /**
     * 主页展示用户
     * @param
     * @return userList
     */
    @ApiOperation("主页展示用户")
    @GetMapping("/recommend")
    public BaseResponse<List<User>> recommendUsers(long pageSize,long pageNum, HttpServletRequest request){
        User loginUser = (User)request.getSession().getAttribute(USER_LOGIN_STATE);
        log.info("分页查询：{}，{},{}",pageSize,pageNum,loginUser);
        List<User> pageResult = userService.pageQuery(pageSize, pageNum, request);
        //分页查询
        return ResultUtils.success(pageResult);
    }

    /**
     * 根据标签相似度匹配用户
     * @param num
     * @param request
     * @return
     */
    @GetMapping("/match")
    @ApiOperation("根据标签匹配相似用户")
    public BaseResponse<List<User>> matchUsers(long num,HttpServletRequest request){
        if (num <=0 || num >20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        User loginUser = userService.getCurrentUser(request);
        List<User> userList = userService.matchUsers(num,loginUser);
        return ResultUtils.success(userList);


    }

}
