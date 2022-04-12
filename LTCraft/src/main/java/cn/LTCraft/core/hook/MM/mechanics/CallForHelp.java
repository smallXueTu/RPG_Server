package cn.LTCraft.core.hook.MM.mechanics;

import cn.LTCraft.core.utils.EntityUtils;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.skills.INoTargetSkill;
import io.lumine.xikage.mythicmobs.skills.SkillCaster;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
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
        SkillCaster caster = skillMetadata.getCaster();
        AbstractEntity entity = caster.getEntity();
        ActiveMob activeMob = EntityUtils.getMythicMob(entity);
        World world = BukkitAdapter.adapt(entity.getWorld());
        Collection<Entity> nearbyEntities = world.getNearbyEntities(BukkitAdapter.adapt(entity.getLocation()), 8, 3, 8);
        for (Entity nearbyEntity : nearbyEntities) {
            ActiveMob mythicMob = EntityUtils.getMythicMob(nearbyEntity);
            if (mythicMob.getType().equals(activeMob.getType())) {
                mythicMob.setTarget(caster.getEntity().getTarget());
            }
        }
        return true;
    }
}
