package cn.LTCraft.core.listener.packetAdapter;

import cn.LTCraft.core.Main;
import cn.LTCraft.core.event.world.ChunkSendEvent;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Chunk;
import org.bukkit.craftbukkit.v1_12_R1.event.CraftEventFactory;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by Angel、 on 2023/6/23 17:08
 */
public class ChunkPacketAdapter extends PacketAdapter {
    private final Main plugin;
    public ChunkPacketAdapter(Main plugin) {
        super(plugin, PacketType.Play.Server.MAP_CHUNK);
        this.plugin = plugin;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        Player player = event.getPlayer();
        List<Integer> values = packet.getIntegers().getValues();
        int chunkX = values.get(0);
        int chunkZ = values.get(1);
        Chunk chunk = player.getWorld().getChunkAt(chunkX, chunkZ);
        ChunkSendEvent sendEvent = new ChunkSendEvent(chunk, player, event);
        CraftEventFactory.callEvent(sendEvent);
        if (sendEvent.isCancelled()) {
            event.setCancelled(sendEvent.isCancelled());
        }
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        super.onPacketReceiving(event);
    }
}
