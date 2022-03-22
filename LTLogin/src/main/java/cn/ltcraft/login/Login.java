package cn.ltcraft.login;

import cn.LTCraft.core.utils.Utils;
import cn.ltcraft.login.listener.PlayerListener;
import cn.ltcraft.login.packetAdapter.ChatPacketAdapter;
import cn.ltcraft.login.packetAdapter.InventoryPacketAdapter;
import cn.ltcraft.login.other.PlayerStatus;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_12_R1.ChatBaseComponent;
import net.minecraft.server.v1_12_R1.ChatComponentText;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Login extends JavaPlugin {
    /**
     * 错误次数
     */
    public static HashMap<String, Integer> errorCount = new HashMap<>();
    /**
     * 玩家的状态
     * @see cn.ltcraft.login.other.PlayerStatus
     */
    public static HashMap<String, PlayerStatus> playerStatus = new HashMap<>();
    /**
     * 内容就不做清理了，撑死也就那几条。
     */
    public static HashMap<String, List<String>> allowReceiveChat = new HashMap<>();
    /**
     * 命令对象 用来解析玩家的命令
     */
    private Command command;
    /**
     * 不可修改List
     */
    private static List<PacketAdapter> adapters;
    private static Login instance = null;
    private static ProtocolManager protocolManager = null;

    /**
     * 获取这个对象的实例
     * @return this
     */
    public static Login getInstance(){
        return instance;
    }

    /**
     * 插件加载
     */
    @Override
    public void onLoad() {
        instance = this;
        super.onLoad();
    }

    /**
     * 插件启用
     */
    @Override
    public void onEnable() {
        super.onEnable();
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        command = new Command(this);
        protocolManager = ProtocolLibrary.getProtocolManager();
        List<PacketAdapter> packetAdapterList = new ArrayList<>();
        packetAdapterList.add(new InventoryPacketAdapter(this));
        packetAdapterList.add(new ChatPacketAdapter(this));
        adapters = Collections.unmodifiableList(packetAdapterList);
        for (PacketAdapter adapter : adapters) {
            protocolManager.addPacketListener(adapter);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        for (PacketAdapter adapter : adapters) {
            protocolManager.removePacketListener(adapter);
        }
    }

    /**
     * 设置玩家状态
     * @param player 要设置的玩家
     * @param ps 玩家的状态
     */
    public void setPlayerStatus(Player player, PlayerStatus ps){
        playerStatus.put(player.getName(), ps);
    }

    /**
     * 执行这个插件相关的指令
     * @param sender 执行者
     * @param command 命令
     * @param label 标签
     * @param args 参数
     * @return  是否成功 似乎没卵用
     */
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        return this.command.onCommand(sender, command, label, args);
    }

    public static InventoryPacketAdapter getInventoryPacketAdapter() {
        return (InventoryPacketAdapter) adapters.get(0);//必定是0
    }

    public static ProtocolManager getProtocolManager() {
        return protocolManager;
    }
    public static void forceSendMessage(Player player, String message){
        String name = player.getName();
        if (allowReceiveChat.containsKey(name)){
            allowReceiveChat.get(name).add(Utils.clearColor(message));
        }else {
            allowReceiveChat.put(name, Stream.of(Utils.clearColor(message)).collect(Collectors.toList()));
        }
        player.sendMessage(message);
    }
}
