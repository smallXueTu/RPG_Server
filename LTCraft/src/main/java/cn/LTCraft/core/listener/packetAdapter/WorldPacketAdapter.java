package cn.LTCraft.core.listener.packetAdapter;

import cn.LTCraft.core.Main;
import cn.LTCraft.core.listener.WorldListener;
import cn.LTCraft.core.listener.worlds.WorldHandle;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import static net.minecraft.server.v1_12_R1.PacketPlayInBlockDig.EnumPlayerDigType.START_DESTROY_BLOCK;

/**
 * 副本时间监视器
 * Created by Angel、 on 2023/6/28 23:22
 */
public class WorldPacketAdapter extends PacketAdapter {
    public WorldPacketAdapter(Main plugin) {
        super(plugin,
//                PacketType.Play.Client.TELEPORT_ACCEPT, PacketType.Play.Client.TILE_NBT_QUERY, PacketType.Play.Client.CHAT, PacketType.Play.Client.CLIENT_COMMAND, PacketType.Play.Client.SETTINGS, PacketType.Play.Client.TAB_COMPLETE, PacketType.Play.Client.TRANSACTION, PacketType.Play.Client.ENCHANT_ITEM, PacketType.Play.Client.WINDOW_CLICK, PacketType.Play.Client.CLOSE_WINDOW, PacketType.Play.Client.CUSTOM_PAYLOAD, PacketType.Play.Client.BOOK_EDIT, PacketType.Play.Client.ENTITY_NBT_QUERY, PacketType.Play.Client.USE_ENTITY, PacketType.Play.Client.KEEP_ALIVE, PacketType.Play.Client.FLYING, PacketType.Play.Client.POSITION, PacketType.Play.Client.POSITION_LOOK, PacketType.Play.Client.LOOK, PacketType.Play.Client.VEHICLE_MOVE, PacketType.Play.Client.BOAT_MOVE, PacketType.Play.Client.PICK_ITEM, PacketType.Play.Client.AUTO_RECIPE, PacketType.Play.Client.ABILITIES, PacketType.Play.Client.ENTITY_ACTION, PacketType.Play.Client.STEER_VEHICLE, PacketType.Play.Client.RECIPE_DISPLAYED, PacketType.Play.Client.ITEM_NAME, PacketType.Play.Client.RESOURCE_PACK_STATUS, PacketType.Play.Client.ADVANCEMENTS, PacketType.Play.Client.TRADE_SELECT, PacketType.Play.Client.BEACON, PacketType.Play.Client.HELD_ITEM_SLOT, PacketType.Play.Client.SET_COMMAND_BLOCK, PacketType.Play.Client.SET_COMMAND_MINECART, PacketType.Play.Client.SET_CREATIVE_SLOT, PacketType.Play.Client.STRUCTURE_BLOCK, PacketType.Play.Client.UPDATE_SIGN, PacketType.Play.Client.ARM_ANIMATION, PacketType.Play.Client.SPECTATE, PacketType.Play.Client.BLOCK_PLACE,
                PacketType.Play.Client.USE_ITEM,
                PacketType.Play.Client.BLOCK_DIG
        );
    }

    @Override
    public void onPacketSending(PacketEvent event) {
//        PacketContainer packet = event.getPacket();
//        BlockPosition read = packet.getBlockPositionModifier().read(0);
//        Vector[] door1Vector1 = WorldUtils.getVectors(new Vector(1078, 108, -1209), new Vector(1078, 103, -1207));
//        if (Arrays.stream(door1Vector1).anyMatch(vector ->
//                    vector.getBlockX() == read.getX() &&
//                    vector.getBlockY() == read.getY() &&
//                    vector.getBlockZ() == read.getZ()
//                )) {
//            System.out.println(read);
//            WrappedBlockData wrappedBlockData = packet.getBlockData().read(0);
//            System.out.println(wrappedBlockData);
//        }
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        PacketContainer packet = event.getPacket();
//        if (packet.getType() == PacketType.Play.Client.CUSTOM_PAYLOAD)return;
        if (packet.getType() == PacketType.Play.Client.USE_ITEM){
            WorldHandle worldHandle = WorldListener.getWorldHandle(event.getPlayer().getWorld().getName());
            if (worldHandle != null)worldHandle.onUseItemEvent(event);
        }
        if (packet.getType() == PacketType.Play.Client.BLOCK_DIG){
            WorldHandle worldHandle = WorldListener.getWorldHandle(event.getPlayer().getWorld().getName());
            if (worldHandle != null)worldHandle.onBlockDigEvent(event);
        }
    }
}
