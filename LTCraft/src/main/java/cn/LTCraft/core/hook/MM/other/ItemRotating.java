package cn.LTCraft.core.hook.MM.other;

import cn.LTCraft.core.Main;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftItem;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemRotating implements Runnable {
    private final List<Item> itemEntities = new ArrayList<>();
    private final static int speed = 1;
    private int angle;
    private int progress = 0;
    private final double radius = 1.5;
    private Entity owner = null;
    public ItemRotating(Entity entity){
        owner = entity;
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), this, 1, 1);
    }
    @Override
    public void run() {
        progress += speed;
        synchronized (itemEntities) {
            if (itemEntities.removeIf(Entity::isDead) && itemEntities.size() > 0) {
                angle = Math.max(360 / itemEntities.size(), 1);
                progress = 1;
            }
        }
        for (int i = 1; i <= itemEntities.size(); i++) {
            CraftItem craftItem = (CraftItem)itemEntities.get(i - 1);
            double targetX = Math.cos(((angle * i + progress) % 360) * Math.PI / 180) * radius + owner.getLocation().getX();
            double targetZ = Math.sin(((angle * i + progress) % 360) * Math.PI / 180) * radius + owner.getLocation().getZ();
            craftItem.getHandle().motX = (targetX - craftItem.getLocation().getX());
            craftItem.getHandle().motY = 0;
            craftItem.getHandle().motZ = (targetZ - craftItem.getLocation().getZ());
            craftItem.getHandle().impulse = true;
        }
    }

    /**
     * 添加实体
     * @param item
     */
    public void addEntity(Item item){
        if (itemEntities.contains(item))return;
        List<String> lore = item.getItemStack().getItemMeta().getLore();
        if (!lore.get(lore.size() - 1).startsWith("sign"))return;
        (((CraftEntity)item).getHandle()).setNoGravity(true);
        synchronized (itemEntities) {
            itemEntities.add(item);
        }
        angle = Math.max(360 / itemEntities.size(), 1);
        progress = 1;
        resetPosition();
    }

    /**
     * 重置坐标
     */
    private void resetPosition(){
        for (int i = 1; i <= itemEntities.size(); i++) {
            itemEntities.get(i - 1).teleport(owner.getLocation().add( radius * Math.cos(angle * i * 3.14 / 360),1, radius * Math.sin(angle * i * 3.14 / 360)));
        }
    }
}
