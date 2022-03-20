package cn.LTCraft.core.hook.MM.mechanics.singletonSkill;

import cn.LTCraft.core.hook.MM.other.ItemRotating;
import cn.LTCraft.core.entityClass.ClutterItem;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DropCheckS extends SkillMechanic implements ITargetedEntitySkill {
    private ClutterItem clutterItem;
    private static Map<Integer, ItemRotating> map = new HashMap<>();
    public DropCheckS(String skill, MythicLineConfig mlc) {
        super(skill, mlc);
        String stringItem = mlc.getString(new String[]{"item", "i"}, "AIR");
        clutterItem = ClutterItem.spawnClutterItem(stringItem);
    }
    @Override
    public boolean castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
        if (!map.containsKey(abstractEntity.getBukkitEntity().getEntityId())){
            map.put(abstractEntity.getBukkitEntity().getEntityId(), new ItemRotating(abstractEntity.getBukkitEntity()));
        }
        World world = abstractEntity.getBukkitEntity().getWorld();
        Collection<Entity> entities = world.getNearbyEntities(abstractEntity.getBukkitEntity().getLocation(), 2, 2, 2);
        for (Entity entity : entities) {
            if (entity instanceof Item && clutterItem.isSimilar(((Item)entity).getItemStack())){
                map.get(abstractEntity.getBukkitEntity().getEntityId()).addEntity((Item) entity);
            }
        }
        return false;
    }
}