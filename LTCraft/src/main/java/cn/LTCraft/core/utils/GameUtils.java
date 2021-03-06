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
            return "??????";
        }else if (PotionEffectType.SLOW.equals(type)) {
            return "??????";
        }else if (PotionEffectType.FAST_DIGGING.equals(type)) {
            return "????????????";
        }else if (PotionEffectType.SLOW_DIGGING.equals(type)) {
            return "????????????";
        }else if (PotionEffectType.INCREASE_DAMAGE.equals(type)) {
            return "??????";
        }else if (PotionEffectType.HEAL.equals(type)) {
            return "????????????";
        }else if (PotionEffectType.HARM.equals(type)) {
            return "????????????";
        }else if (PotionEffectType.JUMP.equals(type)) {
            return "????????????";
        }else if (PotionEffectType.CONFUSION.equals(type)) {
            return "??????";
        }else if (PotionEffectType.REGENERATION.equals(type)) {
            return "????????????";
        }else if (PotionEffectType.DAMAGE_RESISTANCE.equals(type)) {
            return "????????????";
        }else if (PotionEffectType.FIRE_RESISTANCE.equals(type)) {
            return "??????";
        }else if (PotionEffectType.WATER_BREATHING.equals(type)) {
            return "????????????";
        }else if (PotionEffectType.INVISIBILITY.equals(type)) {
            return "??????";
        }else if (PotionEffectType.BLINDNESS.equals(type)) {
            return "??????";
        }else if (PotionEffectType.NIGHT_VISION.equals(type)) {
            return "??????";
        }else if (PotionEffectType.HUNGER.equals(type)) {
            return "??????";
        }else if (PotionEffectType.WEAKNESS.equals(type)) {
            return "??????";
        }else if (PotionEffectType.POISON.equals(type)) {
            return "??????";
        }else if (PotionEffectType.WITHER.equals(type)) {
            return "??????";
        }else if (PotionEffectType.HEALTH_BOOST.equals(type)) {
            return "????????????";
        }else if (PotionEffectType.ABSORPTION.equals(type)) {
            return "????????????";
        }else if (PotionEffectType.SATURATION.equals(type)) {
            return "??????";
        }else if (PotionEffectType.GLOWING.equals(type)) {
            return "??????";
        }else if (PotionEffectType.LEVITATION.equals(type)) {
            return "??????";
        }else if (PotionEffectType.LUCK.equals(type)) {
            return "??????";
        }else if (PotionEffectType.UNLUCK.equals(type)) {
            return "??????";
        }
        return "??????";
    }

    /**
     * ????????????
     * @param location x:y:z:??????
     * @return ?????? ???????????????????????????Null
     */
    public static Location spawnLocation(String location){
        if (location == null || location.isEmpty())return null;
        String[] split = location.split(":");
        World world = Bukkit.getWorld(split[3]);
        if (world == null)return null;
        return new Location(world, Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]));
    }

    /**
     * ??????????????????String
     * @param location ??????
     * @return x:y:z:??????
     */
    public static String spawnLocationString(Location location){
        return location.getBlockX() + ":" + location.getBlockY() + ":" + location.getBlockZ() + ":" + location.getWorld().getName();
    }

    /**
     * ????????????????????????????????? TODO: ????????????
     * ????????????????????????
     * @param level ??????
     * @return ????????????
     */
    public static String getLevelPrefix(String level){
        switch (level){
            case "??????":
            case "??????":
            case "??????":
                return "";
            case "??????":
                return "??r";//??????
            case "??????":
                return "??b";//?????????
            case "??????":
                return "??4";//?????????
            case "??????":
                return "??c";//?????????
            case "??????":
                return "??1";//?????????
            case "??????":
                return "??9";//?????????
            case "??????":
                return "??e";//??????
            case "??????":
                return "??a";//?????????
            default:
                return "";
        }
    }
}
