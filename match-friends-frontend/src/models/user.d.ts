/**
 * 用户类别
 */
export type UserType = {
    /**
     * 主键
     */
    id: number;

    /**
     * 昵称
     */
    username: string;

    /**
     * 头像
     */
    avatarUrl?: string;

     /**
     * 个人介绍
     */
    profile?: string;

    /**
     * 性别
     */
    gender: number;

    /**
     * 电话
     */
    phone?: string;

    /**
     * 邮箱
     */
    email: string;

    /**
     * 是否有效 (比如被封号之类的)
     */
    isValid: number;

    /**
     * 创建时间 (数据插入时间)
     */
    createtime: Date;

    /**
     * 更新时间 (数据更新时间)
     */
    updatetime: Date;

    /**
     * 是否删除 0:否, 1:是 (逻辑删除)
     */
    isDelete: number;

    /**
     * 星球code
     */
    roleId: number;

     /**
     * 用户角色 0代表普通用户 1代表vip会员 2代表超级管理员
     */
     planetCode: string;

     tags: string | string[];
};   