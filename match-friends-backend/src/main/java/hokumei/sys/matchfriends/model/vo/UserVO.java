package hokumei.sys.matchfriends.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.util.Date;

/**
 * 用户包装类
 */
public class UserVO {
	/**
	 * 主键
	 */
	@TableId(type = IdType.AUTO)
	private Long id;

	/**
	 * 昵称
	 */
	private String username;

	/**
	 * 头像
	 */
	private String avatarurl;

	/**
	 * 性别
	 */
	private Integer gender;

	/**
	 * 电话
	 */
	private String phone;

	/**
	 * 邮箱
	 */
	private String email;

	/**
	 * 是否有效 (比如被封号之类的)
	 */
	private Integer isvalid;

	/**
	 * 创建时间 (数据插入时间)
	 */
	private Date createtime;

	/**
	 * 更新时间 (数据更新时间)
	 */
	private Date updatetime;
	/**
	 * 0==普通用户 1==管理员 2==vip
	 */
	private Integer roleid;

	/**
	 * 星球编号
	 */
	private String planetcode;

	/**
	 *
	 */
	private String tags;

	/**
	 * 用户简介
	 */
	private String profile;
}
