package hokumei.sys.matchfriends.domain;
import lombok.Data;
import java.io.Serializable;

/**
 * 用户注册请求 DTO
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    private String username;

    private String password;

    private String checkPassword;

    private String planetCode;
}

