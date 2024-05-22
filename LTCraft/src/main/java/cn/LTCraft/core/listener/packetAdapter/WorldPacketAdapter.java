package cn.LTCraft.core.listener.packetAdapter;

import cn.LTCraft.core.Main;
import cn.LTCraft.core.listener.PlayerListener;
import cn.LTCraft.core.listener.WorldListener;
import cn.LTCraft.core.listener.worlds.WorldHandle;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import net.minecraft.server.v1_12_R1.BlockPosition;
import org.bukkit.craftbukkit.v1_12_R1.block.CraftFurnace;
import org.bukkit.craftbukkit.v1_12_R1.block.CraftSign;
import org.json.JSONObject;

import static net.minecraft.server.v1_12_R1.PacketPlayInBlockDig.EnumPlayerDigType.START_DESTROY_BLOCK;

/**
 * 副本时间监视器
 * Created by Angel、 on 2023/6/28 23:22
 */
public class WorldPacketAdapter extends PacketAdapter {
    public WorldPacketAdapter(Main plugin) {
        super(plugin,
                PacketType.Play.Client.TELEPORT_ACCEPT, PacketType.Play.Client.TILE_NBT_QUERY, PacketType.Play.Client.CHAT, PacketType.Play.Client.CLIENT_COMMAND, PacketType.Play.Client.SETTINGS, PacketType.Play.Client.TAB_COMPLETE, PacketType.Play.Client.TRANSACTION, PacketType.Play.Client.ENCHANT_ITEM, PacketType.Play.Client.WINDOW_CLICK, PacketType.Play.Client.CLOSE_WINDOW, PacketType.Play.Client.CUSTOM_PAYLOAD, PacketType.Play.Client.BOOK_EDIT, PacketType.Play.Client.ENTITY_NBT_QUERY, PacketType.Play.Client.USE_ENTITY, PacketType.Play.Client.KEEP_ALIVE, PacketType.Play.Client.FLYING, PacketType.Play.Client.POSITION, PacketType.Play.Client.POSITION_LOOK, PacketType.Play.Client.LOOK, PacketType.Play.Client.VEHICLE_MOVE, PacketType.Play.Client.BOAT_MOVE, PacketType.Play.Client.PICK_ITEM, PacketType.Play.Client.AUTO_RECIPE, PacketType.Play.Client.ABILITIES, PacketType.Play.Client.ENTITY_ACTION, PacketType.Play.Client.STEER_VEHICLE, PacketType.Play.Client.RECIPE_DISPLAYED, PacketType.Play.Client.ITEM_NAME, PacketType.Play.Client.RESOURCE_PACK_STATUS, PacketType.Play.Client.ADVANCEMENTS, PacketType.Play.Client.TRADE_SELECT, PacketType.Play.Client.BEACON, PacketType.Play.Client.HELD_ITEM_SLOT, PacketType.Play.Client.SET_COMMAND_BLOCK, PacketType.Play.Client.SET_COMMAND_MINECART, PacketType.Play.Client.SET_CREATIVE_SLOT, PacketType.Play.Client.STRUCTURE_BLOCK, PacketType.Play.Client.UPDATE_SIGN, PacketType.Play.Client.ARM_ANIMATION, PacketType.Play.Client.SPECTATE,
                PacketType.Play.Client.BLOCK_PLACE,
                PacketType.Play.Client.USE_ITEM,
                PacketType.Play.Client.BLOCK_DIG,
                PacketType.Play.Server.SPAWN_ENTITY,
        PacketType.Play.Server.SPAWN_ENTITY_EXPERIENCE_ORB,
        PacketType.Play.Server.SPAWN_ENTITY_WEATHER,
        PacketType.Play.Server.SPAWN_ENTITY_LIVING,
        PacketType.Play.Server.SPAWN_ENTITY_PAINTING,
        PacketType.Play.Server.NAMED_ENTITY_SPAWN,
        PacketType.Play.Server.ANIMATION,
        PacketType.Play.Server.STATISTIC,
        PacketType.Play.Server.BLOCK_BREAK_ANIMATION,
        PacketType.Play.Server.TILE_ENTITY_DATA,
        PacketType.Play.Server.BLOCK_ACTION,
        PacketType.Play.Server.BLOCK_CHANGE,
        PacketType.Play.Server.BOSS,
        PacketType.Play.Server.SERVER_DIFFICULTY,
        PacketType.Play.Server.CHAT,
        PacketType.Play.Server.MULTI_BLOCK_CHANGE,
        PacketType.Play.Server.TAB_COMPLETE,
        PacketType.Play.Server.COMMANDS,
        PacketType.Play.Server.TRANSACTION,
        PacketType.Play.Server.CLOSE_WINDOW,
        PacketType.Play.Server.OPEN_WINDOW,
        PacketType.Play.Server.WINDOW_ITEMS,
        PacketType.Play.Server.WINDOW_DATA,
        PacketType.Play.Server.SET_SLOT,
        PacketType.Play.Server.SET_COOLDOWN,
        PacketType.Play.Server.CUSTOM_PAYLOAD,
        PacketType.Play.Server.CUSTOM_SOUND_EFFECT,
        PacketType.Play.Server.KICK_DISCONNECT,
        PacketType.Play.Server.ENTITY_STATUS,
        PacketType.Play.Server.NBT_QUERY,
        PacketType.Play.Server.EXPLOSION,
        PacketType.Play.Server.UNLOAD_CHUNK,
        PacketType.Play.Server.GAME_STATE_CHANGE,
        PacketType.Play.Server.KEEP_ALIVE,
        PacketType.Play.Server.MAP_CHUNK,
        PacketType.Play.Server.WORLD_EVENT,
        PacketType.Play.Server.WORLD_PARTICLES,
        PacketType.Play.Server.LOGIN,
        PacketType.Play.Server.MAP,
        PacketType.Play.Server.ENTITY,
        PacketType.Play.Server.REL_ENTITY_MOVE,
        PacketType.Play.Server.REL_ENTITY_MOVE_LOOK,
        PacketType.Play.Server.ENTITY_LOOK,
        PacketType.Play.Server.VEHICLE_MOVE,
        PacketType.Play.Server.OPEN_SIGN_EDITOR,
        PacketType.Play.Server.AUTO_RECIPE,
        PacketType.Play.Server.ABILITIES,
        PacketType.Play.Server.COMBAT_EVENT,
        PacketType.Play.Server.PLAYER_INFO,
        PacketType.Play.Server.LOOK_AT,
        PacketType.Play.Server.POSITION,
        PacketType.Play.Server.BED,
        PacketType.Play.Server.RECIPES,
        PacketType.Play.Server.ENTITY_DESTROY,
        PacketType.Play.Server.REMOVE_ENTITY_EFFECT,
        PacketType.Play.Server.RESOURCE_PACK_SEND,
        PacketType.Play.Server.RESPAWN,
        PacketType.Play.Server.ENTITY_HEAD_ROTATION,
        PacketType.Play.Server.SELECT_ADVANCEMENT_TAB,
        PacketType.Play.Server.WORLD_BORDER,
        PacketType.Play.Server.CAMERA,
        PacketType.Play.Server.HELD_ITEM_SLOT,
        PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE,
        PacketType.Play.Server.ENTITY_METADATA,
        PacketType.Play.Server.ATTACH_ENTITY,
        PacketType.Play.Server.ENTITY_VELOCITY,
        PacketType.Play.Server.ENTITY_EQUIPMENT,
        PacketType.Play.Server.EXPERIENCE,
        PacketType.Play.Server.UPDATE_HEALTH,
        PacketType.Play.Server.SCOREBOARD_OBJECTIVE,
        PacketType.Play.Server.MOUNT,
        PacketType.Play.Server.SCOREBOARD_TEAM,
        PacketType.Play.Server.SCOREBOARD_SCORE,
        PacketType.Play.Server.SPAWN_POSITION,
        PacketType.Play.Server.UPDATE_TIME,
        PacketType.Play.Server.TITLE,
        PacketType.Play.Server.STOP_SOUND,
        PacketType.Play.Server.NAMED_SOUND_EFFECT,
        PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER,
        PacketType.Play.Server.COLLECT,
        PacketType.Play.Server.ENTITY_TELEPORT,
        PacketType.Play.Server.ADVANCEMENTS,
        PacketType.Play.Server.UPDATE_ATTRIBUTES,
        PacketType.Play.Server.ENTITY_EFFECT,
        PacketType.Play.Server.RECIPE_UPDATE,
        PacketType.Play.Server.TAGS
        );
    }

