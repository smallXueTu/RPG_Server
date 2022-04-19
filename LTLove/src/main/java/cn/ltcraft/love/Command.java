package cn.ltcraft.love;

import cn.LTCraft.core.entityClass.PlayerConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 寻琴
 * @year 2022年04月19日20:35
 */
public class Command implements CommandExecutor {
    //请求
    private Map<String, String> request = new HashMap<>();
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
        Love.Sex sex;
        PlayerConfig playerConfig;
        PlayerConfig targetConfig;
        Player player = null;
        if (sender instanceof Player)player = ((Player) sender);
        switch (arg1){
            case "结婚":
                if (!(sender instanceof Player)){
                    sender.sendMessage("§c你不是一个玩家！");
                    return true;
                }
                if (args.length < 2){
                    sender.sendMessage("§e输入§d/结婚 求婚 目标ID§e来向一个玩家求婚！");
                    return true;
                }
                playerConfig = PlayerConfig.getPlayerConfig(player);
                sex = Love.Sex.byName(playerConfig.getConfig().getString("性别"));
                if (sex == Love.Sex.NONE){
                    sender.sendMessage("§e你还没选择性别，输入§d/结婚 性别 [男/女]§e选择自己的性别!");
                    return true;
                }
                if (sex == Love.Sex.Secrecy){
                    sender.sendMessage("§e对方性别保密，你不能跟ta结婚！");
                    return true;
                }
                String targetName = args[1];
                Player target = Bukkit.getPlayer(targetName);
                if (target == null || !target.isOnline()){
                    sender.sendMessage("§e玩家" + targetName + "不在线！");
                    return true;
                }
                targetConfig = PlayerConfig.getPlayerConfig(player);
                Love.Sex targetSex = Love.Sex.byName(targetConfig.getConfig().getString("性别"));
                if (targetSex == Love.Sex.NONE){
                    sender.sendMessage("§e你还不知道对方性别，你敢结婚吗！");
                    return true;
                }
                String love = targetConfig.getConfig().getString("伴侣");
                if (love != null && !love.isEmpty()){
                    sender.sendMessage("§e哦 太悲催了，你的" + targetSex.getName() + "神已经结婚了！");
                    return true;
                }
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
