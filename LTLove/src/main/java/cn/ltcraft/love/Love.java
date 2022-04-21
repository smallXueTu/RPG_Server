package cn.ltcraft.love;

import cn.LTCraft.core.task.PlayerClass;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author 寻琴
 * @year 2022年04月19日20:34
 */
public class Love extends JavaPlugin {
    @Override
    public void onEnable() {
        super.onEnable();
        getCommand("结婚").setExecutor(new Command());
    }
    public static enum Sex{
        NONE("无性别", "ta", ""),
        Male("男", "他", "父"),
        Female("女", "她", "妻");
        private static final Map<String, Sex> BY_NAME = new HashMap();
        static {
            Sex[] classes = values();
            for (Sex aClass : classes) {
                BY_NAME.put(aClass.getName(), aClass);
            }
        }
        private String name;
        private String prefix;
        private String suffix;
        Sex(String name, String prefix, String suffix){
            this.name = name;
            this.prefix = prefix;
            this.suffix = suffix;
        }

        public String getName() {
            return name;
        }

        public String getSuffix() {
            return suffix;
        }

        public String getPrefix() {
            return prefix;
        }

        @Override
        public String toString() {
            return name;
        }

        /**
         * 通过名字查找性别
         * @param name 职业性别
         * @return 性别
         */
        public static Sex byName(String name){
            return BY_NAME.getOrDefault(name, NONE);
        }
    }

    /**
     * 获取一对人的称呼
     * @param sex1 玩家1
     * @param sex2 玩家2
     * @return 称呼
     */
    public static String getCall(Sex sex1, Sex sex2){
        if (sex1 == Sex.Male && sex2 == Sex.Male)return "基佬";
        if (sex1 == Sex.Female && sex2 == Sex.Male)return "夫妻";
        if (sex1 == Sex.Male && sex2 == Sex.Female)return "夫妻";
        if (sex1 == Sex.Female && sex2 == Sex.Female)return "百合";
        return "玩家";
    }
    public static String getUnmarriedCall(Sex sex1, Sex sex2){
        if (sex1 == Sex.Male && sex2 == Sex.Male)return "基佬";
        if (sex1 == Sex.Female && sex2 == Sex.Male)return "玩家";
        if (sex1 == Sex.Male && sex2 == Sex.Female)return "玩家";
        if (sex1 == Sex.Female && sex2 == Sex.Female)return "百合";
        return "玩家";
    }
}
