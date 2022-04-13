package cn.LTCraft.core.hook.MM.mechanics;

import cn.LTCraft.core.Main;
import cn.LTCraft.core.utils.EntityUtils;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.skills.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.Collection;

/**
 * 呼叫同伴
 * Created by Angel、 on 2022/4/12 22:46
 */
public class CallForHelp extends SkillMechanic implements INoTargetSkill {
    public CallForHelp(String skill, MythicLineConfig mlc){
        super(skill, mlc);
    }

    @Override
    public boolean cast(SkillMetadata skillMetadata) {
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            SkillCaster caster = skillMetadata.getCaster();
            AbstractEntity entity = caster.getEntity();
            int range = entity.isDead()?3:6;
            ActiveMob activeMob = EntityUtils.getMythicMob(entity);
            World world = BukkitAdapter.adapt(entity.getWorld());
            Collection<Entity> nearbyEntities = world.getNearbyEntities(BukkitAdapter.adapt(entity.getLocation()), range, 3, range);
            for (Entity nearbyEntity : nearbyEntities) {
                ActiveMob mythicMob = EntityUtils.getMythicMob(nearbyEntity);
                if (mythicMob == null)continue;
                if (mythicMob.getType().equals(activeMob.getType())) {
                    mythicMob.setTarget(caster.getEntity().getTarget());
                }
            }
        }, 20);
        return true;
    }
}
