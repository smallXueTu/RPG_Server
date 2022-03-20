package cn.LTCraft.core.utils;

import cn.LTCraft.core.Main;
import org.bukkit.entity.Player;

public class ClientUtils {
    public static void sendMessage(Player player, String mess){
        sendMessage(player, mess.getBytes());
    }
    public static void sendMessage(Player player, byte[] mess){
        player.sendPluginMessage(Main.getInstance(), "LTCraft", mess);
    }
    public static void sendActionMessage(Player player, String mess){
        player.sendPluginMessage(Main.getInstance(), "LTCraft", ((char)3 + mess).getBytes());
    }
}
