package cn.LTCraft.core.game.more;

import cn.LTCraft.core.Main;
import cn.LTCraft.core.game.more.tickEntity.TickEntity;
import cn.LTCraft.core.task.GlobalRefresh;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Angel、 on 2022/4/26 14:50
 */
public class FlashingBlock implements TickEntity {
    private int age = 0;
    private final Player player;
    private final FakeBlock[] fakeBlocks;
    public FlashingBlock(Player player, FakeBlock[] fakeBlocks){
        this.player = player;
        this.fakeBlocks = fakeBlocks;
        GlobalRefresh.addTickEntity(this);
    }
    @Override
    public boolean doTick(long tick) {
        if (age > 31 || !player.isOnline())return false;
        if (age % 2 == 0){
            for (FakeBlock fakeBlock : fakeBlocks) {
                PacketContainer blockPacket = Main.getProtocolManager().createPacket(PacketType.Play.Server.BLOCK_CHANGE);
                blockPacket.getBlockPositionModifier().write(0, new BlockPosition(fakeBlock.getBlockX(), fakeBlock.getBlockY(), fakeBlock.getBlockZ()));
                blockPacket.getBlockData().write(0, WrappedBlockData.createData(fakeBlock.getType(), fakeBlock.getData()));
                try {
                    Main.getProtocolManager().sendServerPacket(player, blockPacket, false);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }else {
            for (FakeBlock fakeBlock : fakeBlocks) {
                PacketContainer blockPacket = Main.getProtocolManager().createPacket(PacketType.Play.Server.BLOCK_CHANGE);
                blockPacket.getBlockPositionModifier().write(0, new BlockPosition(fakeBlock.getBlockX(), fakeBlock.getBlockY(), fakeBlock.getBlockZ()));
                blockPacket.getBlockData().write(0, WrappedBlockData.createData(fakeBlock.getWorld().getBlockAt(fakeBlock).getType(), fakeBlock.getWorld().getBlockAt(fakeBlock).getData()));
                try {
                    Main.getProtocolManager().sendServerPacket(player, blockPacket, false);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        age++;
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
}
