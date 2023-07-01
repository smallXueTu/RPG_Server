package cn.LTCraft.core.task;

import cn.LTCraft.core.Main;
import cn.LTCraft.core.entityClass.PlayerConfig;
import cn.LTCraft.core.entityClass.spawns.ChestMobSpawn;
import cn.LTCraft.core.event.world.ChunkSendEvent;
import cn.LTCraft.core.game.ChestSpawnManager;
import cn.LTCraft.core.game.more.tickEntity.TickEntity;
import cn.LTCraft.core.utils.GameUtils;
import cn.LTCraft.core.utils.ReflectionHelper;
import cn.LTCraft.core.utils.Utils;
import cn.LTCraft.core.utils.WorldUtils;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.comphenix.protocol.wrappers.nbt.NbtBase;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.google.common.collect.HashBasedTable;
import me.libraryaddict.disguise.utilities.DisguiseUtilities;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by Angel、 on 2023/6/23 16:54
 * 宝箱任务，负责宝箱效果等。
 */
public class TreasureChestTask implements TickEntity, Listener {

    private static TreasureChestTask instance = null;
    private static HashBasedTable<String, String, Integer> basedTable = HashBasedTable.create();
    public synchronized static TreasureChestTask getInstance() {
        if (instance == null){
            instance = new TreasureChestTask();
        }
        return instance;
    }
    private final ChestSpawnManager chestSpawnManager;
    private TreasureChestTask(){
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
        chestSpawnManager = ChestSpawnManager.getInstance();
        GlobalRefresh.addTickEntity(this);
    }


    @Override
    public boolean doTick(long tick) {
        Set<String> worlds = chestSpawnManager.getSpawns().keySet().stream().map(key -> {
            String[] split = key.split(":");
            return split[split.length - 1];
        }).collect(Collectors.toSet());
        Bukkit.getOnlinePlayers().stream().filter(player -> worlds.contains(player.getWorld().getName())).forEach((Consumer<Player>) player -> {
            for (Map.Entry<String, ChestMobSpawn> entry : chestSpawnManager.getSpawns().entrySet()) {
                PlayerConfig playerConfig = PlayerConfig.getPlayerConfig(player);
                List<String> opened = (List<String>) playerConfig.getTempConfig().getList("已打开箱子", new ArrayList<>());
                Long aLong = entry.getValue().getTryOpenTimer().get(player.getName());
                boolean unseal = opened.contains(entry.getKey());
                if (aLong != null && aLong > System.currentTimeMillis()){
                    if (entry.getValue().getMobSize() <= 0) {
                        unseal = true;
                    }
                }
                if (unseal){
                    Integer eid = basedTable.get(player.getName(), entry.getKey());
                    if (eid != null){
                        PacketContainer entityPacket = Main.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY);
                        entityPacket.getIntegerArrays().write(0, new int[]{eid});
                        GameUtils.sendProtocolLibPacket(entityPacket, player);
                    }
                }
            }
        });
        return true;
    }

    @Override
    public int getTickRate() {
        return 20;
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @EventHandler
    public void onChunkSend(ChunkSendEvent event){
        Chunk chunk = event.getChunk();
        if (chestSpawnManager.getSpawns().keySet().stream().noneMatch(key -> key.endsWith(chunk.getWorld().getName()))) {//没有直接return，尽可能节省性能
            return;
        }
        PacketEvent packetEvent = event.getPacketEvent();
        Player player = event.getPlayer();
        World world = player.getWorld();
        PacketContainer packet = packetEvent.getPacket();
        List<NbtBase<?>> read = packet.getListNbtModifier().read(0);
        for (NbtBase<?> nbtBase : read) {
            if (nbtBase instanceof NbtCompound){
                NbtCompound nbtCompound = (NbtCompound) nbtBase;
                if (nbtCompound.containsKey("id")){
                    if (nbtCompound.getString("id").equals("minecraft:chest")) {
                        int x = nbtCompound.getInteger("x");
                        int y = nbtCompound.getInteger("y");
                        int z = nbtCompound.getInteger("z");
                        Location location = new Location(world, x * 1d, y * 1d, z * 1d);
                        String key = GameUtils.spawnLocationString(location);
                        ChestMobSpawn mobSpawn = chestSpawnManager.getMobSpawn(key);
                        if (mobSpawn != null){
                            PacketContainer entityPacket = Main.getProtocolManager().createPacket(PacketType.Play.Server.SPAWN_ENTITY);
                            int id = ReflectionHelper.getPrivateValue(net.minecraft.server.v1_12_R1.Entity.class, null, "entityCount");
                            ReflectionHelper.setPrivateValue(Entity.class, null, "entityCount", id + 1);
                            entityPacket.getIntegers().write(0, id);
                            entityPacket.getUUIDs().write(0, MathHelper.a(Utils.getRandom()));
                            entityPacket.getDoubles().write(0, location.getBlockX() + 0.5);
                            entityPacket.getDoubles().write(1, location.getBlockY() * 1d);
                            entityPacket.getDoubles().write(2, location.getBlockZ() + 0.5);
                            entityPacket.getIntegers().write(1, 0);
                            entityPacket.getIntegers().write(2, 0);
                            entityPacket.getIntegers().write(3, 0);
                            entityPacket.getIntegers().write(4, 0);
                            entityPacket.getIntegers().write(5, 1);
                            entityPacket.getIntegers().write(6, 78);//盔甲架
                            entityPacket.getIntegers().write(7, 0);
                            GameUtils.sendProtocolLibPacket(entityPacket, player);//发送数据包
                            PacketContainer metadataPacket = Main.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA);
                            metadataPacket.getIntegers().write(0, id);
                            WrappedDataWatcher dataWatcher = new WrappedDataWatcher();
                            dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, WrappedDataWatcher.Registry.get(String.class)), "黄色宝箱符文");
                            dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(11, WrappedDataWatcher.Registry.get(Byte.class)), (byte)25);
                            metadataPacket.getWatchableCollectionModifier().write(0, dataWatcher.getWatchableObjects());
                            GameUtils.sendProtocolLibPacket(metadataPacket, player);//发送数据包
                            basedTable.put(player.getName(), key, id);
                        }
                    }
                }
            }
        }
    }
}
