package cn.LTCraft.core.game.skills.shields;

import cn.LTCraft.core.game.skills.BaseSkill;
import cn.LTCraft.core.other.Temp;
import cn.LTCraft.core.utils.EntityUtils;
import cn.LTCraft.core.utils.PlayerUtils;
import net.minecraft.server.v1_12_R1.DamageSource;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;


/**
 * 闪避护盾
 * Created by Angel、 on 2022/6/19 1:32
 */
public class DodgeShield extends BaseShield{
    private Player owner;
    public DodgeShield(Player player, BaseSkill baseSkill){
        super(baseSkill);
        owner = player;
    }
    public DodgeShield(Player player, int level, int awakenLevel, boolean awaken){
        super(level, awakenLevel, awaken);
        owner = player;
    }

    @Override
    public boolean block(EntityDamageEvent event) {
        if (event instanceof EntityDamageByEntityEvent){
            event.setCancelled(true);
        }
        return true;
    }

    public Player getOwner() {
        return owner;
    }
}
