package cn.ltcraft.love;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * @author 寻琴
 * @year 2022年04月19日20:35
 */
public class Command implements CommandExecutor {
    /**
     * 命令一定是/结婚
     * @author 寻琴
     * @date 2022/4/19 20:37
     * @param sender
     * @param command
     * @param s
     * @param args
     * @return boolean
     */
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String s, String[] args) {
        if (args.length == 0){
            sender.sendMessage("§c输入/结婚 帮助 来了解次命令！");
            return true;
        }
        switch (args[0]){
            case "帮助":
                sender.sendMessage("§c输入/结婚 求婚 目标ID 来向一个玩家求婚！");
                sender.sendMessage("§c输入/结婚 离婚 向自己的伴侣提出离婚！");
            break;
        }
        return true;
    }
}
