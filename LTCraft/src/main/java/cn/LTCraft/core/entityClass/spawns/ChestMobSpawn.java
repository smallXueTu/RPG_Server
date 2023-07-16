package cn.LTCraft.core.entityClass.spawns;

import cn.LTCraft.core.Config;
import cn.LTCraft.core.Main;
import cn.LTCraft.core.entityClass.PlayerConfig;
import cn.LTCraft.core.task.GlobalRefresh;
import cn.LTCraft.core.utils.GameUtils;
import cn.LTCraft.core.utils.PlayerUtils;
import cn.ltcraft.item.utils.Utils;
import io.lumine.utils.config.file.YamlConfiguration;
import io.lumine.xikage.mythicmobs.adapters.AbstractItemStack;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.drops.DropMetadata;
import io.lumine.xikage.mythicmobs.drops.IItemDrop;
import io.lumine.xikage.mythicmobs.drops.LootBag;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Created by Angel、 on 2022/7/15 23:35
 */
public class ChestMobSpawn extends AbstractMobSpawn {
    protected final Map<String, Long> tryOpenTimer = new HashMap<>();
    protected final String quality;
    //成功打开动作
    protected final String successfullyOpenedAction;
    //尝试打开动作
    protected final String attemptToOpenAction;
    //无法打开动作
    protected final String unableToOpenAction;
    //已经打开动作
    protected final String actionAlreadyOpened;
    public ChestMobSpawn(String insideName) {
        super(insideName);
        quality = config.getString("品质", "普通");
        successfullyOpenedAction = config.getString("成功打开动作", "none");
        attemptToOpenAction = config.getString("尝试打开动作", "message:§c箱子的守卫者出来了，在120s内将他们一网打尽！即可拿走战利品！");
        unableToOpenAction = config.getString("无法打开动作", "message:§c你必须清理掉所有的战利品守卫者才能打开它！");
        actionAlreadyOpened = config.getString("已经打开动作", "message:§c这个箱子你已经打开过了！");
    }

    public Map<String, Long> getTryOpenTimer() {
        return tryOpenTimer;
    }

    public String getUnableToOpenAction() {
        return unableToOpenAction;
    }

    public String getAttemptToOpenAction() {
        return attemptToOpenAction;
    }

    public String getActionAlreadyOpened() {
        return actionAlreadyOpened;
    }

    public String getSuccessfullyOpenedAction() {
        return successfullyOpenedAction;
    }

    @Override
    public YamlConfiguration getYamlConfig() {
        return Config.getInstance().getChestSpawnYaml();
    }

    @Override
    public Location getAddLocation(Location location) {
        return location.add(0.5, 1, 0.5);
    }

    public String getQuality() {
        return quality;
    }

    /**
     * 玩家尝试打开箱子
     */
    public boolean attemptToOpen(Player player, Block block){
        PlayerConfig playerConfig = PlayerConfig.getPlayerConfig(player);
        YamlConfiguration tempConfig = playerConfig.getTempConfig();
        List<String> openedChest = (List<String>) tempConfig.getList("已打开箱子", new ArrayList<>());
        if (openedChest.contains(key)) {//已经打开过不允许再次打开
            GameUtils.executeAction(player, getActionAlreadyOpened());
            return false;
        }
        Long aLong = getTryOpenTimer().get(player.getName());
        if (aLong == null || aLong < System.currentTimeMillis()){//玩家未点击过或者点击已经过期
            GlobalRefresh.scheduleTaskRuns(this::spawnMob, 1L, getMaxMobs(), true);
            GameUtils.executeAction(player, getAttemptToOpenAction());
            getTryOpenTimer().put(player.getName(), System.currentTimeMillis() + 120 * 1000);
            return false;
        }else {
            if (getMobSize() > 0){
                GameUtils.executeAction(player, getUnableToOpenAction());
                return false;
            }else {
                openedChest.add(key);
                tempConfig.set("已打开箱子", openedChest);
                LootBag dropTable = getDropTable(player);
                Location blockLocation = block.getLocation();
                Location location = GameUtils.dropNextToTheBlock(blockLocation, player.getLocation());
                ItemStack[] stacks = dropTable.getDrops().stream().map(drop -> {
                    if (drop instanceof IItemDrop) {
                        IItemDrop iItemDrop = (IItemDrop) drop;
                        AbstractItemStack itemDropDrop = iItemDrop.getDrop(new DropMetadata(null, BukkitAdapter.adapt(player)));
                        return BukkitAdapter.adapt(itemDropDrop);
                    }
                    return null;
                }).filter(Objects::nonNull).toArray(ItemStack[]::new);
                PlayerUtils.dropItem(player, location, stacks);
                GameUtils.sendBlockActionPacket(player, block, new int[]{1, 1});
                blockLocation.getWorld().playSound(blockLocation, Sound.BLOCK_CHEST_OPEN, 0.5f, 1);
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                    blockLocation.getWorld().playSound(blockLocation, Sound.BLOCK_CHEST_CLOSE, 0.5f, 1);
                    GameUtils.sendBlockActionPacket(player, block, new int[]{1, 0});
                }, 20);
                GameUtils.executeAction(player, getSuccessfullyOpenedAction());
                return true;
            }
        }
    }
}
