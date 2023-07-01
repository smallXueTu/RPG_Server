package cn.LTCraft.core.listener.packetAdapter;

import cn.LTCraft.core.Main;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by Angel、 on 2023/6/23 23:30
 */
public class EntityMetadataPacketAdapter extends PacketAdapter {
    private final Main plugin;
    public EntityMetadataPacketAdapter(Main plugin) {
        super(plugin, /*PacketType.Play.Server.SPAWN_ENTITY,*/ PacketType.Play.Server.ENTITY_METADATA);
        this.plugin = plugin;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if (true)return;
        PacketContainer packet = event.getPacket();
        List<WrappedWatchableObject> read = packet.getWatchableCollectionModifier().read(0);
        if (read.stream().anyMatch(wrappedWatchableObject -> wrappedWatchableObject.getValue().toString().startsWith("§l§c-") || wrappedWatchableObject.getValue().toString().startsWith("§l§a+"))) {
//            for (WrappedWatchableObject wrappedWatchableObject : read) {
//                System.out.println(wrappedWatchableObject);
//            }
//            event.setCancelled(true);
        }else {

//            for(Iterator<WrappedWatchableObject> iterator = read.iterator();iterator.hasNext();){
//                WrappedWatchableObject next = iterator.next();
//                if (next.getIndex() == 11 || next.getIndex() == 0){
//                    System.out.println(next);
//                    iterator.remove();
//                }
//            }
//            packet.getWatchableCollectionModifier().write(0, read);
        }
    }
}
