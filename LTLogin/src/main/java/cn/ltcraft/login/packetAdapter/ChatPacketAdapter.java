package cn.ltcraft.login.packetAdapter;

import cn.ltcraft.login.Login;
import cn.ltcraft.login.other.PlayerStatus;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import org.bukkit.entity.Player;

import java.util.List;

public class ChatPacketAdapter extends PacketAdapter {
    public ChatPacketAdapter(Login plugin) {
        super(plugin, PacketType.Play.Server.CHAT);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        Player player = event.getPlayer();
        boolean cancel =  Login.playerStatus.get(player.getName()) != PlayerStatus.NORMAL;
        if (cancel && event.getPacketType() == PacketType.Play.Server.CHAT) {
            PacketContainer packet = event.getPacket();
            List<Object> values = packet.getModifier().getValues();
            for (Object value : values) {
                if (!(value instanceof IChatBaseComponent))continue;
                IChatBaseComponent textComponent = (IChatBaseComponent) value;
                String text = textComponent.toPlainText();
                if (Login.allowReceiveChat.containsKey(player.getName()) && Login.allowReceiveChat.get(player.getName()).contains(text)){
                    cancel = false;
                }
            }
        }
        if (cancel)event.setCancelled(true);
    }
}
