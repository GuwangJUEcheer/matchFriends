package hokumei.sys.matchfriends.model.dto;

import lombok.Data;

/**
 * Team查询封装类
 */
@Data
public class TeamQuery {

    /**
     * 主键，自增
     */
    private Long id;

    /**
     * 搜索关键词
     */
    private String searchText;

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
     * 用户ID
     */
    private Long userId;

    /**
     * 状态：0-公开, 1-私有, 2-加密
     */
    private Integer status;

    private int pageNum;

    private int pageSize;

}
