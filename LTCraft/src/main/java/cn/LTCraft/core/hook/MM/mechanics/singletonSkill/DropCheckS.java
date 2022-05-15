package cn.LTCraft.core.hook.MM.mechanics.singletonSkill;

import cn.LTCraft.core.hook.MM.other.ItemRotating;
import cn.LTCraft.core.entityClass.ClutterItem;
import cn.LTCraft.core.utils.ItemUtils;
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
        Entity bukkitEntity = abstractEntity.getBukkitEntity();
        if (!map.containsKey(bukkitEntity.getEntityId())){
            map.put(bukkitEntity.getEntityId(), new ItemRotating(bukkitEntity));
        }
        World world = bukkitEntity.getWorld();
        Collection<Entity> entities = world.getNearbyEntities(bukkitEntity.getLocation(), 2, 2, 2);
        for (Entity entity : entities) {
            if (entity == null)continue;
            if (entity.getCustomName().equals(bukkitEntity.getCustomName()) && entity != bukkitEntity)entity.remove();
            if (entity instanceof Item && clutterItem.isSimilar(ItemUtils.cleanVar(((Item)entity).getItemStack().clone()))){
                map.get(bukkitEntity.getEntityId()).addEntity((Item) entity);
            }
        }
        return false;
    }
}