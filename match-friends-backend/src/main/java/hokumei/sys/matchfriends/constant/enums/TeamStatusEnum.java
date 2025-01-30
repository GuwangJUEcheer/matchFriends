package hokumei.sys.matchfriends.constant.enums;

import lombok.Getter;

/**
 * 队伍状态枚举
 */

@Getter
public enum TeamStatusEnum {
    PUBLIC(0, "公开"),
    PRIVATE(1, "私有"),
    SECRET(2, "加密");

    private int value;
    private String text;

    // 构造方法
    TeamStatusEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

	// 根据值获取对应的枚举
    public static TeamStatusEnum getEnumByValue(Integer value) {
        if (value == null) {
            return null;
        }
        TeamStatusEnum[] values = TeamStatusEnum.values();
        for (TeamStatusEnum teamStatusEnum : values) {
            if (teamStatusEnum.getValue() == value) {
                return teamStatusEnum;
            }
        }
        return null;
    }
}
