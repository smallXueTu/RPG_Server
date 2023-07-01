package cn.LTCraft.core.event.world;

import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.world.ChunkEvent;

/**
 * Created by Angel、 on 2023/6/23 17:32
 */
public class ChunkSendEvent extends ChunkEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final PacketEvent packetEvent;
    private boolean cancelled;
    public ChunkSendEvent(Chunk chunk, Player player, PacketEvent packetEvent) {
        super(chunk);
        this.player = player;
        this.packetEvent = packetEvent;
        cancelled = false;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public PacketEvent getPacketEvent() {
        return packetEvent;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }
}
