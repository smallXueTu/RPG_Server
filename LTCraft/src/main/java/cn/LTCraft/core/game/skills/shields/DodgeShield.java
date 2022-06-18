package cn.LTCraft.core.game.skills.shields;

import cn.LTCraft.core.game.skills.BaseSkill;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;


/**
 * 闪避护盾
 * Created by Angel、 on 2022/6/19 1:32
 */
public class DodgeShield extends BaseShield{
    public DodgeShield(Player player, BaseSkill baseSkill){
        super(player, baseSkill);
        owner = player;
    }
    public DodgeShield(Player player, int level, int awakenLevel, boolean awaken){
        super(player, level, awakenLevel, awaken);
        owner = player;
    }

    @Override
    public boolean block(EntityDamageEvent event) {
        if (event instanceof EntityDamageByEntityEvent){
            event.setCancelled(true);
        }
        return true;
    }
}
