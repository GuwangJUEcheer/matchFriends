package hokumei.sys.matchfriends.service;

import com.baomidou.mybatisplus.extension.service.IService;
import hokumei.sys.matchfriends.model.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
* @author 17685
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2025-01-11 21:11:08
*/
public interface UserService extends IService<User> {
    String USER_LOGIN_STATE = "LoginState";
    /**
     * 用户注册
     *
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param checkPassword 校验密码
     * @return 注册结果（返回用户ID）
     */
    long userRegister(String userAccount, String userPassword, String checkPassword,String planetCode);

    /**
     * 登录方法
     *
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @return
     */
    User login (String userAccount, String userPassword, HttpServletRequest request);


    void logOut(HttpServletRequest request);

    List<User> searchUserByTags(List<String> tags);

    Integer updateUser(User user,User loginUser,HttpServletRequest request);

    User getLoginUser(HttpServletRequest request);

    boolean isAdmin(HttpServletRequest request);

    boolean isAdmin(User loginUser);

    User getSafetyUser(User originUser);

    List<User> matchUsers(long num,User loginUser);
}
