package hokumei.sys.matchfriends.domain;

import lombok.Data;

import java.util.Date;

/**
 * 组队修改请求Bean
 */
@Data
public class TeamUpdateRequest {

	long id;
	/**
	 * 队伍名称
	 */
	private String name;

	/**
	 * 描述
	 */
	private String description;

	/**
	 * 最大人数
	 */
	private Integer maxNum;

	/**
	 * 过期时间
	 */
	private Date expireTime;

	/**
	 * 用户ID
	 */
	private Long userId;

	/**
	 * 状态：0-公开, 1-私有, 2-加密
	 */
	private Integer status;

	/**
	 * 密码
	 */
	private String password;
}
