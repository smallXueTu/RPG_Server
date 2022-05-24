package cn.LTCraft.core.commands;

import cn.LTCraft.core.Main;
import cn.LTCraft.core.utils.PlayerUtils;
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
        String auxString = "";

        for(int i = count - 1; i < args.length; ++i) {
            auxString = auxString + args[i];
            if (i + 1 < args.length) {
                auxString = auxString + " ";
            }
        }
        plugin.getChat().setPlayerPrefix("zy", (OfflinePlayer)player, "[" + auxString + "§r]");
        plugin.getChat().setPlayerPrefix("world", (OfflinePlayer)player, "[" + auxString + "§r]");
        PlayerUtils.updatePlayerDisplayName(player);
        commandSender.sendMessage("设置成功！");
        return true;
    }
}
