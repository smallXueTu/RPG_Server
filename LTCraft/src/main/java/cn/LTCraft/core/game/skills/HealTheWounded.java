package cn.LTCraft.core.game.skills;

import cn.LTCraft.core.utils.Utils;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Collection;

public class HealTheWounded extends BaseSkill {
    private Player owner;
    public HealTheWounded(Player player){
        owner = player;
    }
    public HealTheWounded(Player player, int level, int awakenLevel, boolean awake){
        super(level, awakenLevel, awake);
        owner = player;
    }

    @Override
    public boolean cast(Entity entity) {
        double lost = owner.getMaxHealth() - owner.getHealth();
        double percentage = 30 + level * 10;
        double l = Math.max(lost * (percentage / 100), 1);
        owner.setHealth(Math.min(owner.getHealth() + l, owner.getMaxHealth()));
        owner.sendTitle("§l§a您已被医疗", "§l§e来自§a您§e的§d救死扶伤" + Utils.getLevelStr(awakenLevel) + "§d效果，医疗量：" + Utils.formatNumber(l));
        if (isAwaken()){
            double radius = 3 + awakenLevel;
            Collection<Entity> entities = owner.getWorld().getNearbyEntities(owner.getLocation(), radius, radius, radius);
            entities.removeIf(entity1 -> !(entity1 instanceof Player) || entity1.equals(owner));
            percentage = 30 + awakenLevel * 10;
            for (Entity entity1 : entities) {
                Player p = (Player)entity1;
                lost = p.getMaxHealth() - p.getHealth();
                l = Math.max(lost * (percentage / 100), 1);
                p.setHealth(Math.min(owner.getHealth() + l, owner.getMaxHealth()));
                ((CraftWorld)owner.getWorld()).spawnParticle(Particle.VILLAGER_HAPPY, p.getLocation().add(0, 1.2, 0), 10, 0.5, 0.5, 0.5);
                p.sendTitle("§l§a你已被医疗", "§l§e来自§a"+owner.getName()+"§e的§d救死扶伤" + Utils.getLevelStr(awakenLevel) + "§d效果，医疗量：" + Utils.formatNumber(l));
            }
        }
        ((CraftWorld)owner.getWorld()).spawnParticle(Particle.VILLAGER_HAPPY, owner.getLocation().add(0, 1.2, 0), 10, 0.3, 0.3, 0.3);
        return true;
    }

    public Player getOwner() {
        return owner;
    }
}
