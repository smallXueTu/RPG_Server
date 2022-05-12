package cn.LTCraft.core.utils;

import cn.ltcraft.item.base.subAttrbute.PotionAttribute;
import cn.ltcraft.item.base.subAttrbute.PotionMap;
import cn.ltcraft.item.base.subAttrbute.SkillMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;

public class GameUtils {
    public static Map<PotionEffectType, PotionAttribute> analyticalPotion(String potions){
        Map<PotionEffectType, PotionAttribute> map = new PotionMap<>();
        if (potions == null)return map;
        String[] ps = potions.split("&");
        for (String p : ps) {
            try {
                String[] pInfo = p.split(":");
                if (pInfo.length < 2)continue;
                PotionEffectType potionEffectType;
                if (pInfo[0].matches("[0-9]+")){
                    potionEffectType = PotionEffectType.getById(Integer.parseInt(pInfo[0]));
                }else {
                    potionEffectType = PotionEffectType.getByName(pInfo[0]);
                }
                int duration = 60 * 20;
                int amplifier = 1;
                double probability = 1;
                if (pInfo.length > 2){
                    duration = Integer.parseInt(pInfo[1]);
                    amplifier = Integer.parseInt(pInfo[2]);
                    probability = Double.parseDouble(pInfo[3]) / 100;
                }else {
                    amplifier = Integer.parseInt(pInfo[1]);
                }
                map.put(potionEffectType, new PotionAttribute(
                        potionEffectType, duration,
                        amplifier, probability
                ));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return map;
    }
    public static Map<String, Double> analyticalSkill(String skills){
        Map<String, Double> map = new SkillMap<>();
        if (skills == null)return map;
        String[] ss = skills.split("&");
        for (String s : ss) {
            try {
                String[] sInfo = s.split(":");
                if (sInfo.length < 2)continue;
                map.put(sInfo[0], Double.parseDouble(sInfo[1]));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return map;
    }
    public static String getPotionName(PotionEffectType type){
        if (PotionEffectType.SPEED.equals(type)) {
            return "速度";
        }else if (PotionEffectType.SLOW.equals(type)) {
            return "缓慢";
        }else if (PotionEffectType.FAST_DIGGING.equals(type)) {
            return "挖掘迅速";
        }else if (PotionEffectType.SLOW_DIGGING.equals(type)) {
            return "挖掘缓慢";
        }else if (PotionEffectType.INCREASE_DAMAGE.equals(type)) {
            return "力量";
        }else if (PotionEffectType.HEAL.equals(type)) {
            return "瞬间治疗";
        }else if (PotionEffectType.HARM.equals(type)) {
            return "瞬间伤害";
        }else if (PotionEffectType.JUMP.equals(type)) {
            return "跳跃提升";
        }else if (PotionEffectType.CONFUSION.equals(type)) {
            return "反胃";
        }else if (PotionEffectType.REGENERATION.equals(type)) {
            return "生命恢复";
        }else if (PotionEffectType.DAMAGE_RESISTANCE.equals(type)) {
            return "抗性提升";
        }else if (PotionEffectType.FIRE_RESISTANCE.equals(type)) {
            return "抗火";
        }else if (PotionEffectType.WATER_BREATHING.equals(type)) {
            return "水下呼吸";
        }else if (PotionEffectType.INVISIBILITY.equals(type)) {
            return "隐身";
        }else if (PotionEffectType.BLINDNESS.equals(type)) {
            return "失明";
        }else if (PotionEffectType.NIGHT_VISION.equals(type)) {
            return "夜视";
        }else if (PotionEffectType.HUNGER.equals(type)) {
            return "饥饿";
        }else if (PotionEffectType.WEAKNESS.equals(type)) {
            return "虚弱";
        }else if (PotionEffectType.POISON.equals(type)) {
            return "中毒";
        }else if (PotionEffectType.WITHER.equals(type)) {
            return "凋零";
        }else if (PotionEffectType.HEALTH_BOOST.equals(type)) {
            return "生命提升";
        }else if (PotionEffectType.ABSORPTION.equals(type)) {
            return "伤害吸收";
        }else if (PotionEffectType.SATURATION.equals(type)) {
            return "饱和";
        }else if (PotionEffectType.GLOWING.equals(type)) {
            return "发光";
        }else if (PotionEffectType.LEVITATION.equals(type)) {
            return "漂浮";
        }else if (PotionEffectType.LUCK.equals(type)) {
            return "幸运";
        }else if (PotionEffectType.UNLUCK.equals(type)) {
            return "霉运";
        }
        return "未知";
    }

    /**
     * 解析位置
     * @param location x:y:z:世界
     * @return 位置 如果世界不存在返回Null
     */
    public static Location spawnLocation(String location){
        if (location == null || location.isEmpty())return null;
        String[] split = location.split(":");
        World world = Bukkit.getWorld(split[3]);
        if (world == null)return null;
        return new Location(world, Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]));
    }

    /**
     * 从位置解析为String
     * @param location 位置
     * @return x:y:z:世界
     */
    public static String spawnLocationString(Location location){
        return location.getBlockX() + ":" + location.getBlockY() + ":" + location.getBlockZ() + ":" + location.getWorld().getName();
    }

    /**
     * 获取指定等级的颜色前缀 TODO: 改为枚举
     * 以下品质从低到高
     * @param level 等级
     * @return 颜色前缀
     */
    public static String getLevelPrefix(String level){
        switch (level){
            case "入门":
            case "初级":
            case "普通":
                return "";
            case "中级":
                return "§r";//白色
            case "高级":
                return "§b";//浅蓝色
            case "终极":
                return "§4";//深红色
            case "史诗":
                return "§c";//浅红色
            case "传说":
                return "§1";//深蓝色
            case "神话":
                return "§9";//浅蓝色
            case "至臻":
                return "§e";//黄色
            case "稀有":
                return "§a";//浅绿色
            default:
                return "";
        }
    }
}
