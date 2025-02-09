type Team = {
    id?: number;            // 主键，自增
    name: string;           // 队伍名称
    description?: string;   // 描述（可选）
    maxNum: number;         // 最大人数
    expireTime: string|Date;     // 过期时间，使用字符串格式（ISO 8601）
    userId: number;         // 用户ID
    status: number;         // 状态：0-公开, 1-私有, 2-加密
    password?: string;      // 密码（可选）
    createTime?: string;    // 创建时间（可选）
    updateTime?: string;    // 更新时间（可选）
    isDelete?: number;      // 是否删除：0-否, 1-是
    memberNum: number;      // 队员人数
  }
  export default Team;
  