package cn.ltcraft.spawn;

import cn.LTCraft.core.entityClass.ClutterItem;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.placeholder.PlaceholderReplacer;
import com.gmail.filoghost.holographicdisplays.object.CraftHologram;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.AbstractLocation;
import io.lumine.xikage.mythicmobs.adapters.AbstractWorld;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitWorld;
import io.lumine.xikage.mythicmobs.drops.Drop;
import io.lumine.xikage.mythicmobs.drops.droppables.MythicItemDrop;
import io.lumine.xikage.mythicmobs.io.MythicConfig;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Spawn {
    private final LTSpawn ltSpawn;
    private final Hologram hologram;
    private final String insideName;
    private final MythicConfig config;
    private final Location location;
    private final AbstractLocation abstractLocation;
    private int timer = 0;
    private int range = 0;
    private int cooling = 0;
    private String mobName;
    private int maxMobs = 0;
    private List<ActiveMob> mobs = new ArrayList<>();
    public Spawn(String insideName){
        this.insideName = insideName;
        ltSpawn = LTSpawn.getInstance();
        config = new MythicConfig(insideName, ltSpawn.getSpawnConfig());
        World world = Bukkit.getWorld(config.getString("world"));
        mobName = config.getString("mobName");
        MythicMob mm = MythicMobs.inst().getMobManager().getMythicMob(mobName);
        this.location = new Location(world, config.getDouble("x"), config.getDouble("y") + 1 + mm.getDrops().size() * 0.3, config.getDouble("z"));
        this.abstractLocation = new AbstractLocation(new BukkitWorld(world), config.getDouble("x"), config.getDouble("y") + 2, config.getDouble("z"));
        cooling = config.getInteger("cooling", 10);
        maxMobs = config.getInteger("maxMobs", 3);
        range = config.getInteger("range", 16);
        hologram = HologramsAPI.createHologram(LTSpawn.getInstance(), location);
        HologramsAPI.registerPlaceholder(LTSpawn.getInstance(), "LTSpawn:" + insideName + ":colling", 1, () -> {
            if (mobs.size() >= maxMobs)
                return "怪物已达最大数量！";
            else
                return Math.max(0, cooling - timer) +"S刷新！";
        });
        HologramsAPI.registerPlaceholder(LTSpawn.getInstance(), "LTSpawn:" + insideName + ":mobCount", 1, () -> String.valueOf(mobs.size()));
        hologram.appendTextLine("§a=========[" + insideName + "§a]=========");
        hologram.appendTextLine("§6名字:§3"+ mobName +"§d还有:LTSpawn:"+insideName+":colling");
        hologram.appendTextLine("§e当前怪物数量:LTSpawn:"+insideName+":mobCount/" + maxMobs);
        for (int i = 0; i < mm.getDrops().size(); i++) {
            String drop = mm.getDrops().get(i);
            String[] drops = MythicLineConfig.unparseBlock(drop).split(" ");
            if (drops[0].startsWith("LTItem")){
                hologram.appendTextLine("§e掉落:" + ClutterItem.spawnClutterItem(drops[1], ClutterItem.ItemSource.LTCraft) + "×" + (drops.length >= 3?drops[2]:1) + " " +(Integer.parseInt(drops.length >= 4?drops[3]:"1") * 100) + "%");
            }else if (drops[0].startsWith("goldCoinsDrop")){
                hologram.appendTextLine("§e掉落:" + drops[1].split(":")[0] + "×" + (drops.length >= 3?drops[2]:1) + " " +(Integer.parseInt(drops.length >= 4?drops[3]:"1") * 100) + "%");
            }else if (drops[0].startsWith("goldCoins")){
                hologram.appendTextLine("§e掉落:金币×" + (drops.length >= 2?drops[1]:1) + " " +(Integer.parseInt(drops.length >= 3?drops[2]:"1") * 100) + "%");
            }else if (drops[0].startsWith("skillapi-exp")){
                hologram.appendTextLine("§e掉落:经验×" + (drops.length >= 2?drops[1]:1) + " " +(Integer.parseInt(drops.length >= 3?drops[2]:"1") * 100) + "%");
            }else if (drops.length >= 3) {
                hologram.appendTextLine("§e掉落:" + drops[0] + "×" + drops[1] + " " +(Integer.parseInt(drops[2]) * 100) + "%");
            }
        }
        hologram.setAllowPlaceholders(true);
    }
    public void onUpdate(){
        int lastSize = mobs.size();
        int lastTimer = timer;
        if (mobs.size() < maxMobs) {
            if (
                timer++ >= cooling
                &&
                location.getWorld().getPlayers().stream().anyMatch(player -> player.getLocation().distance(location) <= range)
            ) {
                ActiveMob am = MythicMobs.inst().getMobManager().spawnMob(this.mobName, location);
                mobs.add(am);
                timer = 0;
            }
        }
        checkMobs();
        if (lastSize != mobs.size() || lastTimer != timer) {
            ((CraftHologram) hologram).refreshSingleLines();
        }
    }
    public void close(){
        hologram.setAllowPlaceholders(false);
        hologram.delete();
        for (ActiveMob mob : mobs) {
            mob.setDead();
            mob.getEntity().remove();
        }
        mobs.clear();
        HologramsAPI.unregisterPlaceholder(LTSpawn.getInstance(), "LTSpawn:" + insideName + ":colling");
        HologramsAPI.unregisterPlaceholder(LTSpawn.getInstance(), "LTSpawn:" + insideName + ":mobCount");
    }
    public void checkMobs(){
        for (Iterator<ActiveMob> iterator = mobs.iterator();iterator.hasNext();) {
            ActiveMob mob = iterator.next();
            if (mob.isDead() || mob.getEntity().isDead() || mob.getEntity().getBukkitEntity().isDead())iterator.remove();
            if (mob.getLocation().distance(abstractLocation) > mob.getType().getConfig().getDouble("Options.FollowRange", 16)){
                Hologram hologram = HologramsAPI.createHologram(LTSpawn.getInstance(), mob.getEntity().getBukkitEntity().getLocation().add(0, 1.5, 0));
                hologram.appendTextLine("§c怪物超出范围，拉回怪物！");
                mob.getEntity().teleport(abstractLocation);
                Bukkit.getScheduler().scheduleSyncDelayedTask(LTSpawn.getInstance(), hologram::delete, 40);
            }
        }
    }

    public String getInsideName() {
        return insideName;
    }
}
