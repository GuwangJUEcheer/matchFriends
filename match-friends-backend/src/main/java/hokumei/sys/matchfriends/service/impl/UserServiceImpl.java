package hokumei.sys.matchfriends.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hokumei.sys.matchfriends.common.ErrorCode;
import hokumei.sys.matchfriends.constant.UserConstant;
import hokumei.sys.matchfriends.exception.BusinessException;
import hokumei.sys.matchfriends.mapper.UserMapper;
import hokumei.sys.matchfriends.model.User;
import hokumei.sys.matchfriends.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
* @author 17685
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2025-01-11 21:11:08
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{
    /**
     * 盐值
     */
    private static final String salt = "test01";


    private UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public boolean save(User entity) {
        return super.save(entity);
    }

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword,String planetCode) {
        // 1. 校验账号、密码是否为空
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword,planetCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"表单必须项目为空"); // 空值
        }
        // 账号长度不符合
        // 2. 校验账号长度
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号过短");
        }

        // 3. 校验密码长度
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码长度过短");// 密码长度不符合
        }

        if(planetCode.length()>5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"星球已满");
        }

        // 4. 校验账号是否包含特殊字符
        String validPattern = "^[a-zA-Z0-9_]+$"; // 仅允许字母、数字、下划线
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (!matcher.matches()) {
            return -1; // 包含特殊字符
        }

        // 5. 校验密码是否一致
        if (!userPassword.equals(checkPassword)) {
            return -1; // 密码不一致
        }

        // 6. 检查账号是否重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", userAccount);
        long count = this.count(queryWrapper);
        if (count > 0) {
            return -1; // 账号已存在
        }

        // 6. 检查账号是否重复
        QueryWrapper<User> queryWrapper2 = new QueryWrapper<>();
        queryWrapper.eq("planetCode", planetCode);
        if (this.count(queryWrapper) > 0) {
            return -1; // 账号已存在
        }

        // 7. 对密码进行加密后保存到数据库 盐值 salt
        final String salt = "test01";
        String encryptedPassword = DigestUtils.md5DigestAsHex((salt + userPassword).getBytes());
        User user = new User();
        user.setUsername(userAccount);
        user.setPassword(encryptedPassword);
        boolean result = this.save(user);
        if(!result){return -1;}
        return user.getId();
    }


    @Override
    public User login(String userAccount, String userPassword, HttpServletRequest request) {

        // 1. 校验账号、密码是否为空
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null; // 空值
        }

        // 2. 校验账号长度
        if (userAccount.length() < 4) {
            return null; // 账号长度不符合
        }

        // 3. 校验密码长度
        if (userPassword.length() < 8) {
            return null; // 密码长度不符合
        }

        // 4. 校验账号是否包含特殊字符
        String validPattern = "^[a-zA-Z0-9_]+$"; // 仅允许字母、数字、下划线
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (!matcher.matches()) {
            return null; // 包含特殊字符
        }

        // 5. 对密码进行加密后保存到数据库 进行查询
        String encryptedPassword = DigestUtils.md5DigestAsHex((salt + userPassword).getBytes());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", userAccount);
        queryWrapper.eq("password", encryptedPassword);
        User user = userMapper.selectOne(queryWrapper);
        if(user == null){
            return null;
        }
        User safetyUser = this.getSafetyUser(user);
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
        System.out.println(request.getSession().getId());
        return user;
    }

    @Override
    public void logOut(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
    }

    @Override
    public List<User> searchUserByTags(List<String> tagNameList) {
        // 检查输入参数是否为空
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 查询所有用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> userList = userMapper.selectList(queryWrapper);
        ObjectMapper objectMapper = new ObjectMapper(); // 使用 Jackson 处理 JSON

        // 过滤用户
        return userList.stream()
                .filter(user -> {
                    String tagsStr = user.getTags();
                    Set<String> tempTagNameSet;
                    try {
                        // 将 JSON 字符串转换为 Set<String>
                        tempTagNameSet = objectMapper.readValue(tagsStr, new TypeReference<Set<String>>() {});
                    } catch (Exception e) {
                        // 如果解析失败，则认为该用户不匹配
                        return false;
                    }

                    // 确保标签集不为 null
                    if (tempTagNameSet == null) {
                        return false;
                    }

                    // 检查用户的标签是否包含所有传入的标签
                    for (String tagName : tagNameList) {
                        if (!tempTagNameSet.contains(tagName)) {
                            return false;
                        }
                    }
                    return true;
                })
                .map(this::getSafetyUser) // 转换为安全用户
                .collect(Collectors.toList());
        }

    @Override
    public Integer updateUser(User user,User loginUser,HttpServletRequest request) {
        long userId = user.getId();
        if (userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 如果是管理员，允许更新任意用户
        // 如果不是管理员，只允许更新自己的信息
        if (!isAdmin(loginUser) && userId != loginUser.getId()) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        User oldUser = userMapper.selectById(userId);
        if (oldUser == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        int result = userMapper.updateById(user);
        if(result>=0){
            request.getSession().setAttribute(USER_LOGIN_STATE, user);
        }
        return result;
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        if(request == null){
            return null;
        }
        Object userObj = request.getSession().getAttribute(UserService.USER_LOGIN_STATE);
        if(userObj == null){
            return null;
        }
        return (User) userObj;
    }

    @Override
    public User getSafetyUser(User originUser) {
        if (originUser == null) {
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId()); // 设置用户ID
        safetyUser.setUsername(originUser.getUsername()); // 设置昵称
        safetyUser.setUsername(originUser.getUsername()); // 设置用户账户
        safetyUser.setAvatarurl(originUser.getAvatarurl()); // 设置头像
        safetyUser.setGender(originUser.getGender()); // 设置性别
        safetyUser.setPhone(originUser.getPhone()); // 设置电话
        safetyUser.setEmail(originUser.getEmail()); // 设置邮箱
        safetyUser.setPlanetcode(originUser.getPlanetcode()); // 设置星球代码
        safetyUser.setRoleid(originUser.getRoleid()); // 设置角色
        safetyUser.setCreatetime(originUser.getCreatetime()); // 设置创建时间
        safetyUser.setTags(originUser.getTags()); // 设置用户标签
        safetyUser.setPlanetcode(originUser.getPlanetcode());//设置星球编号
        return safetyUser;
    }

    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 获取当前登录的用户
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getRoleid() == UserConstant.ADMIN_ROLE;
    }

    /**
     * 判断指定用户是否为管理员
     *
     * @param loginUser 登录用户对象
     * @return boolean 是否为管理员
     */
    @Override
    public boolean isAdmin(User loginUser) {
        return loginUser != null && loginUser.getRoleid() == UserConstant.ADMIN_ROLE;
    }
}