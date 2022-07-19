package cn.LTCraft.core.entityClass.spawns;

import cn.LTCraft.core.Config;
import cn.LTCraft.core.Main;
import cn.LTCraft.core.entityClass.ClutterItem;
import cn.LTCraft.core.game.more.tickEntity.TickEntity;
import cn.LTCraft.core.task.GlobalRefresh;
import cn.LTCraft.core.utils.GameUtils;
import cn.LTCraft.core.utils.Utils;
import cn.LTCraft.core.utils.WorldUtils;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.object.CraftHologram;
import io.lumine.utils.config.file.YamlConfiguration;
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

import java.util.List;

/**
 * Created by Angel、 on 2022/7/15 23:37
 */
public abstract class AbstractMobSpawn implements TickEntity {
    protected final String insideName;
    protected final Location location;
    protected final AbstractLocation abstractLocation;
    protected final String mobName;
    protected int maxMobs;
    protected final Location[] locations;
    protected ActiveMob[] mobs;
    protected int mobSize = 0;
    protected boolean closed = false;
    protected final MythicConfig config;
    protected int spawnRange = 1;
    public AbstractMobSpawn(String insideName){
        this.insideName = insideName;
        config = new MythicConfig(insideName, getYamlConfig());
        World world = Bukkit.getWorld(config.getString("world"));
        mobName = config.getString("mobName");
        spawnRange = config.getInteger("spawnRange", 1);
        location = new Location(world, config.getDouble("x"), config.getDouble("y") + 2, config.getDouble("z"));
        abstractLocation = new AbstractLocation(new BukkitWorld(world), config.getDouble("x"), config.getDouble("y") + 2, config.getDouble("z"));
        maxMobs = config.getInteger("maxMobs", 3);
        mobs = new ActiveMob[maxMobs];
        List<String> locations = config.getStringList("locations");
        this.locations = new Location[locations.size()];
        for (int i = 0; i < locations.size(); i++) {
            this.locations[i] = GameUtils.spawnLocation(locations.get(i));
        }
        GlobalRefresh.addTickEntity(this);
    }
    public boolean doTick(long tick){
        if (closed)return false;
        checkMobs();
        return true;
    }
    public void close(){
        closed = true;
        for (ActiveMob mob : mobs) {
            if (mob != null) {
                mob.setDead();
                mob.getEntity().remove();
            }
        }
        mobs = null;
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

    /**
     * 产出怪物
     * @return 如果成功
     */
    public boolean spawnMob(){
        if (mobSize < maxMobs) {
            int index = getIndex();
            if (index == -1) {
                Bukkit.getLogger().warning("无法产出怪物，未知错误，打印堆栈用于分析！");
                try {
                    throw new Exception();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
            Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                ActiveMob am;
                if (index < locations.length) {
                    am = MythicMobs.inst().getMobManager().spawnMob(this.mobName, locations[index]);
                } else {
                    am = MythicMobs.inst().getMobManager().spawnMob(this.mobName, WorldUtils.rangeLocation(location, spawnRange));
                }
                mobs[index] = am;
            });
            return true;
        }
        return false;
    }

    public String getInsideName() {
        return insideName;
    }

    public Location getLocation() {
        return location;
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

    public ActiveMob[] getMobs() {
        return mobs;
    }

    public MythicConfig getConfig() {
        return config;
    }

    public int getMaxMobs() {
        return maxMobs;
    }

    public int getMobSize() {
        checkMobs();
        return mobSize;
    }

    @Override
    public int getTickRate() {
        return 20;
    }

    @Override
    public boolean isAsync() {
        return true;
    }
    public abstract YamlConfiguration getYamlConfig();
}
