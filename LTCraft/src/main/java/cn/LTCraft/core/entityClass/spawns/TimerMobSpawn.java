package cn.LTCraft.core.entityClass.spawns;

import cn.LTCraft.core.Config;
import cn.LTCraft.core.Main;
import cn.LTCraft.core.entityClass.ClutterItem;
import cn.LTCraft.core.game.more.tickEntity.TickEntity;
import cn.LTCraft.core.task.GlobalRefresh;
import cn.LTCraft.core.utils.GameUtils;
import cn.LTCraft.core.utils.MathUtils;
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

public class TimerMobSpawn extends AbstractMobSpawn {
    /**
     * 悬浮字对象
     */
    protected Hologram hologram = null;
    /**
     * 内部计时器
     */
    protected int timer = 0;
    /**
     * 是否展示悬浮字
     */
    protected boolean showText = true;
    /**
     * 怪物范围
     */
    protected int range = 0;
    /**
     * 冷却时间
     */
    protected int cooling = 0;
    public TimerMobSpawn(String insideName){
        super(insideName);
        Main main = Main.getInstance();
        World world = Bukkit.getWorld(config.getString("world"));
        MythicMob mm = MythicMobs.inst().getMobManager().getMythicMob(mobName);
        Location location;
        if (mm.getConfig().getBoolean("团队"))
            location = new Location(world, config.getDouble("x"), config.getDouble("y") + 1 + (mm.getDrops().size() + 1) * 0.3, config.getDouble("z"));
        else
            location = new Location(world, config.getDouble("x"), config.getDouble("y") + 1 + mm.getDrops().size() * 0.3, config.getDouble("z"));
        cooling = config.getInteger("cooling", 10);
        range = config.getInteger("range", 16);
        showText = config.getBoolean("showText", true);
        if (showText) {
            hologram = HologramsAPI.createHologram(main, location);
            HologramsAPI.registerPlaceholder(main, "LTSpawn:" + insideName + ":colling", 1, () -> {
                if (mobSize >= maxMobs)
                    return "怪物已达最大数量！";
                else
                    return Math.max(0, cooling - timer) + "S刷新！";
            });
            HologramsAPI.registerPlaceholder(main, "LTSpawn:" + insideName + ":mobCount", 1, () -> String.valueOf(mobSize));
            hologram.appendTextLine("§a=========[" + insideName + "§a]=========");
            hologram.appendTextLine("§6名字:§3" + mobName + "§d还有:LTSpawn:" + insideName + ":colling");
            hologram.appendTextLine("§e当前怪物数量:LTSpawn:" + insideName + ":mobCount/" + maxMobs);
            if (mm.getConfig().getBoolean("团队")) hologram.appendTextLine("§c这个怪物需要多人配合击`杀！");
            if (mm.getConfig().getInteger("护甲", 0) > 0) hologram.appendTextLine("§6护甲:" + mm.getConfig().getInteger("护甲", 0) + "(" + Utils.formatNumber(MathUtils.getInjuryFreePercentage(mm.getConfig().getInteger("护甲", 0)) * 100, 2) + "%)");
            for (int i = 0; i < mm.getDrops().size(); i++) {
                String drop = mm.getDrops().get(i);
                String[] drops = MythicLineConfig.unparseBlock(drop).split(" ");
                if (drops[0].startsWith("ltitem")) {
                    hologram.appendTextLine("§e掉落:" + ClutterItem.spawnClutterItem(drops[1], ClutterItem.ItemSource.LTCraft) + "×" + (drops.length >= 3 ? drops[2] : 1) + " " + Utils.formatNumber(Double.parseDouble(drops.length >= 4 ? drops[3] : "1") * 100) + "%");
                } else if (drops[0].startsWith("goldCoinsDrop")) {
                    hologram.appendTextLine("§e掉落:" + drops[1].split(":")[0] + "×" + (drops.length >= 3 ? drops[2] : 1) + " " + Utils.formatNumber(Double.parseDouble(drops.length >= 4 ? drops[3] : "1") * 100) + "%");
                } else if (drops[0].startsWith("goldCoins")) {
                    hologram.appendTextLine("§e掉落:金币×" + (drops.length >= 2 ? drops[1] : 1) + " " + Utils.formatNumber(Double.parseDouble(drops.length >= 3 ? drops[2] : "1") * 100) + "%");
                } else if (drops[0].startsWith("skillapi-exp")) {
                    hologram.appendTextLine("§e掉落:经验×" + (drops.length >= 2 ? drops[1] : 1) + " " + Utils.formatNumber(Double.parseDouble(drops.length >= 3 ? drops[2] : "1") * 100) + "%");
                } else if (drops[0].startsWith("ParticipateInSAPExp")) {
                    hologram.appendTextLine("§e参与掉落:经验×" + drops[1].split("%")[0] + " " + Utils.formatNumber(Double.parseDouble(drops[1].split("%").length > 1 ? drops[1].split("%")[1] : "1") * 100) + "%");
                } else if (drops[0].startsWith("ParticipateInDrop")) {
                    hologram.appendTextLine("§e参与掉落:" + drops[1].split("%")[0] + "×" + (drops.length >= 3 ? drops[2] : 1) + " " + Utils.formatNumber(Double.parseDouble(drops[1].split("%").length > 1 ? drops[1].split("%")[1] : "1") * 100) + "%");
                } else if (drops[0].startsWith("ParticipateInGoldCoinsDrop")) {
                    hologram.appendTextLine("§e参与掉落:金币×" + drops[1].split("%")[0] + " " + Utils.formatNumber(Double.parseDouble(drops[1].split("%").length > 1 ? drops[1].split("%")[1] : "1") * 100) + "%");
                } else if (drops[0].startsWith("PseudorandomDrop")) {
                    hologram.appendTextLine("§e伪随机掉落:" + drops[1].split("%")[0] + "×" + (drops.length >= 3 ? drops[2] : 1) + " " + Utils.formatNumber(Double.parseDouble(drops[1].split("%").length > 1 ? drops[1].split("%")[1] : "1") * 100) + "%");
                } else if (drops.length >= 3) {
                    hologram.appendTextLine("§e掉落:" + drops[0] + "×" + drops[1] + " " + Utils.formatNumber(Double.parseDouble(drops[2]) * 100) + "%");
                }
            }
            hologram.setAllowPlaceholders(true);
        }
    }
    public boolean doTick(long tick){
        if (!super.doTick(tick))return false;
        int lastSize = mobSize;
        int lastTimer = timer;
        if (mobSize < maxMobs) {
            if (
                timer++ >= cooling
                &&
                location.getWorld().getPlayers().stream().anyMatch(player -> player.getLocation().distance(location) <= range * 2)
            ) {
                if (spawnMob()) {
                    timer = 0;
                }
            }
        }
        if ((lastSize != mobSize || lastTimer != timer) && hologram != null) {
            ((CraftHologram) hologram).refreshSingleLines();///????
        }
        return true;
    }
    public void close(){
        super.close();
        if (hologram != null){
            hologram.setAllowPlaceholders(false);
            hologram.delete();
        }
        HologramsAPI.unregisterPlaceholder(Main.getInstance(), "LTSpawn:" + insideName + ":colling");
        HologramsAPI.unregisterPlaceholder(Main.getInstance(), "LTSpawn:" + insideName + ":mobCount");
    }

    @Override
    public YamlConfiguration getYamlConfig() {
        return Config.getInstance().getTimerSpawnYaml();
    }

    @Override
    public Location getAddLocation(Location location) {
        return location.add(0, 2, 0);
    }
}
