package cn.LTCraft.core.hook.MM.mechanics;

import cn.LTCraft.core.other.Temp;
import cn.LTCraft.core.task.GlobalRefresh;
import cn.LTCraft.core.utils.EntityUtils;
import cn.LTCraft.core.utils.MathUtils;
import cn.LTCraft.core.utils.PlayerUtils;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.AbstractLocation;
import io.lumine.xikage.mythicmobs.adapters.AbstractWorld;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import io.lumine.xikage.mythicmobs.skills.INoTargetSkill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Created by Angel、 on 2022/4/11 18:50
 */
public class Escort extends SkillMechanic implements INoTargetSkill {
    public Escort(String skill, MythicLineConfig mlc){
        super(skill, mlc);
        setAsyncSafe(false);
    }
    @Override
    public boolean cast(SkillMetadata skillMetadata) {
        AbstractEntity entity = skillMetadata.getCaster().getEntity();
        Optional<ActiveMob> activeMobs = MythicMobs.inst().getMobManager().getActiveMob(entity.getUniqueId());
        if (!activeMobs.isPresent())return false;
        ActiveMob activeMob = activeMobs.get();
        if (activeMob.getNewTarget() != null) {
            return false;
        }
        AbstractLocation location = entity.getLocation();
        AbstractWorld world = location.getWorld();
        List<AbstractEntity> livingEntities = world.getLivingEntities();
        for (int i = 0; i < livingEntities.size(); i++) {//不要使用增强for循环 会触发并发修改异常
            AbstractEntity livingEntity = livingEntities.get(i);
            if (livingEntity.isPlayer() && PlayerUtils.isLegitimateName(livingEntity.getName())) {
                double distance = livingEntity.getLocation().distance(entity.getLocation());
                if (distance > 15 || Math.abs(livingEntity.getLocation().getY() - entity.getLocation().getY()) > 3)continue;
                Player player = (Player) livingEntity.getBukkitEntity();
                if (!player.getGameMode().equals(GameMode.SURVIVAL)) {
                    continue;
                }
                AbstractLocation livingEntityLocation = livingEntity.getLocation();
                double yaw = MathUtils.getYaw(BukkitAdapter.adapt(livingEntityLocation), BukkitAdapter.adapt(location));
                double v = MathUtils.getMinAngle(yaw, location.getYaw());
                if (v < 120d / 2){//玩家出现在怪物面前 设置怪物的仇恨
                    if ((player.isSneaking() && distance < 3) || (distance < 8 && !player.isSneaking()) || (player.isSprinting() && distance < 15)) {
                        activeMob.setTarget(livingEntity);
                        Temp.lastBattleTime.put((LivingEntity) entity.getBukkitEntity(), GlobalRefresh.getTick());
                        informNearbyCompanions(entity.getBukkitEntity());
                    }
                }else {
                    if (distance < 3 && !player.isSneaking()) {
                        activeMob.setTarget(livingEntity);
                        Temp.lastBattleTime.put((LivingEntity) entity.getBukkitEntity(), GlobalRefresh.getTick());
                        informNearbyCompanions(entity.getBukkitEntity());//通知周围的怪物警戒
                    }
                }
                if (activeMob.getNewTarget() != null && GlobalRefresh.getTick() - Temp.lastBattleTime.getOrDefault(entity.getBukkitEntity(), GlobalRefresh.getTick()) > 60 * 20){//脱离战斗60s 取消战斗状态
                    activeMob.setTarget(null);
                }
            }
        }
        return true;
    }

    /**
     * 通知团队
     * @param entity 通知团队
     */
    public void informNearbyCompanions(Entity entity){
        World world = entity.getWorld();
        ActiveMob activeMob = EntityUtils.getMythicMob(entity);
        Location location = entity.getLocation();
        Collection<Entity> nearbyEntities = world.getNearbyEntities(location, 4, 3, 4);
        for (Entity next : nearbyEntities) {
            ActiveMob mythicMob = EntityUtils.getMythicMob(next);
            if (mythicMob != null) {
                MythicMob type = mythicMob.getType();
                if (type.equals(activeMob.getType())){
                    mythicMob.setTarget(activeMob.getNewTarget());
                }
            }
        }
    }
}
