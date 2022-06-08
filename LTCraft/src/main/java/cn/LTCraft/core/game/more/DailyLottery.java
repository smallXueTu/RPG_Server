package cn.LTCraft.core.game.more;

import cn.LTCraft.core.Main;
import cn.LTCraft.core.entityClass.ClutterItem;
import cn.LTCraft.core.entityClass.PlayerConfig;
import cn.LTCraft.core.game.more.tickEntity.TickEntity;
import cn.LTCraft.core.task.GlobalRefresh;
import cn.LTCraft.core.utils.EntityUtils;
import cn.LTCraft.core.utils.ReflectionHelper;
import cn.LTCraft.core.utils.Utils;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import io.lumine.utils.config.file.YamlConfiguration;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Created by Angel、 on 2022/6/7 8:39
 */
public class DailyLottery implements TickEntity, Listener {
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
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
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
                dataWatcher.register(dataWatcherObject, CraftItemStack.asNMSCopy(clutterItem.getItemStack()));
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
            location.getWorld().playSound(location, Sound.ENTITY_BLAZE_SHOOT, 1, 1);
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
    @EventHandler
    public void onNPCLeftClick(NPCRightClickEvent event){
        if (event.getNPC().getId() == 128){
            Player clicker = event.getClicker();
            PlayerConfig playerConfig = PlayerConfig.getPlayerConfig(clicker);
            YamlConfiguration config = playerConfig.getConfig();
            List<String> lotteryDrawn = config.getStringList("已抽奖");
            String day = Utils.getYyyyMMdd().format(new Date());
            if (!lotteryDrawn.contains(day)) {
                Prize prize = prizes[Utils.getRandom().nextInt(prizes.length)];
                org.bukkit.inventory.ItemStack generate = prize.clutterItem.generate();
                clicker.getInventory().addItem(generate);
                lotteryDrawn.add(day);
                checkDate(lotteryDrawn);
                config.set("已抽奖", lotteryDrawn);
                clicker.sendTitle("§6§l运气不错,不抽到了：", "§d§l" + prize.name + "×" + generate.getAmount());
                setSpray();
            }else {
                Location add = location.clone().add(0, -1.5, 0);
                World world = add.getWorld();
                ((CraftWorld) world).getHandle().createExplosion(((CraftEntity) event.getNPC().getEntity()).getHandle(), add.getX(), add.getY(), add.getZ(), 5, false, false);
                EntityUtils.repel(clicker, event.getNPC().getEntity().getLocation(), 2, 10);
                clicker.sendTitle("§6§l你今天已经抽过奖了~", "");
            }
        }
    }

    /**
     * 月初清空上一个月的
     * @param days 已签到的日期
     */
    public static void checkDate(List<String> days){
        String format = Utils.getYyyyMM().format(new Date());
        if (days.stream().filter(s -> s.startsWith(format)).count() == 1) {//本月第一次签到
            Iterator<String> iterator = days.iterator();
            while (iterator.hasNext()) {
                iterator.next();
                if (days.size() > 1) {
                    iterator.remove();
                }
            }
        }
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
