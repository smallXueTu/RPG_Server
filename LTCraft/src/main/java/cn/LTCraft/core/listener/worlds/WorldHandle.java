package cn.LTCraft.core.listener.worlds;

import cn.LTCraft.core.event.world.ChunkSendEvent;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Created by Angel、 on 2023/6/26 22:30
 * 世界侦听器
 */
public interface WorldHandle {
    /**
     * @return 世界名
     */
    String getWorldName();
    /**
     * 区块发送事件
     */
    default void onChunkSend(ChunkSendEvent event){

    }

    /**
     * 玩家点击事件
     */
    default void onInteractEvent(PlayerInteractEvent event){

    }

    /**
     * 实体产卵事件
     */
    default void onEntitySpawn(EntitySpawnEvent event){

    }

    /**
     * 玩家使用物品包
     * 此条件一定为
     * <br />
     * <code> event.getPacketType() == PacketType.Play.Client.USE_ITEM <code/>
     */
    default void onUseItemEvent(PacketEvent event){

    }

    /**
     * 玩家挖掘方块
     * 此条件一定为
     * <br />
     * <code> event.getPacketType() == PacketType.Play.Client.BLOCK_DIG <code/>
     */
    default void onBlockDigEvent(PacketEvent event){

    }


    /**
     * 玩家放置方块
     * 此条件一定为
     * <br />
     * <code> event.getPacketType() == PacketType.Play.Client.BLOCK_PLACE <code/>
     */
    default void onBlockPlaceEvent(PacketEvent event){

    }
}
