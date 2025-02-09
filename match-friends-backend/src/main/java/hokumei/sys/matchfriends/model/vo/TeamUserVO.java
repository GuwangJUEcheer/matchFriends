package hokumei.sys.matchfriends.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 队伍用户返回对象
 */
@Data
public class TeamUserVO implements Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * 主键，自增
	 */
	@TableId(type = IdType.AUTO)
	private Long id;

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
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 更新时间
	 */
	private Date updateTime;

	/**
	 * 是否删除：0-否, 1-是
	 */
	private Integer isDelete;

	/**
	 * 已经加入队伍
	 */
	private boolean hasJoin = false;

	/**
	 * 加入的人数
	 */
	private int memberNum;

    List<UserVO> userVOList;

	/**
	 * 创建人信息
	 */
	UserVO createUser;
}
