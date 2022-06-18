package cn.LTCraft.core.game.skills;

import cn.LTCraft.core.game.skills.shields.BaseShield;
import cn.LTCraft.core.other.Temp;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class TheWayOfAssassination extends BaseSkill{
    private Player owner;
    public TheWayOfAssassination(Player player, int level, int awakenLevel, boolean awaken) {
        super(level, awakenLevel, awaken);
        owner = player;
    }

    @Override
    public boolean cast(Entity entity) {
        owner.sendTitle("§l§a释放成功", "§l§e来自§a您§e的§d行刺之道§d技能。");
        owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, (3 + level) * 20, 3));
        owner.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, (3 + level) * 20, 1));
        cn.LTCraft.core.game.skills.shields.BaseShield baseShield = BaseShield.getShieldObjForPlayer("闪避护盾", owner, this);
        if (baseShield != null) {
            baseShield.setRemainingTick((3 + awakenLevel) * 20);
            Temp.addShield(owner, baseShield);
        }
        return false;
    }
}
