package cn.LTCraft.core.game.skills;

import cn.LTCraft.core.entityClass.PlayerConfig;
import cn.LTCraft.core.entityClass.spawns.ChestMobSpawn;
import cn.LTCraft.core.game.ChestSpawnManager;
import cn.LTCraft.core.utils.GameUtils;
import com.gmail.berndivader.mythicmobsext.Main;
import net.minecraft.server.v1_12_R1.EntityEnderSignal;
import net.minecraft.server.v1_12_R1.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.EnderSignal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by Angel、 on 2024/7/11 下午11:43
 */
public class FindChest extends BaseSkill{
    private Player owner;
    public FindChest(Player player, int level, int awakenLevel, boolean awaken){
        super(level, awakenLevel, awaken);
        owner = player;
    }
    @Override
    public boolean cast(Entity target) {
        ItemStack itemInHand = owner.getItemInHand();
        if (itemInHand.getType() == Material.EYE_OF_ENDER) {
            Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> {
                EnderSignal enderSignal = null;
                World world = owner.getWorld();
                List<Entity> nearbyEntities = owner.getNearbyEntities(2, 2, 2);
                for (Entity entity : nearbyEntities) {
                    // 检查实体是否为末影之眼
                    if (entity.getType() == EntityType.ENDER_SIGNAL) {
                        enderSignal = (EnderSignal) entity;
                        break;
                    }
                }
                if (enderSignal == null) {
                    enderSignal = world.spawn(owner.getEyeLocation(), EnderSignal.class);
                }
                if (enderSignal != null) {
                    itemInHand.setAmount(itemInHand.getAmount() - 1);
                    ChestSpawnManager chestSpawnManager = ChestSpawnManager.getInstance();
                    Map<String, ChestMobSpawn> spawns = chestSpawnManager.getSpawns();
                    PlayerConfig playerConfig = PlayerConfig.getPlayerConfig(owner);
                    List<String> opened = (List<String>) playerConfig.getTempConfig().getList("已打开箱子", new ArrayList<>());
                    // 删除非本世界
                    opened.removeIf(location -> !location.endsWith(owner.getWorld().getName()));
                    final Location center = opened.isEmpty() ? owner.getLocation() : GameUtils.spawnLocation(opened.get(opened.size() - 1));
                    List<Map.Entry<String, ChestMobSpawn>> collect = spawns.entrySet().stream().filter(new Predicate<Map.Entry<String, ChestMobSpawn>>() {
                        @Override
                        public boolean test(Map.Entry<String, ChestMobSpawn> stringChestMobSpawnEntry) {
                            return stringChestMobSpawnEntry.getValue().getOriginLocation().getWorld().equals(world) && !opened.contains(stringChestMobSpawnEntry.getKey());
                        }
                    }).sorted(new Comparator<Map.Entry<String, ChestMobSpawn>>() {
                        @Override
                        public int compare(Map.Entry<String, ChestMobSpawn> o1, Map.Entry<String, ChestMobSpawn> o2) {
                            // y坐标优先级设小
                            Location o1L = o1.getValue().getOriginLocation();
                            Location o2L = o2.getValue().getOriginLocation();
                            return Double.compare(center.distance(o1L), center.distance(o2L));
                        }
                    }).collect(Collectors.toList());
                    if (collect.size() > 0) {
                        enderSignal.setTargetLocation(collect.get(0).getValue().getOriginLocation());
                    } else {
                        enderSignal.setTargetLocation(enderSignal.getTargetLocation());
                    }
                    enderSignal.setDropItem(false);
                }
            }, 0);
        }
        return true;
    }

    public static boolean needToLearn(){
        return false;
    }
}
