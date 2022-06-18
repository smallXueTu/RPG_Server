package cn.LTCraft.core.commands;

import cn.LTCraft.core.Main;
import cn.LTCraft.core.entityClass.PlayerConfig;
import cn.LTCraft.core.utils.PlayerUtils;
import io.lumine.utils.config.file.YamlConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PrefixCommand implements CommandExecutor {
    private final Main plugin;
    public PrefixCommand(){
        plugin = Main.getInstance();
        plugin.getCommand("prefix").setExecutor(this);
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        Player player = null;
        if (commandSender instanceof Player){
            player = (Player) commandSender;
        }else return false;
        if (!commandSender.isOp() && !commandSender.hasPermission("LTCraft.vip")){
            commandSender.sendMessage("你没有足够的权限。");
            return true;
        }
        int count = commandSender.isOp()?2:1;
        if (args.length < count){
            if (commandSender.isOp()){
                commandSender.sendMessage("/prefix [ID] 称号");
            }else{
                commandSender.sendMessage("/prefix 称号");
            }
            return true;
        }
        if (commandSender.isOp()){
            player = Bukkit.getServer().getPlayerExact(args[0]);
            if (player==null){
                commandSender.sendMessage("找不到玩家"+args[0]+"。");
                return true;
            }
        }else {
            player = (Player) commandSender;
        }
        StringBuilder auxString = new StringBuilder();

        for(int i = count - 1; i < args.length; ++i) {
            auxString.append(args[i]);
            if (i + 1 < args.length) {
                auxString.append(" ");
            }
        }
        if (auxString.length() > 14) {
            commandSender.sendMessage("§c称号最长支持14位！");
        }
        YamlConfiguration config = PlayerConfig.getPlayerConfig(player.getName());
        config.set("prefix", auxString.toString());
        PlayerUtils.updatePlayerDisplayName(player);
        commandSender.sendMessage("设置成功！");
        return true;
    }
}
