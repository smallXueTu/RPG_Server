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
        Bukkit.getPluginManager().registerEvents(new Listener(), this);
        getCommand("结婚").setExecutor(new Command());
    }
    public static enum Sex{
        NONE(""),
        Male("男"),
        Female("女"),
        Secrecy("保密");
        private static final Map<String, Sex> BY_NAME = new HashMap();
        static {
            Sex[] classes = values();
            for (Sex aClass : classes) {
                BY_NAME.put(aClass.getName(), aClass);
            }
        }
        private String name;
        Sex(String name){
            this.name = name;
        }

        public String getName() {
            return name;
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
}
