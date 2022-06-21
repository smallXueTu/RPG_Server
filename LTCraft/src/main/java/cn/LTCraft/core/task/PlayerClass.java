package cn.LTCraft.core.task;

import java.util.HashMap;
import java.util.Map;

public enum PlayerClass {
    ASSASSIN("刺客"),
    WARRIOR("战士"),
    MASTER("法师"),
    MINISTER("牧师"),
    NONE("无职业");
    private static final Map<String, PlayerClass> BY_NAME = new HashMap<>();
    static {
        PlayerClass[] classes = values();
        for (PlayerClass aClass : classes) {
            BY_NAME.put(aClass.getName(), aClass);
        }
    }
    private final String name;
    PlayerClass(String name){
        this.name = name;
    }

    /**
     * 获取职业名字
     * @return 职业名字
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * 通过名字查找职业
     * @param name 职业名字
     * @return 职业
     */
    public static PlayerClass byName(String name){
        return BY_NAME.get(name);
    }
}
