package cn.LTCraft.core.hook.MM.mechanics;

import cn.LTCraft.core.entityClass.RandomValue;
import cn.LTCraft.core.utils.WorldUtils;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.AbstractLocation;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill;
import io.lumine.xikage.mythicmobs.skills.ITargetedLocationSkill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.List;

/**
 * 在指定范围召唤多少个雷击
 * Created by Angel、 on 2022/4/23 12:05
 */
public class RandomLightning extends SkillMechanic implements ITargetedLocationSkill, ITargetedEntitySkill {
    private final RandomValue count;
    private final double radius;
    private final RandomValue damage;
    public RandomLightning(String skill, MythicLineConfig mlc){
        super(skill, mlc);
        count = new RandomValue(mlc.getInteger(new String[]{"count", "c"}, 5));
        radius = mlc.getDouble(new String[]{"radius", "r"}, 5);
        damage = new RandomValue(mlc.getDouble(new String[]{"damage", "d"}, 10));
    }
    @Override
    public boolean castAtLocation(SkillMetadata skillMetadata, AbstractLocation abstractLocation) {
        int count = (int) this.count.getValue();
        for (int i = 0; i < count; i++) {
            Location location = WorldUtils.rangeLocation(BukkitAdapter.adapt(abstractLocation), radius);
            LightningStrike lightningStrike = location.getWorld().strikeLightningEffect(location);
            List<Entity> nearbyEntities = lightningStrike.getNearbyEntities(0.5, 0.5, 0.5);
            for (Entity nearbyEntity : nearbyEntities) {
                if (nearbyEntity instanceof LivingEntity){
                    ((LivingEntity) nearbyEntity).damage(damage.getValue(), lightningStrike);
                }
            }
        }
        return true;
    }

    @Override
    public boolean castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
        return castAtLocation(skillMetadata, abstractEntity.getLocation());
    }
}