    @Override
    public void onPacketSending(PacketEvent event) {
//        if (true)return;
//        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.TILE_ENTITY_DATA);

//        packet.getBlockPositionModifier().write(0, new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
//
//        // 设置 Tile Entity 的 NBT 数据
//        packet.getNbtModifier().write(0, nbt);
//
//        try {
//            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }
//        if (event.getPlayer().getName().equals("Angel_XX")) {
//            return;
//        }
//        PacketContainer packet = event.getPacket();
//        PacketType type = packet.getType();
//        if (type == PacketType.Play.Server.BLOCK_CHANGE){
//            System.out.println(type);
//            System.out.println(JSONObject.valueToString(packet));
//        }
//        if (type == PacketType.Play.Server.TILE_ENTITY_DATA){
//            System.out.println(type);
//            System.out.println(JSONObject.valueToString(packet));
//        }
//        if (type == PacketType.Play.Server.SCOREBOARD_TEAM)return;
//        if (type == PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER)return;
//        if (type == PacketType.Play.Server.UPDATE_HEALTH)return;
//        if (type == PacketType.Play.Server.UPDATE_TIME)return;
//        if (type == PacketType.Play.Server.SET_SLOT)return;
//        System.out.println(type);
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
        if (packet.getType() == PacketType.Play.Client.CUSTOM_PAYLOAD)return;
        if (packet.getType() == PacketType.Play.Client.POSITION)return;
        if (packet.getType() == PacketType.Play.Client.LOOK)return;
        if (packet.getType() == PacketType.Play.Client.USE_ITEM){
            WorldHandle worldHandle = WorldListener.getWorldHandle(event.getPlayer().getWorld().getName());
            if (worldHandle != null)worldHandle.onUseItemEvent(event);
        }
        if (packet.getType() == PacketType.Play.Client.BLOCK_DIG){
            WorldHandle worldHandle = WorldListener.getWorldHandle(event.getPlayer().getWorld().getName());
            if (worldHandle != null)worldHandle.onBlockDigEvent(event);
        }
        if (packet.getType() == PacketType.Play.Client.BLOCK_PLACE){
            PlayerListener.getInstance().onBlockPlaceEvent(event);
            WorldHandle worldHandle = WorldListener.getWorldHandle(event.getPlayer().getWorld().getName());
            if (worldHandle != null)worldHandle.onBlockPlaceEvent(event);
        }
    }
}
