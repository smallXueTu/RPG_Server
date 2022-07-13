package cn.LTCraft.core.commands;

import cn.LTCraft.core.listener.PlayerListener;
import cn.LTCraft.core.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SignCommand implements CommandExecutor {
    private final Main plugin;
    public SignCommand(){
        plugin = Main.getInstance();
        plugin.getCommand("sign").setExecutor(this);
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        Player player = null;
        if (commandSender instanceof Player){
            player = (Player) commandSender;
        }else return false;
        String[] str = new String[4];
        if (args.length <=0){
            return false;
        }
        for (int i = 0; i < 4; i++) {
            if (args.length >= i + 1) {
                str[i] = args[i].replace("&", "§");
            }
        }
        PlayerListener.signEdit.put(player.getName(), str);
        Player finalPlayer = player;
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
                        PlayerListener.signEdit.remove(finalPlayer.getName())
                , 10 * 20);
        player.sendMessage("§a请在10内右击要修改的木牌。");
        return true;
    }
}
