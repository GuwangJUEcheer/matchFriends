package hokumei.sys.matchfriends.domain;

import java.io.Serializable;

/**
 * 封装分页请求
 */
public class PageRequest implements Serializable {

    /**
     * 一页多少条
     */
    protected int pageSize;

    /**
     * 当前的页数
     */
    protected int pageNum;
}
