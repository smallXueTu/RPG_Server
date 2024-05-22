package cn.LTCraft.core.entityClass.spawns;

import cn.LTCraft.core.Main;
import cn.LTCraft.core.game.more.tickEntity.TickEntity;
import cn.LTCraft.core.task.GlobalRefresh;
import cn.LTCraft.core.utils.GameUtils;
import cn.LTCraft.core.utils.WorldUtils;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import io.lumine.utils.config.file.YamlConfiguration;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.AbstractLocation;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.drops.DropMetadata;
import io.lumine.xikage.mythicmobs.drops.DropTable;
import io.lumine.xikage.mythicmobs.drops.LootBag;
import io.lumine.xikage.mythicmobs.io.MythicConfig;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.mobs.GenericCaster;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.swing.text.html.parser.Entity;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by Angel、 on 2022/7/15 23:37
 */
public abstract class AbstractMobSpawn implements TickEntity {
    protected final String insideName;
    protected final Location location;
    protected final Location originLocation;
    protected final AbstractLocation abstractLocation;
    protected final String mobName;
    protected final List<String> mobNames;
    protected final String dropTableName;
    protected int maxMobs;
    protected final Location[] locations;
    protected ActiveMob[] mobs;
    protected int mobSize = 0;
    protected boolean closed = false;
    protected final MythicConfig config;
    protected int spawnRange = 1;
    protected int lastIndex = 0;
    /**
     * 这个刷怪点的key
     */
    protected final String key;
    public AbstractMobSpawn(String insideName){
        this.insideName = insideName;
        config = new MythicConfig(insideName, getYamlConfig());
        World world = Bukkit.getWorld(config.getString("world"));
        mobName = config.getString("mobName");
        mobNames = config.getStringList("mobNames");
        dropTableName = config.getString("dropTable");
        spawnRange = config.getInteger("spawnRange", 1);
        location = getAddLocation(new Location(world, config.getDouble("x"), config.getDouble("y"), config.getDouble("z")));
        originLocation = new Location(world, config.getDouble("x"), config.getDouble("y"), config.getDouble("z"));
        key = GameUtils.spawnLocationString(getOriginLocation());
        abstractLocation = BukkitAdapter.adapt(location);
        maxMobs = config.getInteger("maxMobs", 3);
        mobs = new ActiveMob[maxMobs];
        List<String> locations = config.getStringList("locations");
        this.locations = new Location[locations.size()];
        for (int i = 0; i < locations.size(); i++) {
            this.locations[i] = GameUtils.spawnLocation(locations.get(i));
        }
        GlobalRefresh.addTickEntity(this);
        lastIndex = maxMobs;
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
                continue;
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
    public LootBag getDropTable(Player player){
        if (Objects.isNull(dropTableName))return null;
        Optional<DropTable> dropTable = MythicMobs.inst().getDropManager().getDropTable(dropTableName);
//        GenericCaster genericCaster = new GenericCaster(BukkitAdapter.adapt(player));
        return dropTable.map(table -> table.generate(new DropMetadata(null, BukkitAdapter.adapt(player)))).orElse(null);
    }
    /**
     * 产出怪物
     * 推荐同一游戏时刻只调用一次！
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
            mobSize++;//TODO 修复逻辑
            if (Bukkit.isPrimaryThread()){
                ActiveMob am;
                if (index < locations.length) {
                    am = MythicMobs.inst().getMobManager().spawnMob(this.getMobName(index), locations[index]);
                } else {
                    am = MythicMobs.inst().getMobManager().spawnMob(this.getMobName(index), WorldUtils.rangeLocation(location, spawnRange));
                }
                if (am == null){
                    am = MythicMobs.inst().getMobManager().spawnMob("AngrySludge", locations[index]);
                }
                mobs[index] = am;
            }else {
                Bukkit.getScheduler().runTask(Main.getInstance(),() -> {
                    ActiveMob am;
                    if (index < locations.length) {
                        am = MythicMobs.inst().getMobManager().spawnMob(this.getMobName(index), locations[index]);
                    } else {
                        am = MythicMobs.inst().getMobManager().spawnMob(this.getMobName(index), WorldUtils.rangeLocation(location, spawnRange));
                    }
                    if (am == null){
                        am = MythicMobs.inst().getMobManager().spawnMob("AngrySludge", locations[index]);
                    }
                    mobs[index] = am;
                });
            }
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

    public Location getOriginLocation() {
        return originLocation;
    }

    public int getIndex(){
        int loopCount = 0;
        for (int i = lastIndex - 1; i >= 0; i--) {
            ActiveMob mob = mobs[i];
            if (mob == null || mob.isDead() || mob.getEntity().getBukkitEntity().isDead()){
                lastIndex = i;
                if (lastIndex <= 0)lastIndex = maxMobs;
                return i;
            }
            if (i == 0) i = maxMobs;
            if (loopCount++ > maxMobs)break;
        }
        return -1;
    }

    /**
     * 获取索引对应的怪物名
     */
    public String getMobName(int index){
        if (mobNames.size() <= 0)return mobName;
        return  mobNames.get(index % mobNames.size());
    }

    /**
     * 获取所有已刷出怪物
     */
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

    public String getKey() {
        return key;
    }

    @Override
    public boolean isAsync() {
        return true;
    }
    public abstract YamlConfiguration getYamlConfig();
    public abstract Location getAddLocation(Location location);
}
