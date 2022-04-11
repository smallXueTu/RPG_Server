package cn.LTCraft.core.hook.MM.mechanics;

import cn.LTCraft.core.utils.MathUtils;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.AbstractLocation;
import io.lumine.xikage.mythicmobs.adapters.AbstractWorld;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.skills.INoTargetSkill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * Created by Angel、 on 2022/4/11 18:50
 */
public class Escort extends SkillMechanic implements INoTargetSkill {
    public Escort(String skill, MythicLineConfig mlc){
        super(skill, mlc);
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
        for (AbstractEntity livingEntity : world.getLivingEntities()) {
            if (livingEntity.isPlayer()) {
                double distance = livingEntity.getLocation().distance(entity.getLocation());
                if (distance > 15)continue;
                Player player = (Player) livingEntity.getBukkitEntity();
                AbstractLocation livingEntityLocation = livingEntity.getLocation();
                double x = livingEntityLocation.getX() - location.getX();
                double z = livingEntityLocation.getZ() - location.getZ();
                double diff = Math.abs(x) + Math.abs(z);
                double yaw = (-Math.atan2(x / diff, z / diff) * 180 / Math.PI + 360) % 360;
                System.out.println(yaw);
                System.out.println(location.getYaw());
                double v = MathUtils.getMinAngle(yaw, location.getYaw());
                System.out.println(v);
                System.out.println(distance);
                if (v < 120d / 2){
                    if ((player.isSneaking() && distance < 3) || (distance < 8 && !player.isSneaking()) || (player.isSprinting() && distance < 15)) {
                        activeMob.setTarget(livingEntity);
                    }
                }else {
                    if (distance < 3 && !player.isSneaking()) {
                        activeMob.setTarget(livingEntity);
                    }
                }
            }
        }
        return true;
    }
}
