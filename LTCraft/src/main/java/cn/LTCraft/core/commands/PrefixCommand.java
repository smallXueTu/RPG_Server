package cn.LTCraft.core.commands;

import cn.LTCraft.core.Main;
import cn.LTCraft.core.entityClass.PlayerConfig;
import cn.LTCraft.core.task.GlobalRefresh;
import cn.LTCraft.core.utils.PlayerUtils;
import io.lumine.utils.config.file.YamlConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrefixCommand implements CommandExecutor {
    public static final Map<String, Long> settingPlayers = new HashMap<>();
    private final Main plugin;
    public PrefixCommand(){
        plugin = Main.getInstance();
        plugin.getCommand("prefix").setExecutor(this);
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        Player player = null;
        if (!commandSender.isOp()){
            commandSender.sendMessage("你没有足够的权限。");
            return true;
        }
        if (commandSender.isOp()) {
            if (args.length < 2) {
                    commandSender.sendMessage("/prefix [ID] 称号");
                return true;
            }
            player = Bukkit.getServer().getPlayerExact(args[0]);
            if (player == null) {
                commandSender.sendMessage("找不到玩家" + args[0] + "。");
                return true;
            }
            StringBuilder auxString = new StringBuilder();
            for (int i = 1; i < args.length; ++i) {
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
        }else {
            settingPlayers.put(commandSender.getName(), GlobalRefresh.getTick());
            commandSender.sendMessage("§e请30秒内在聊天框输入你要设置的称号，输入exit退出:");
        }
        return true;
    }
}
