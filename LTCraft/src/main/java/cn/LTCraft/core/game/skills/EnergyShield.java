package cn.LTCraft.core.game.skills;

import cn.LTCraft.core.game.skills.shields.BaseShield;
import cn.LTCraft.core.other.Temp;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class EnergyShield extends BaseSkill {
    private Player owner;
    public EnergyShield(Player player, int level, int awakenLevel, boolean awaken){
        super(level, awakenLevel, awaken);
        owner = player;
    }
    @Override
    public boolean cast(Entity entity) {
        cn.LTCraft.core.game.skills.shields.BaseShield baseShield = BaseShield.getShieldObj("能量护盾", owner, this);
        if (baseShield != null) {
            baseShield.setRemainingTick((2 + level) * 20);
            Temp.addShield(owner, baseShield);
        }
        return true;
    }
}
