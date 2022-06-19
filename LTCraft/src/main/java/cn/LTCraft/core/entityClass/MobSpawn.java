package cn.LTCraft.core.entityClass;

import cn.LTCraft.core.Config;
import cn.LTCraft.core.Main;
import cn.LTCraft.core.game.more.tickEntity.TickEntity;
import cn.LTCraft.core.task.GlobalRefresh;
import cn.LTCraft.core.utils.GameUtils;
import cn.LTCraft.core.utils.MathUtils;
import cn.LTCraft.core.utils.Utils;
import cn.LTCraft.core.utils.WorldUtils;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.object.CraftHologram;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.AbstractLocation;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitWorld;
import io.lumine.xikage.mythicmobs.io.MythicConfig;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MobSpawn implements TickEntity {
    private final Hologram hologram;
    private final String insideName;
    private final Location location;
    private final AbstractLocation abstractLocation;
    private int timer = 0;
    private int range = 0;
    private int cooling = 0;
    private int spawnRange = 1;
    private final String mobName;
    private int maxMobs = 0;
    private Location[] locations;
    private ActiveMob[] mobs;
    private int mobSize = 0;
    private boolean closed = false;
    public MobSpawn(String insideName){
        this.insideName = insideName;
        Main main = Main.getInstance();
        MythicConfig config = new MythicConfig(insideName, Config.getInstance().getSpawnYaml());
        World world = Bukkit.getWorld(config.getString("world"));
        mobName = config.getString("mobName");
        spawnRange = config.getInteger("spawnRange", 1);
        MythicMob mm = MythicMobs.inst().getMobManager().getMythicMob(mobName);
        if (mm.getConfig().getBoolean("团队"))
            this.location = new Location(world, config.getDouble("x"), config.getDouble("y") + 1 + (mm.getDrops().size() + 1) * 0.3, config.getDouble("z"));
        else
            this.location = new Location(world, config.getDouble("x"), config.getDouble("y") + 1 + mm.getDrops().size() * 0.3, config.getDouble("z"));
        this.abstractLocation = new AbstractLocation(new BukkitWorld(world), config.getDouble("x"), config.getDouble("y") + 2, config.getDouble("z"));
        cooling = config.getInteger("cooling", 10);
        maxMobs = config.getInteger("maxMobs", 3);
        mobs = new ActiveMob[maxMobs];
        range = config.getInteger("range", 16);
        List<String> locations = config.getStringList("locations");
        this.locations = new Location[locations.size()];
        for (int i = 0; i < locations.size(); i++) {
            this.locations[i] = GameUtils.spawnLocation(locations.get(i));
        }
        hologram = HologramsAPI.createHologram(main, location);
        HologramsAPI.registerPlaceholder(main, "LTSpawn:" + insideName + ":colling", 1, () -> {
            if (mobSize >= maxMobs)
                return "怪物已达最大数量！";
            else
                return Math.max(0, cooling - timer) +"S刷新！";
        });
        HologramsAPI.registerPlaceholder(main, "LTSpawn:" + insideName + ":mobCount", 1, () -> String.valueOf(mobSize));
        hologram.appendTextLine("§a=========[" + insideName + "§a]=========");
        hologram.appendTextLine("§6名字:§3"+ mobName +"§d还有:LTSpawn:"+insideName+":colling");
        hologram.appendTextLine("§e当前怪物数量:LTSpawn:"+insideName+":mobCount/" + maxMobs);
        if (mm.getConfig().getBoolean("团队"))hologram.appendTextLine("§c这个怪物需要多人配合击杀！");
        for (int i = 0; i < mm.getDrops().size(); i++) {
            String drop = mm.getDrops().get(i);
            String[] drops = MythicLineConfig.unparseBlock(drop).split(" ");
            if (drops[0].startsWith("ltitem")){
                hologram.appendTextLine("§e掉落:" + ClutterItem.spawnClutterItem(drops[1], ClutterItem.ItemSource.LTCraft) + "×" + (drops.length >= 3?drops[2]:1) + " " + Utils.formatNumber(Double.parseDouble(drops.length >= 4?drops[3]:"1") * 100) + "%");
            }else if (drops[0].startsWith("goldCoinsDrop")){
                hologram.appendTextLine("§e掉落:" + drops[1].split(":")[0] + "×" + (drops.length >= 3?drops[2]:1) + " " + Utils.formatNumber(Double.parseDouble(drops.length >= 4?drops[3]:"1") * 100) + "%");
            }else if (drops[0].startsWith("goldCoins")){
                hologram.appendTextLine("§e掉落:金币×" + (drops.length >= 2?drops[1]:1) + " " + Utils.formatNumber(Double.parseDouble(drops.length >= 3?drops[2]:"1") * 100) + "%");
            }else if (drops[0].startsWith("skillapi-exp")){
                hologram.appendTextLine("§e掉落:经验×" + (drops.length >= 2?drops[1]:1) + " " + Utils.formatNumber(Double.parseDouble(drops.length >= 3?drops[2]:"1") * 100) + "%");
            }else if (drops[0].startsWith("ParticipateInSAPExp")){
                hologram.appendTextLine("§e参与掉落:经验×" + drops[1].split("%")[0] + " " + Utils.formatNumber(Double.parseDouble(drops[1].split("%").length > 1?drops[1].split("%")[1]:"1") * 100) + "%");
            }else if (drops[0].startsWith("ParticipateInDrop")){
                hologram.appendTextLine("§e参与掉落:" + drops[1].split("%")[0] + "×" + (drops.length >= 3?drops[2]:1) + " " + Utils.formatNumber(Double.parseDouble(drops[1].split("%").length > 1?drops[1].split("%")[1]:"1") * 100) + "%");
            }else if (drops[0].startsWith("ParticipateInGoldCoinsDrop")){
                hologram.appendTextLine("§e参与掉落:金币×" + drops[1].split("%")[0] + " " + Utils.formatNumber(Double.parseDouble(drops[1].split("%").length > 1?drops[1].split("%")[1]:"1") * 100) + "%");
            }else if (drops[0].startsWith("PseudorandomDrop")){
                hologram.appendTextLine("§e伪随机掉落:" + drops[1].split("%")[0] + "×" + (drops.length >= 3?drops[2]:1) + " " + Utils.formatNumber(Double.parseDouble(drops[1].split("%").length > 1?drops[1].split("%")[1]:"1") * 100) + "%");
            }else if (drops.length >= 3) {
                hologram.appendTextLine("§e掉落:" + drops[0] + "×" + drops[1] + " " + Utils.formatNumber(Double.parseDouble(drops[2]) * 100) + "%");
            }
        }
        hologram.setAllowPlaceholders(true);
        GlobalRefresh.addTickEntity(this);
    }
    public boolean doTick(long tick){
        if (closed)return false;
        int lastSize = mobSize;
        int lastTimer = timer;
        if (mobSize < maxMobs) {
            if (
                timer++ >= cooling
                &&
                location.getWorld().getPlayers().stream().anyMatch(player -> player.getLocation().distance(location) <= range)
            ) {
                int index = getIndex();
                if (index == -1){
                    Bukkit.getLogger().warning("未知错误，打印堆栈用于分析！");
                    try {
                        throw new Exception();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    return false;
                }
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                    ActiveMob am;
                    if (index < locations.length){
                        am = MythicMobs.inst().getMobManager().spawnMob(this.mobName, locations[index]);
                    }else {
                        am = MythicMobs.inst().getMobManager().spawnMob(this.mobName, WorldUtils.rangeLocation(location, spawnRange));
                    }
                    mobs[index] = am;
                });
                timer = 0;
                mobSize++;
            }
        }
        checkMobs();
        if (lastSize != mobSize || lastTimer != timer) {
            ((CraftHologram) hologram).refreshSingleLines();
        }
        return true;
    }
    public void close(){
        closed = true;
        hologram.setAllowPlaceholders(false);
        hologram.delete();
        for (ActiveMob mob : mobs) {
            if (mob != null) {
                mob.setDead();
                mob.getEntity().remove();
            }
        }
        mobs = null;
        HologramsAPI.unregisterPlaceholder(Main.getInstance(), "LTSpawn:" + insideName + ":colling");
        HologramsAPI.unregisterPlaceholder(Main.getInstance(), "LTSpawn:" + insideName + ":mobCount");
    }
    public void checkMobs(){
        for (int i = 0; i < mobs.length; i++) {
            ActiveMob mob = mobs[i];
            if (mob == null)continue;
            if (mob.isDead() || mob.getEntity().isDead() || mob.getEntity().getBukkitEntity().isDead()){
                mobs[i] = null;
                mobSize--;
            }
            if (mob.getLocation().distance(i >= locations.length ? abstractLocation: BukkitAdapter.adapt(locations[i])) > mob.getType().getConfig().getDouble("Options.FollowRange", 16)){
                int finalI = i;
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                    Hologram hologram = HologramsAPI.createHologram(Main.getInstance(), mob.getEntity().getBukkitEntity().getLocation().add(0, 1.5, 0));
                    hologram.appendTextLine("§c怪物超出范围，拉回怪物！");
                    mob.getEntity().teleport(finalI >= locations.length ? abstractLocation: BukkitAdapter.adapt(locations[finalI]));
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), hologram::delete, 40);
                });
            }
        }
    }

    public String getInsideName() {
        return insideName;
    }
    public int getIndex(){
        for (int i = mobs.length - 1; i >= 0; i--) {
            ActiveMob mob = mobs[i];
            if (mob == null || mob.isDead() || mob.getEntity().getBukkitEntity().isDead()){
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public int getTickRate() {
        return 20;
    }
}
