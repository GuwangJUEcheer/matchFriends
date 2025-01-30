package hokumei.sys.matchfriends.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import hokumei.sys.matchfriends.common.BaseResponse;
import hokumei.sys.matchfriends.common.ErrorCode;
import hokumei.sys.matchfriends.common.ResultUtils;
import hokumei.sys.matchfriends.domain.UserLoginRequest;
import hokumei.sys.matchfriends.domain.UserRegisterRequest;
import hokumei.sys.matchfriends.exception.BusinessException;
import hokumei.sys.matchfriends.mapper.UserMapper;
import hokumei.sys.matchfriends.model.User;
import hokumei.sys.matchfriends.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@ResponseBody
@Tag(name = "body参数")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @PostMapping("/register")
    public BaseResponse<Long> register(@RequestBody UserRegisterRequest userRegisterRequest) {

        if(userRegisterRequest == null) {
            return new BaseResponse<Long>(0, (long) -1,"NG");
        }

        String userAccount = userRegisterRequest.getUsername();
        String userPassword = userRegisterRequest.getPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String planetCode = userRegisterRequest.getPlanetCode();
        Long result = userService.userRegister(userAccount,userPassword,checkPassword,planetCode);
        return ResultUtils.success(result);
    }

    @PostMapping("/getAllUsers")
    public List<User> getAllUsers() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        return userMapper.selectList(queryWrapper);
    }


    @PostMapping("/login")
    public BaseResponse<User> login(@RequestBody UserLoginRequest loginRequest, HttpServletRequest request) {

        if(loginRequest == null) {
            return null;
        }
        String userAccount = loginRequest.getUsername();
        String userPassword = loginRequest.getPassword();
        if(StringUtils.isAnyBlank(userAccount, userPassword)){
            return null;
        }
        User user = userService.login(userAccount,userPassword,request);
        return ResultUtils.success(user);
    }

    /**
     * 根据用户名模糊查询所有的user
     * @param userAccount 用户名
     * @return 用户列表
     */

    @GetMapping("/search")
    public List<User> search(@RequestParam String userAccount,HttpServletRequest request) {
        //仅有管理员可以查询
        Object userObject = request.getSession().getAttribute(UserService.USER_LOGIN_STATE);
        User user = (User) userObject;
        if (user == null || user.getRoleid() != 1) {
            return new ArrayList<User>();
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("username",userAccount);
        return userService.list(queryWrapper);
    }

    /**
     * 根据标签查询所有的user
     * @param tagNameList 标签列表
     * @return 用户列表
     */
    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchByTags(@RequestParam List<String> tagNameList) {
        List<User> users = userService.searchUserByTags(tagNameList);
        return ResultUtils.success(users);
    }

    /**
     * 根据标签进行用户的推荐匹配
     * @param pageSize 分页大小
     * @param pageNumber 页码数
     * @return 每页用户的数据
     */
    @GetMapping("/recommend")
    public BaseResponse<Page<User>> recommendUsers(long pageSize,long pageNumber,HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        //第一步有缓存 从缓存中去查
        String userRedisKey = String.format("match friends:user:recommend:%s",loginUser.getId());
        Page<User> userListPerPage = (Page<User>)redisTemplate.opsForValue().get(userRedisKey);
        if(userListPerPage != null) {
            return ResultUtils.success(userListPerPage);
        }
        //进行分页查询
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        userListPerPage = userService.page(new Page<>(pageNumber,pageSize),wrapper);
        try{
            redisTemplate.opsForValue().set(userRedisKey,userListPerPage,3000, TimeUnit.SECONDS);
        }catch(Exception e){
            log.error("redis error");
        }
        return ResultUtils.success(userListPerPage);
    }

    /**
     * 根据id 删除用户
     * @param id 用户id
     * @return 返回逻辑删除结果
     */
    @PostMapping("/deleteUser")
    public boolean delete(@RequestBody Long id) {

       if(id == null) {
           return false;
       }
       return userService.removeById(id);
    }

    @PostMapping("/logOut")
    public void logOut(HttpServletRequest request) {
       userService.logOut(request);
    }

    /**
     * 获取当前用户
     * @param request 请求
     * @return 返回当前用户详细信息
     */
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        User currentUser = userService.getLoginUser(request);
        return ResultUtils.success(currentUser);
    }

    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody User user,HttpServletRequest request) {
        if(user == null) {
           throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        if(loginUser == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Integer result = userService.updateUser(user,loginUser,request);
        return ResultUtils.success(result);
    }
}
