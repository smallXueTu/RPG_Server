package cn.LTCraft.core.game.more;

import cn.LTCraft.core.Main;
import cn.LTCraft.core.entityClass.ClutterItem;
import cn.LTCraft.core.game.more.tickEntity.TickEntity;
import cn.LTCraft.core.task.GlobalRefresh;
import cn.LTCraft.core.utils.ReflectionHelper;
import cn.LTCraft.core.utils.Utils;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Created by Angel、 on 2022/6/7 8:39
 */
public class DailyLottery implements TickEntity {
    private final Prize[] prizes = {
            new Prize(ClutterItem.spawnClutterItem("宝石:高级永恒水晶", ClutterItem.ItemSource.LTCraft), "高级永恒水晶"),
            new Prize(ClutterItem.spawnClutterItem("材料:中型生命药剂:10", ClutterItem.ItemSource.LTCraft), "中型生命药剂"),
            new Prize(ClutterItem.spawnClutterItem("材料:大瓶生命药剂:3", ClutterItem.ItemSource.LTCraft), "大瓶生命药剂"),
            new Prize(ClutterItem.spawnClutterItem("宝石:医疗水晶", ClutterItem.ItemSource.LTCraft), "医疗水晶"),
            new Prize(ClutterItem.spawnClutterItem("DIAMOND:16", ClutterItem.ItemSource.Minecraft), "钻石"),
            new Prize(ClutterItem.spawnClutterItem("EMERALD:8", ClutterItem.ItemSource.Minecraft), "绿宝石"),
            new Prize(ClutterItem.spawnClutterItem("EXP_BOTTLE:6", ClutterItem.ItemSource.Minecraft), "附魔之瓶"),
            new Prize(ClutterItem.spawnClutterItem("NETHER_STAR", ClutterItem.ItemSource.Minecraft), "下界之星"),
    };
    private final Location location = new Location(Bukkit.getWorld("world"), 101, 22.8, -20);

    private final Map<Integer, Integer> items = new HashMap<>();
    private DataWatcherObject<ItemStack> dataWatcherObject = null;
    private final Map<DataWatcherObject<?>, Object> dataWatcherObjectObjectMap = new HashMap<DataWatcherObject<?>, Object>(){
        {
            Class<Entity> entityClass = Entity.class;
            Class<EntityItem> entityItemClass = EntityItem.class;
            try {
                Field ZF = entityClass.getDeclaredField("Z");
                Field aAF = entityClass.getDeclaredField("aA");
                Field aBF = entityClass.getDeclaredField("aB");
                Field aDF = entityClass.getDeclaredField("aD");
                Field aCF = entityClass.getDeclaredField("aC");
                Field aEF = entityClass.getDeclaredField("aE");
                Field cF = entityItemClass.getDeclaredField("c");
                ZF.setAccessible(true);
                aAF.setAccessible(true);
                aBF.setAccessible(true);
                aCF.setAccessible(true);
                aDF.setAccessible(true);
                aEF.setAccessible(true);
                cF.setAccessible(true);
                put((DataWatcherObject<Byte>)ZF.get(null), (byte)0);
                put((DataWatcherObject<Integer>)aAF.get(null), 300);
                put((DataWatcherObject<String>)aBF.get(null), "");
                put((DataWatcherObject<Boolean>)aCF.get(null), false);
                put((DataWatcherObject<Boolean>)aDF.get(null), false);
                put((DataWatcherObject<Boolean>)aEF.get(null), false);
                dataWatcherObject = (DataWatcherObject<ItemStack>)cF.get(null);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    };
    private int sprayTick = 0;
    private static DailyLottery instance = null;
    public static DailyLottery getInstance() {
        if (instance == null)instance = new DailyLottery();
        return instance;
    }
    private DailyLottery(){
        GlobalRefresh.addTickEntity(this);
    }
    @Override
    public boolean doTick(long tick) {
        if (GlobalRefresh.getTick() % 60 == 0 || sprayTick > 0) {
            for (int i = 0; i < 20; i++) {
                ClutterItem clutterItem = prizes[Utils.getRandom().nextInt(prizes.length)].clutterItem;
                PacketContainer entity = Main.getProtocolManager().createPacket(PacketType.Play.Server.SPAWN_ENTITY);
                int id = ReflectionHelper.getPrivateValue(Entity.class, null, "entityCount");
                ReflectionHelper.setPrivateValue(Entity.class, null, "entityCount", id + 1);
                entity.getIntegers().write(0, id);
                entity.getUUIDs().write(0, MathHelper.a(Utils.getRandom()));
                entity.getDoubles().write(0, location.getX());
                entity.getDoubles().write(1, location.getY());
                entity.getDoubles().write(2, location.getZ());
                entity.getIntegers().write(1, (int)(MathHelper.a((Utils.getRandom().nextInt(41) - 20) / 100d, -3.9, 3.9) * 8000.0));
                entity.getIntegers().write(2, (int)(MathHelper.a(0.5, -3.9, 3.9) * 8000.0));
                entity.getIntegers().write(3, (int)(MathHelper.a((Utils.getRandom().nextInt(41) - 20) / 100d, -3.9, 3.9) * 8000.0));
                entity.getIntegers().write(4, 0);
                entity.getIntegers().write(5, 0);
                entity.getIntegers().write(6, 2);
                entity.getIntegers().write(7, 1);
                PacketContainer metadata = Main.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA);
                metadata.getIntegers().write(0, id);
                DataWatcher dataWatcher = new DataWatcher(null);
                for (Map.Entry<DataWatcherObject<?>, Object> dataWatcherObjectObjectEntry : dataWatcherObjectObjectMap.entrySet()) {
                    dataWatcher.register(dataWatcherObjectObjectEntry.getKey(), dataWatcherObjectObjectEntry.getValue());
                }
                dataWatcher.register(dataWatcherObject, CraftItemStack.asNMSCopy(clutterItem.generate()));
                metadata.getSpecificModifier(List.class).write(0, dataWatcher.c());
                for (Player onlinePlayer : location.getWorld().getPlayers()) {
                    try {
                        Main.getProtocolManager().sendServerPacket(onlinePlayer, entity, false);
                        Main.getProtocolManager().sendServerPacket(onlinePlayer, metadata, false);
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
                items.put(id, 0);
            }
            if (sprayTick > 0)sprayTick--;
        }
        PacketContainer remove = Main.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY);
        int[] ints = new int[20];
        int ii = 0;
        for (Iterator<Map.Entry<Integer, Integer>> iterator = items.entrySet().iterator();iterator.hasNext();){
            Map.Entry<Integer, Integer> integerIntegerEntry = iterator.next();
            int i = integerIntegerEntry.getValue() + 10;
            if (i >= 75){
                ints[ii++] = integerIntegerEntry.getKey();
                iterator.remove();
            }else {
                items.put(integerIntegerEntry.getKey(), i);
            }
        }
        remove.getIntegerArrays().write(0, ints);
        for (Player onlinePlayer : location.getWorld().getPlayers()) {
            try {
                Main.getProtocolManager().sendServerPacket(onlinePlayer, remove, false);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
    @Override
    public int getTickRate() {
        return 10;
    }
    public void setSpray(){
        sprayTick = 10;
    }
    static class Prize{
        ClutterItem clutterItem;
        String name;
        Prize(ClutterItem clutterItem, String name){
            this.clutterItem = clutterItem;
            this.name = name;
        }
    }
}
