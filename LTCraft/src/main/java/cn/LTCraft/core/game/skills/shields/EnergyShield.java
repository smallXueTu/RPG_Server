package cn.LTCraft.core.game.skills.shields;

import cn.LTCraft.core.game.skills.BaseSkill;
import cn.LTCraft.core.other.Temp;
import cn.LTCraft.core.utils.PlayerUtils;
import cn.ltcraft.item.LTItemSystem;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class EnergyShield extends BaseShield{
    private Player owner;
    public EnergyShield(Player player, BaseSkill baseSkill){
        super(baseSkill);
        owner = player;
    }
    public EnergyShield(Player player, int level, int awakenLevel, boolean awaken){
        super(level, awakenLevel, awaken);
        owner = player;
    }
    @Override
    public boolean block(EntityDamageEvent event) {
        if (event instanceof EntityDamageByEntityEvent){
            double percentage;
            if (((EntityDamageByEntityEvent) event).getDamager() instanceof Player) {
                percentage = 30 + level * 20;
                event.setDamage(event.getDamage() * (percentage / 100 + 1));
            }else {
                percentage = 50 + level * 20;
                event.setDamage(event.getDamage() * (percentage / 100 + 1));
            }
            owner.getWorld().playSound(owner.getLocation(), Sound.BLOCK_ANVIL_FALL, 1, 1);
        }
        return true;
    }

    @Override
    public boolean doTick(long tick) {
        if (tick % 20 == 0){
            owner.getWorld().playSound(owner.getLocation(), Sound.ENTITY_EVOCATION_ILLAGER_PREPARE_SUMMON, 1, 1);
            PlayerUtils.castMMSkill(owner, "能量护盾");
        }
        return super.doTick(tick);
    }

    @Override
    public void destroy() {
        super.destroy();
        if (isAwaken()){

        }
        Temp.shield.remove(getOwner());
    }

    public Player getOwner() {
        return owner;
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}
