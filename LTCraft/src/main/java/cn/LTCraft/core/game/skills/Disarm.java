package cn.LTCraft.core.game.skills;

import cn.LTCraft.core.Main;
import cn.LTCraft.core.entityClass.PlayerState;
import cn.LTCraft.core.other.Temp;
import cn.LTCraft.core.utils.PlayerUtils;
import cn.LTCraft.core.utils.ReflectionHelper;
import cn.LTCraft.core.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.scheduler.CraftScheduler;
import org.bukkit.craftbukkit.v1_12_R1.scheduler.CraftTask;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.List;

public class Disarm extends BaseSkill{
    private Player owner;
    public Disarm(Player player, int level, int awakenLevel, boolean awaken) {
        super(level, awakenLevel, awaken);
        owner = player;
    }
    @Override
    public boolean cast(Entity e) {
        owner.sendTitle("§a§l释放成功", "§a您§e释放了§d缴械" + Utils.getLevelStr(level) + "§e技能。");
        double radius = 3 + level;
        List<Entity> collection = owner.getNearbyEntities(radius, radius, radius);
        for (Entity entity : collection) {
            if (entity instanceof LivingEntity){
                if (entity instanceof Player && entity != owner){
                    ((Player)entity).sendTitle("§l§a受技能影响", "§l§e来自§a"+owner.getName()+"§e的§d缴械§e和§d沉默§e技能，你的主武器已被缴械到背包！");
                    PlayerUtils.castMMSkill(((Player) entity), "缴械自己");
                }
                Temp.addSilence(entity, (5 + level) * 20);
            }
        }
        if (isAwaken()) {
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                if (!owner.getAllowFlight()) {
                    owner.setAllowFlight(true);
                    owner.setFlying(true);
                    ((CraftPlayer) owner).setMomentum(new Vector(0, 1, 0));
                    CraftTask bukkitTask = ((CraftTask) ((CraftScheduler) Bukkit.getScheduler()).runTaskLater(Main.getInstance(), () -> {
                        owner.setAllowFlight(false);
                        owner.setFlying(false);
                    }, (3 + awakenLevel) * 20L));
                    Temp.lock.lock();
                    Temp.playerStates.get(owner).add(new PlayerState(owner, "飞行 %s%S", () -> ((long) ReflectionHelper.getPrivateValue(CraftTask.class, bukkitTask, "nextRun") - (int) ReflectionHelper.getPrivateValue(CraftScheduler.class, Bukkit.getScheduler(), "currentTick")) / 20d));
                    Temp.lock.unlock();
                }
            }, 0);
        }
        return true;
    }
}
