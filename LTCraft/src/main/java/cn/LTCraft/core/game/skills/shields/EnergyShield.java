package cn.LTCraft.core.game.skills.shields;

import cn.LTCraft.core.Main;
import cn.LTCraft.core.game.Game;
import cn.LTCraft.core.game.skills.BaseSkill;
import cn.LTCraft.core.other.Temp;
import cn.LTCraft.core.utils.EntityUtils;
import cn.LTCraft.core.utils.PlayerUtils;
import net.minecraft.server.v1_12_R1.DamageSource;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Collection;

public class EnergyShield extends BaseShield{
    private double blockDamage = 0;
    public EnergyShield(Player player, BaseSkill baseSkill){
        super(player, baseSkill);
    }
    public EnergyShield(Player player, int level, int awakenLevel, boolean awaken){
        super(player, level, awakenLevel, awaken);
    }
    @Override
    public boolean block(EntityDamageEvent event) {
        if (event instanceof EntityDamageByEntityEvent){
            blockDamage += event.getDamage();
            double percentage;
            if (((EntityDamageByEntityEvent) event).getDamager() instanceof Player) {
                percentage = 30 + level * 10;//免伤百分比
                event.setDamage(event.getDamage() * (1 - percentage / 100));
            }else {
                percentage = 50 + level * 10;
                event.setDamage(event.getDamage() * (1 - percentage / 100));
            }
            owner.getWorld().playSound(owner.getLocation(), Sound.BLOCK_ANVIL_FALL, 1, 1);
        }
        return true;
    }

    @Override
    public boolean doTick(long tick) {
        if (tick % 20 == 0){
            owner.getWorld().playSound(owner.getLocation(), Sound.ENTITY_EVOCATION_ILLAGER_PREPARE_SUMMON, 1, 1);
            PlayerUtils.castMMSkill((Player) owner, "能量护盾");
        }
        return super.doTick(tick);
    }

    @Override
    public void destroy() {
        super.destroy();
        if (isAwaken()){
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                World world = owner.getWorld();
                ((CraftWorld) world).getHandle().createExplosion((((CraftPlayer) owner)).getHandle(), owner.getLocation().getX(), owner.getLocation().getY(), owner.getLocation().getZ(), 5, false, false);
                Collection<Entity> nearbyEntities = world.getNearbyEntities(owner.getLocation(), 3, 3, 3);
                double damage = blockDamage * (0.2 + awakenLevel * 0.1);
                for (Entity nearbyEntity : nearbyEntities) {
                    if (Game.rpgWorlds.contains(world.getName()) && nearbyEntity instanceof Player) {
                        continue;
                    }
                    EntityUtils.repel(nearbyEntity, owner.getLocation(), 2, 10);
                    ((CraftEntity) nearbyEntity).getHandle().damageEntity(DamageSource.a(((CraftPlayer) owner).getHandle()), (float) damage);
                }
            }, 0);
        }
    }
}
