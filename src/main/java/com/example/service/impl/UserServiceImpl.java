package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.comment.ErrorCode;
import com.example.exception.BusinessException;
import com.example.module.domain.User;
import com.example.service.UserService;
import com.example.mapper.UserMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.example.comment.ErrorCode.NULL_ERROR;
import static com.example.comment.ErrorCode.PARAMS_ERROR;
import static com.example.constant.UserConstant.ADMIN_ROLE;
import static com.example.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author 86136
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2024-03-14 21:12:31
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;

    //盐值  用来加密用户密码
    private static final String SALT = "kano";


    /**
     * 用户注册
     * @param userAccount   用户账户
     * @param password  用户密码
     * @param checkPassword 校验密码
     * @param planetCode
     * @return
     */
    @Override
    public long userRegister(String userAccount, String password, String checkPassword, String planetCode) {
        //1.校验
        if (StringUtils.isAnyBlank(userAccount, password, checkPassword,planetCode)) {
            throw new BusinessException(PARAMS_ERROR,"参数为空");
        }

        if (userAccount.length() < 4) {
            throw new BusinessException(PARAMS_ERROR,"账户长度小于四位");
        }

        if (password.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(PARAMS_ERROR,"密码小于8位");
        }

        if (planetCode.length() > 5){
            throw new BusinessException(PARAMS_ERROR,"星球编号大于5位");
        }

        //账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(PARAMS_ERROR,"账户包含特殊字符");
        }

        //校验密码是否相同
        if (!password.equals(checkPassword)) {
            throw new BusinessException(PARAMS_ERROR,"密码输入不一致");
        }

        //校验账户是否唯一
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        if (this.count(queryWrapper) > 0) {
            throw new BusinessException(PARAMS_ERROR,"账户不唯一");
        }

        //校验星球编号是否唯一
        QueryWrapper<User> codeWrapper = new QueryWrapper<>();
        codeWrapper.eq("planetCode", planetCode);
        if (this.count(codeWrapper) > 0) {
            throw new BusinessException(PARAMS_ERROR,"星球编号不唯一");
        }

        //加密数据

        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());


        //插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setPlanetCode(planetCode);

        boolean save = this.save(user);

        if (!save) {
            throw new BusinessException(PARAMS_ERROR,"用户保存失败");
        }
        return user.getId();

    }

    @Override
    public User userLogin(String userAccount, String password, HttpServletRequest request) {
        //1.校验
        if (StringUtils.isAnyBlank(userAccount, password)) {
            throw new BusinessException(PARAMS_ERROR,"参数为空");
        }

        if (userAccount.length() < 4) {
            throw new BusinessException(PARAMS_ERROR,"账户长度小于四位");
        }

        if (password.length() < 8) {
            throw new BusinessException(PARAMS_ERROR,"密码小于8位");
        }

        //账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(PARAMS_ERROR,"账户包含特殊字符");
        }

        //加密数据
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());

        //校验密码是否正确，或者用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getUserAccount,userAccount);
        queryWrapper.lambda().eq(User::getUserPassword,encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null){
            log.info("userAccount not match password or user is not exist");
            throw new BusinessException(NULL_ERROR,"用户不存在");
        }

        User safetyUser = getSafetyUser(user);
        //4.记录用户的登录状态
        request.getSession().setAttribute(USER_LOGIN_STATE,safetyUser);


        return safetyUser;
    }


    /**
     *
     * @param request
     * 用户注销
     * @return 注销成功返回1
     */
    public int userLogout(HttpServletRequest request){
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }



    @Override
    public User getSafetyUser(User originUser){
        //3.用户信息脱敏
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setPlanetCode(originUser.getPlanetCode());
        safetyUser.setTags(originUser.getTags());
        safetyUser.setProfile(originUser.getProfile());
        return safetyUser;
    }

    /**
     *   根据标签搜索用户，数据库查询sql
     * @param tagNameList  用户要搜索的标签
     * @return
     */
    @Deprecated
    private List<User> searchUsersByTagsBySql(List<String> tagNameList){
        if (CollectionUtils.isEmpty(tagNameList)){
            throw  new BusinessException(PARAMS_ERROR);
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        for (String tag : tagNameList) {
            queryWrapper = queryWrapper.like("tags", tag);
        }
        List<User> userList = userMapper.selectList(queryWrapper);
        return userList.stream().map(this::getSafetyUser).collect(Collectors.toList());
    }


    /**
     * 根据标签搜索用户 - 内存查询
     * @param tagNameList 用户拥有的标签
     * @return
     */
    @Override
    public List<User> searchUsersByTags(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> userList = userMapper.selectList(queryWrapper);
        Gson gson = new Gson();

        //2.在内存中判断是否包含要求的标签
        return userList.stream().filter(user -> {
            String tagsStr = user.getTags();
            log.info("标签",tagsStr);
            if (StringUtils.isBlank(tagsStr)) {
                return false;
            }

            Set<String> tempTagNameSet = gson.fromJson(tagsStr, new TypeToken<Set<String>>() {
            }.getType());

            // Java8 Optional.ofNullable判断为空
            tempTagNameSet = Optional.ofNullable(tempTagNameSet).orElse(new HashSet<>());
            for (String tagName : tagNameList) {
                if (!tempTagNameSet.contains(tagName)) {
                    return false;
                }
            }
            return true;
        }).map(this::getSafetyUser).collect(Collectors.toList());
    }

    /**
     * 获取当前登录用户
     * @param request
     * @return
     */
    @Override
    public User getCurrentUser(HttpServletRequest request) {
        if (request == null){
            throw new BusinessException(PARAMS_ERROR);
        }
        User loginUser  =(User) request.getSession().getAttribute(USER_LOGIN_STATE);

        if (loginUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        return loginUser;
    }

    /**
     * 判断是否为管理员
     * @param request
     * @return
     */
    public boolean isAdmin(HttpServletRequest request){
       User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);

       if (user == null || user.getUserRole()!= ADMIN_ROLE){
           return false;
       }
       return true;
    }

    /**
     * 判断是否为管理员
     * @param loginUser
     * @return
     */
    public boolean isAdmin(User loginUser){
        return loginUser.getUserRole() == ADMIN_ROLE;
    }

    /**
     * 更新用户信息
     * @param user
     * @param loginUser
     * @return
     */
    @Override
    public int updateUser(User user, User loginUser) {
        //判断修改用户是否存在
        Long userId = user.getId();
        if (userId < 0){
            throw new BusinessException(PARAMS_ERROR);
        }
        //权限校验
        if (!isAdmin(loginUser) && !userId.equals(loginUser.getId())){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }

        User oldUser = this.getById(user.getId());
        if (oldUser == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        // 3. 触发更新
        return userMapper.updateById(user);
    }


}




