package cn.LTCraft.core.listener;

import cn.LTCraft.core.event.world.ChunkSendEvent;
import cn.LTCraft.core.game.Game;
import cn.LTCraft.core.listener.worlds.WorldHandle;
import cn.LTCraft.core.listener.worlds.WorldT5Handle;
import org.bukkit.Chunk;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemMendEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * 没个世界的自定义事件
 * Created by Angel、 on 2023/6/24 23:57
 */
public class WorldListener implements Listener {

    public static final Map<String, WorldHandle> worldHandles = new HashMap<>();

    public WorldListener(){
        worldHandles.put("t5", new WorldT5Handle());
    }
    public static WorldHandle getWorldHandle(String world){
        return worldHandles.get(world);
    }
    @EventHandler
    public void onChunkSend(ChunkSendEvent event){
        WorldHandle worldHandle = getWorldHandle(event.getChunk().getWorld().getName());
        if (worldHandle != null)worldHandle.onChunkSend(event);
    }
    @EventHandler
    public void onInteractSend(PlayerInteractEvent event){
        WorldHandle worldHandle = getWorldHandle(event.getPlayer().getWorld().getName());
        if (worldHandle != null)worldHandle.onInteractEvent(event);
    }
    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event){
//        WorldHandle worldHandle = getWorldHandle(event.getPlayer().getWorld().getName());
//        if (worldHandle != null)worldHandle.onEntitySpawn(event);
    }
//    public void on(Player){
//
//    }
}
