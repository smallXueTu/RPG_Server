package cn.LTCraft.core.commands;

import cn.LTCraft.core.Main;
import cn.LTCraft.core.entityClass.Exchange;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LTECommand implements CommandExecutor {
    private final Main plugin;
    public LTECommand(){
        plugin = Main.getInstance();
        plugin.getCommand("lte").setExecutor(this);
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            return false;
        }
        if ("exchange".equals(args[0])) {
            if (sender instanceof Player) {
                String name = args[1];
                if (Exchange.canExchange(name, (Player) sender)) {
                    Exchange.exchange(name, ((Player) sender));
                    sender.sendMessage("§e兑换成功！");
                } else {
                    sender.sendMessage("§c兑换失败，你的物品不足！");
                }
            }
        }
        return true;
    }
}
