package cn.LTCraft.core.commands;

import cn.LTCraft.core.Main;
import cn.LTCraft.core.entityClass.PlayerConfig;
import io.lumine.xikage.mythicmobs.io.MythicConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CInfoCommand implements CommandExecutor{
    private final Main plugin;
    public CInfoCommand(){
        plugin = Main.getInstance();
        plugin.getCommand("cinfo").setExecutor(this);
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = null;
        if (commandSender instanceof Player){
            player = (Player) commandSender;
        }else return false;
        player.sendMessage("§e职业技能信息:");
        MythicConfig classAttConfig = PlayerConfig.getPlayerConfig(player).getClassAttConfig();
        for (String skill : classAttConfig.getKeys("skills")) {
            player.sendMessage("§a" + skill + ":");
            player.sendMessage("   §6技能等级:" + classAttConfig.getInteger("skills." + skill + ".level") + "/" +  classAttConfig.getInteger("skills." + skill + ".maxLevel"));
            player.sendMessage("   §6觉醒等级:" + classAttConfig.getInteger("skills." + skill + ".awakenLevel") + "/5");
        }
        return true;
    }
}
