package cn.LTCraft.core.listener;

import cn.LTCraft.core.Main;
import cn.LTCraft.core.entityClass.ClutterItem;
import cn.LTCraft.core.game.Game;
import cn.LTCraft.core.game.TargetOnlyMobsManager;
import cn.LTCraft.core.task.PlayerClass;
import cn.LTCraft.core.other.Temp;
import cn.LTCraft.core.utils.EntityUtils;
import cn.LTCraft.core.utils.PlayerUtils;
import com.google.common.collect.Lists;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class EntityListener implements Listener {
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event){
        Entity entity = event.getEntity();
        if (MythicMobs.inst().getMobManager().isActiveMob(entity.getUniqueId())){
            ActiveMob am = MythicMobs.inst().getMobManager().getMythicMobInstance(entity);
//            System.out.println(am.getDisplayName() + ":" + event.getCause());
        }
    }
    @EventHandler
    public void onDamageByEntityEvent(EntityDamageByEntityEvent event){
        Entity entity = event.getEntity();
        if (entity instanceof ArmorStand){
            Entity damager = event.getDamager();
            if (damager instanceof Player){
                if (!damager.hasPermission("LTCraft.damage.armorStand."+entity.getCustomName()) &&
                        !damager.hasPermission("LTCraft.damage.armorStand.*") &&
                        !damager.hasPermission("LTCraft.damage.*")){
                    event.setCancelled(true);
                }
            }else{
                event.setCancelled(true);
            }
        }else if(entity instanceof ItemFrame){
            Entity damager = event.getDamager();
            if (damager instanceof Player){
                if (!damager.hasPermission("LTCraft.damage.ItemFrame."+entity.getCustomName()) &&
                        !damager.hasPermission("LTCraft.damage.ItemFrame.*") &&
                        !damager.hasPermission("LTCraft.damage.*")){
                    event.setCancelled(true);
                }
            }else{
                event.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event){
        if (event.getEntity() instanceof Item){
            if(Temp.playerDropItem.containsKey(event.getEntity())){
                Player player = Bukkit.getPlayerExact(Temp.playerDropItem.get(event.getEntity()));
                if (Temp.dropCount.containsKey(player)){
                    Temp.dropCount.put(player, Temp.dropCount.get(player) - 1 );
                }
                Temp.playerDropItem.remove(event.getEntity());
            }
            Temp.discardOnly.remove((Item) event.getEntity());
        }
    }
    @EventHandler(
        priority = EventPriority.LOWEST
    )
    public void onEntityRegainHealth(EntityRegainHealthEvent event){
        if (event.getEntity() instanceof Player){
            if (PlayerUtils.getClass(((Player) event.getEntity())) == PlayerClass.MINISTER){
                event.setAmount(event.getAmount() * 1.5);
            }
        }
        if (Temp.injured.containsKey(event.getEntity())){
            event.setAmount(event.getAmount() / 2);
        }
    }
    @EventHandler
    public void onExplosionPrime(ExplosionPrimeEvent event){
        if (!Game.resourcesWorlds.contains(event.getEntity().getWorld().getName())) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event){
        if ((event.getEntityType() == EntityType.WITHER) && !Game.resourcesWorlds.contains(event.getEntity().getWorld().getName())) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onTargetLivingEntity(EntityTargetLivingEntityEvent event){
        Entity entity = event.getEntity();
        ActiveMob mythicMob = EntityUtils.getMythicMob(entity);
        if (mythicMob != null && event.getTarget() != null){
            if (TargetOnlyMobsManager.targetOnlyMobs.containsKey(mythicMob) && !event.getTarget().equals(TargetOnlyMobsManager.targetOnlyMobs.get(mythicMob))) {
                event.setCancelled(true);
            }
        }
    }
    private static Map<String[], String> explosiveFormula = new HashMap<String[], String>(){
        {
            put(new String[]{"EYE_OF_ENDER", "通用要素", "DRAGONS_BREATH"}, "通用不稳定要素");
        }
    };
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event){
        if (event.getEntity() instanceof TNTPrimed) {
            Entity entity = event.getEntity();
            Location location = entity.getLocation();
            World world = entity.getWorld();
            Collection<Entity> nearbyEntities = world.getNearbyEntities(location, 3, 3, 3);
            Set<Map.Entry<String[], String>> entries = explosiveFormula.entrySet();
            List<Entity> killEntities = new ArrayList<>();
            for (Map.Entry<String[], String> entry : entries) {
                String[] key = entry.getKey();
                List<ClutterItem> clutterItems = Arrays.stream(key).map(ClutterItem::spawnClutterItem).collect(Collectors.toList());
                for (Entity nearbyEntity : nearbyEntities) {
                    if (nearbyEntity instanceof Item){
                        ItemStack itemStack = ((Item) nearbyEntity).getItemStack();
                        if (clutterItems.removeIf(clutterItem -> clutterItem.isSimilar(itemStack))) {
                            killEntities.add(nearbyEntity);
                        }
                    }
                }
                if (clutterItems.size() <= 0){
                    ItemStack generate = ClutterItem.spawnClutterItem(entry.getValue()).generate();
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> world.dropItem(location, generate));
                    killEntities.forEach(Entity::remove);
                }
                killEntities.clear();
            }
        }
    }
}
