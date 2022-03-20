package cn.ltcraft.login;

import cn.ltcraft.login.listener.PlayerListener;
import cn.ltcraft.login.other.InventoryPacketAdapter;
import cn.ltcraft.login.other.PlayerStatus;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class Login extends JavaPlugin {
    /**
     * 玩家的状态
     * @see cn.ltcraft.login.other.PlayerStatus
     */
    public HashMap<String, cn.ltcraft.login.other.PlayerStatus> playerStatus = new HashMap<String, cn.ltcraft.login.other.PlayerStatus>();
    /**
     * 命令对象 用来解析玩家的命令
     */
    private Command command;
    private InventoryPacketAdapter inventoryPacketAdapter;
    private static Login instance = null;
    private ProtocolManager protocolManager = null;

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
        inventoryPacketAdapter = new InventoryPacketAdapter(this);
        protocolManager.addPacketListener(inventoryPacketAdapter);
        inventoryPacketAdapter.register();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        inventoryPacketAdapter.unregister();
    }

    /**
     * 设置玩家状态
     * @param player 要设置的玩家
     * @param ps 玩家的状态
     */
    public void setPlayerStatus(Player player, PlayerStatus ps){
        this.playerStatus.remove(player.getName());
        this.playerStatus.put(player.getName(), ps);
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

    public InventoryPacketAdapter getInventoryPacketAdapter() {
        return inventoryPacketAdapter;
    }

    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }
}
