package cn.ltcraft.item.enums;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;

/**
 * Created by Angel、 on 2024/4/27 22:24
 */
@Getter
public enum ItemQuality {
    NONE("无品质"),
    UNKNOWN("未知"),
    START("入门"),
    PRIMARY("初级"),
    ORDINARY("普通"),
    INTERMEDIATE("中级"),
    SENIOR("高级"),
    ULTIMATE("终极", 30),
    RARE("稀有"),
    EPIC("史诗", 50),
    LEGEND("传说");
    private final String name;
    private int maxLevel = -1;
    ItemQuality(String name){
        this.name = name;
    }
    ItemQuality(String name, int maxLevel){
        this.name = name;
        this.maxLevel = maxLevel;
    }
    public static ItemQuality ofQuality(String quality){
        for (ItemQuality value : values()) {
            if (ObjectUtil.equals(value.getName(), quality)){
                return value;
            }
        }
        return null;
    }
}
