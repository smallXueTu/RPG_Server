package cn.LTCraft.core.utils;

import cn.ltcraft.item.base.subAttrbute.PotionAttribute;
import cn.ltcraft.item.base.subAttrbute.PotionMap;
import cn.ltcraft.item.base.subAttrbute.SkillMap;
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
                int duration = 10 * 20;
                int amplifier = 1;
                double probability = 1;
                if (pInfo.length > 2){
                    duration = Integer.parseInt(pInfo[1]);
                    amplifier = Integer.parseInt(pInfo[2]);
                    probability = Double.parseDouble(pInfo[2]) / 100;
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
}
