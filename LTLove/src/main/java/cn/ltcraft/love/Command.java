package cn.ltcraft.love;

import cn.LTCraft.core.entityClass.PlayerConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        String arg1 = "";
        if (args.length == 0){
            arg1 = "帮助";
        }
        if (arg1.equals("")){
            arg1 = args[0];
        }
        String sex;
        switch (arg1){
            case "结婚":
                if (args.length < 2){
                    sender.sendMessage("§e输入§d/结婚 求婚 目标ID§e来向一个玩家求婚！");
                    return true;
                }
                String targetName = args[1];
                Player player = Bukkit.getPlayer(targetName);
                if (player == null || !player.isOnline()){
                    sender.sendMessage("§e玩家" + targetName + "不在线！");
                    return true;
                }
                PlayerConfig playerConfig = PlayerConfig.getPlayerConfig(player);
                break;
            case "帮助":
                sender.sendMessage("§e输入§d/结婚 求婚 目标ID§e来向一个玩家求婚！");
                sender.sendMessage("§e输入§d/结婚 离婚§e向自己的伴侣提出离婚！");
                sender.sendMessage("§e输入§d/结婚 性别 [男/女]§e选择自己的性别！");
                break;
        }
        return true;
    }
}
