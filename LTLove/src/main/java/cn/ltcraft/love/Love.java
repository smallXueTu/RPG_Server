package cn.ltcraft.love;

import cn.LTCraft.core.entityClass.Cooling;
import cn.LTCraft.core.entityClass.PlayerConfig;
import cn.LTCraft.core.task.PlayerClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author 寻琴
 * @year 2022年04月19日20:34
 */
public class Love extends JavaPlugin {
    private static Map<String, String> loves = new HashMap<>();
    private static Map<String, Sex> sexs = new HashMap<>();
    @Override
    public void onEnable() {
        super.onEnable();
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onMoveEvent(PlayerMoveEvent event){
                Player player = event.getPlayer();
                String love = getLove(player);
                Player partner = Bukkit.getPlayer(love);
                if (partner != null && player.isOnline()) {
                    if (partner.getWorld().equals(player.getWorld())) {
                        Location partnerLocation = partner.getLocation();
                        if (partnerLocation.distance(event.getFrom()) > 0.5 && partnerLocation.distance(event.getTo()) < 0.5){
                            if (Cooling.isCooling(player, "贴贴")) {
                                return;
                            }
                            Cooling.cooling(player, "贴贴", 10);
                        }
                    }
                }
            }
            @EventHandler
            public void onQuitEvent(PlayerQuitEvent event){
                Player player = event.getPlayer();
                loves.remove(player.getName());
                sexs.remove(player.getName());
            }
        }, this);
        getCommand("结婚").setExecutor(new Command());
    }

    /**
     * 获取和设置伴侣
     * @param player 玩家
     * @return 伴侣
     */
    public static String getLove(Player player){
        String love = loves.get(player.getName());
        if (love == null){
            PlayerConfig playerConfig = PlayerConfig.getPlayerConfig(player);
            love = playerConfig.getConfig().getString("伴侣", "");
            loves.put(player.getName(), love);
        }
        return love;
    }

    /**
     * 提供配置 用于节省微小的性能损失
     * @param player 玩家
     * @param playerConfig 配置
     * @return 伴侣
     */
    public static String getLove(Player player, PlayerConfig playerConfig){
        String love = loves.get(player.getName());
        if (love == null){
            if (playerConfig == null)playerConfig = PlayerConfig.getPlayerConfig(player);
            love = playerConfig.getConfig().getString("伴侣", "");
            loves.put(player.getName(), love);
        }
        return love;
    }
    public static void setLove(Player player, String love){
        PlayerConfig playerConfig = PlayerConfig.getPlayerConfig(player);
        playerConfig.getConfig().set("伴侣", love);
        loves.put(player.getName(), love);
    }
    public static void setLove(Player player, String love, PlayerConfig playerConfig){
        if (playerConfig == null)playerConfig = PlayerConfig.getPlayerConfig(player);
        playerConfig.getConfig().set("伴侣", love);
        loves.put(player.getName(), love);
    }

    /**
     * 获取和设置性别
     * @param player 玩家
     * @return 性别
     */
    public static Sex getSex(Player player){
        Sex sex = sexs.get(player.getName());
        if (sex == null){
            PlayerConfig playerConfig = PlayerConfig.getPlayerConfig(player);
            sex = Sex.byName(playerConfig.getConfig().getString("性别", ""));
            sexs.put(player.getName(), sex);
        }
        return sex;
    }
    public static Sex getSex(Player player, PlayerConfig playerConfig){
        Sex sex = sexs.get(player.getName());
        if (sex == null){
            if (playerConfig == null)playerConfig = PlayerConfig.getPlayerConfig(player);
            sex = Sex.byName(playerConfig.getConfig().getString("性别", ""));
            sexs.put(player.getName(), sex);
        }
        return sex;
    }
    public static void setSex(Player player, Sex sex){
        PlayerConfig playerConfig = PlayerConfig.getPlayerConfig(player);
        playerConfig.getConfig().set("伴侣", player);
        sexs.put(player.getName(), sex);
    }
    public static void setSex(Player player, Sex sex, PlayerConfig playerConfig){
        if (playerConfig == null)playerConfig = PlayerConfig.getPlayerConfig(player);
        playerConfig.getConfig().set("伴侣", player);
        sexs.put(player.getName(), sex);
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

    /**
     * TODO:自定义称呼
     * 获取结婚后的称呼
     * @param sex 性别
     * @return 老公/老婆
     */
    public static String getAfterMarriageCall(Sex sex){
        if (sex == Sex.Male)return "老公";
        return "老婆";
    }
}
