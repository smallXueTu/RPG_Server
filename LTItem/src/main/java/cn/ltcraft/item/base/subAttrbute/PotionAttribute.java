package cn.ltcraft.item.base.subAttrbute;

import cn.LTCraft.core.utils.GameUtils;
import cn.LTCraft.core.utils.Utils;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionAttribute extends PotionEffect {
    private final double probability;
    public PotionAttribute(PotionEffectType type, int duration, int amplifier, double probability){
        super(type, duration, amplifier);
        this.probability = probability;
    }

    public double getProbability() {
        return probability;
    }

    @Override
    public String toString() {
        return GameUtils.getPotionName(getType()) + Utils.getLevelStr(getAmplifier()) + " " + Utils.formatNumber(getDuration() / 20d) + "s " + Utils.formatNumber(probability * 100) + "%";
    }
    public String toMapString(){
        return super.toString();
    }
}