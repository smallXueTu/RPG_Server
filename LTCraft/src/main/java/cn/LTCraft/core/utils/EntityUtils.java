package cn.LTCraft.core.utils;

import cn.ltcraft.item.objs.PlayerAttribute;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import net.minecraft.server.v1_12_R1.EntityItem;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftItem;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.Optional;

public class EntityUtils {

    /**
     * 击退实体
     * @param entity 击退的实体
     * @param primary 原坐标
     * @param distance 距离 注意，此距离不是方块距离
     * @param height 击飞高度 注意，此距离不是方块距离
     */
    public static void repel(Entity entity, Location primary, double distance, double height){
        if (distance == 0 && height == 0)return;
        double x = entity.getLocation().getX() - primary.getX();
        double z = entity.getLocation().getZ() - primary.getZ();
        double f = Math.sqrt(x * x + z * z);
        if(f > 0) {
            f = 1 / f;
            if (entity instanceof Player) {
                CraftPlayer craftPlayer = (CraftPlayer) entity;
                double v = 1 - PlayerAttribute.getPlayerAttribute(craftPlayer).getTenacity();
                craftPlayer.setMomentum(new Vector(x * f * distance * v, 0.1 * height * v, z * f * distance * v));
            }else {
                ((CraftEntity)entity).setMomentum(new Vector(x * f * distance, 0.1 * height, z * f * distance));
                ((CraftEntity)entity).getHandle().velocityChanged = false;
                ((CraftEntity)entity).getHandle().impulse = true;
            }
        }
    }
    public static void repel(Entity entity, Location primary, double distance) {
        repel(entity, primary, distance, 1);
    }

    /**
     * 范围恢复
     */
    public static void rangeRecovery(Location location, double range, int amount){
        Collection<Entity> entities = location.getWorld().getNearbyEntities(location, range, range, range);
        for (Entity entity : entities) {
            if (entity instanceof Player)
            ((CraftPlayer)entity).getHandle().heal(amount);
        }
    }
    public static int getItemAge(Item item){
        EntityItem handle = (EntityItem) ((CraftItem) item).getHandle();
        int age = -1;
        try {
            age = ReflectionHelper.getPrivateValue(handle.getClass(), handle, "age");
        }catch (NullPointerException e){
            age = -1;
        }
        return age;
    }

    /**
     * 获取MythicMobs实体的类
     * @return ActiveMob
     */
    public static ActiveMob getMythicMob(Entity entity){
        Optional<ActiveMob> activeMob = MythicMobs.inst().getMobManager().getActiveMob(entity.getUniqueId());
        return activeMob.orElse(null);
    }
}
