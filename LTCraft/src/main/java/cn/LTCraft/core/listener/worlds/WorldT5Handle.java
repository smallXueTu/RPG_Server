package cn.LTCraft.core.listener.worlds;

import cn.LTCraft.core.Main;
import cn.LTCraft.core.event.world.ChunkSendEvent;
import cn.LTCraft.core.utils.GameUtils;
import cn.LTCraft.core.utils.WorldUtils;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import io.lumine.utils.tasks.Scheduler;
import net.minecraft.server.v1_12_R1.EnumHand;
import net.minecraft.server.v1_12_R1.PacketPlayInBlockDig;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.minecraft.server.v1_12_R1.PacketPlayInBlockDig.EnumPlayerDigType.START_DESTROY_BLOCK;

/**
 * Created by Angel、 on 2023/6/28 20:21
 */
public class WorldT5Handle implements WorldHandle{
    private final static Vector[] door1Vector1 = WorldUtils.getVectors(new Vector(1078, 108, -1209), new Vector(1078, 103, -1207));
    @Override
    public String getWorldName() {
        return "t5";
    }

    @Override
    public void onChunkSend(ChunkSendEvent event) {
        Chunk chunk = event.getChunk();
        if (chunk.getX() == 67 && chunk.getZ() == -76){//发送堵塞的块
            Player player = event.getPlayer();
            PacketContainer blockPacket = Main.getProtocolManager().createPacket(PacketType.Play.Server.BLOCK_CHANGE);
            blockPacket.getBlockData().write(0, WrappedBlockData.createData(Material.QUARTZ_BLOCK, 2));//石英柱
            List<PacketContainer> packetContainers = new ArrayList<>();
            for (Vector vector : door1Vector1) {
                PacketContainer packetContainer = blockPacket.shallowClone();
                packetContainer.getBlockPositionModifier().write(0, new BlockPosition(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ()));
                packetContainers.add(packetContainer);
            }
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> packetContainers.forEach(packetContainer -> GameUtils.sendProtocolLibPacket(packetContainer, player)), 20);
        }
    }

    @Override
    public void onUseItemEvent(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        BlockPosition pos = packet.getBlockPositionModifier().read(0);
        if (Arrays.stream(door1Vector1).anyMatch(vector ->
                vector.getBlockX() == pos.getX() &&
                vector.getBlockY() == pos.getY() &&
                vector.getBlockZ() == pos.getZ()
        )) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onBlockDigEvent(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        BlockPosition pos = packet.getBlockPositionModifier().read(0);
        if (Arrays.stream(door1Vector1).anyMatch(vector ->
                vector.getBlockX() == pos.getX() &&
                        vector.getBlockY() == pos.getY() &&
                        vector.getBlockZ() == pos.getZ()
        )) {
            PacketContainer blockPacket = Main.getProtocolManager().createPacket(PacketType.Play.Server.BLOCK_CHANGE);
            blockPacket.getBlockData().write(0, WrappedBlockData.createData(Material.QUARTZ_BLOCK, 2));//石英柱
            blockPacket.getBlockPositionModifier().write(0, pos);
            GameUtils.sendProtocolLibPacket(blockPacket, event.getPlayer());
            event.setCancelled(true);
        }
    }
}
