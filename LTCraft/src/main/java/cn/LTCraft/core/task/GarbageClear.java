package cn.LTCraft.core.task;

import cn.LTCraft.core.entityClass.ClutterItem;
import cn.LTCraft.core.game.more.tickEntity.TickEntity;
import cn.LTCraft.core.utils.EntityUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GarbageClear implements TickEntity {
    private static GarbageClear instance = null;
    public synchronized static GarbageClear getInstance(){
        if (instance == null){
            instance = new GarbageClear();
        }
        return instance;
    }
    public static final List<ClutterItem> skips = new ArrayList<>();
    private int remainingTime = 5 * 60;
    private GarbageClear(){
        instance = this;
        GlobalRefresh.addTickEntity(this);
    }
    public boolean doTick(long tick) {
        remainingTime--;
        if (remainingTime == 60 || remainingTime == 20 || remainingTime == 10 || remainingTime == 5){
            if (remainingTime == 60){
                int dropCount = getDropCount();
                if (dropCount < 30){
                    remainingTime = 3 * 60;
                    return true;
                }
            }
            Bukkit.broadcastMessage("§l§e[扫地娘]§c将在§d" + remainingTime + "§c秒后清理所有垃圾了！");
        }
        if (remainingTime == 0){
            int drop = clearDrop();
            Bukkit.broadcastMessage("§l§e[扫地娘]§c本次清理了§d" + drop + "§c个掉落物！");
            remainingTime = 5 * 60;
        }
        return true;
    }

    @Override
    public int getTickRate() {
        return 20;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public int clearDrop() {
        return clearDrop(false);
    }
    public int clearDrop(boolean force){
        int count = 0;
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof Item){
                    Item item = (Item) entity;
                        if (skips.stream().anyMatch(clutterItem -> clutterItem.isSimilar(item.getItemStack())))
                            continue;
                        int age;
                        if (force)
                            age = -1;
                        else
                            age = EntityUtils.getItemAge(item);
                        if (age == -1 || age > 20 * 20){
                            entity.remove();
                            count++;
                        }
                }
            }
        }
        return count;
    }
    public int getDropCount(){
        int count = 0;
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof Item){
                    int age = EntityUtils.getItemAge(((Item) entity));
                    if (age == -1 || age > 20 * 20){
                        count++;
                    }
                }
            }
        }
        return count;
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
