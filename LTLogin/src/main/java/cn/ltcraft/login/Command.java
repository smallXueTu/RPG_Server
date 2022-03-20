/**
 * Created with IntelliJ IDEA.
 * projectName: RPG
 * fileName: Command.java
 * packageName: cn.ltcraft.login
 * date: 2020-07-15 1:48
 *
 * @Auther: Angel、
 */
package cn.ltcraft.login;

import cn.LTCraft.core.dataBase.bean.PlayerInfo;
import cn.LTCraft.core.entityClass.PlayerConfig;
import cn.LTCraft.core.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created with IntelliJ IDEA.
 * @Auther: Angel、
 * @Date: 2020/07/15/1:48
 * @Description: 大半夜的睡不着了，写会代码吧...
 */

public class Command {
    /**
     * 插件主类
     */
    private Login plugin;

    /**
     * 构造函数
     * @param plugin 插件主类
     */
    public Command(Login plugin){
        this.plugin = plugin;
    }


    /**
     * 执行这个插件相关的指令
     * @param sender 执行者
     * @param command 命令
     * @param label 标签
     * @param args 参数
     * @return 是否成功 似乎没卵用
     */
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (args.length==0){
            sender.sendMessage("§e修改密码输入/login up 新密码 新密码");
            sender.sendMessage("§e绑定QQ输入/login qq QQ号");
            sender.sendMessage("§e绑定邮箱输入/login email 邮箱");
            return true;
        }
        Player player = sender instanceof Player ? ((Player) sender):null;
        switch (args[0]){
            case "e":
            case "email":
                if (player == null){
                    sender.sendMessage("§e你不是一个玩家！");
                    return true;
                }
                if (args.length==1){
                    sender.sendMessage("§e绑定邮箱输入/login email 邮箱");
                    return true;
                }
                if(Utils.checkEmail(args[1])){
                    PlayerInfo playerInfo = PlayerConfig.getPlayerConfig(player).getPlayerInfo();
                    if (playerInfo != null){
                        if (playerInfo.getEmail()!=null){
                            sender.sendMessage("§c这个ID已经绑定邮箱了！");
                        }else {
                            playerInfo.setEmail(args[1]);
                            playerInfo.commitChanges();
                            sender.sendMessage("§a修改成功！");
                        }
                    }else {
                        sender.sendMessage("§c找不到你的信息。");
                    }
                }else{
                    sender.sendMessage("§c邮箱不合法！");
                }
            break;
            case "up":
            case "updatePassword":
            case "updatePasswd":
            case "passwd":
            case "p":
                if (player == null){
                    sender.sendMessage("§e你不是一个玩家！");
                    return true;
                }
                if (args.length==2){
                    sender.sendMessage("§e修改密码输入/login up 新密码 新密码");
                    return true;
                }
                String check = Utils.checkPassword(args[1]);
                if(check.equals("true")){
                    if (!args[1].equals(args[2])){
                        sender.sendMessage("§c两次密码不一致！");
                    }else {
                        PlayerInfo playerInfo = PlayerConfig.getPlayerConfig(player).getPlayerInfo();
                        if (playerInfo != null) {
                            playerInfo.setPassword(args[1]);
                            playerInfo.commitChanges();
                            sender.sendMessage("§a修改成功！");
                        } else {
                            sender.sendMessage("§c找不到你的信息。");
                        }
                    }
                }else{
                    sender.sendMessage("§c"+check);
                }
            break;
            case "q":
            case "qq":
                if (player == null){
                    sender.sendMessage("§e你不是一个玩家！");
                    return true;
                }
                if (args.length==1){
                    sender.sendMessage("§e绑定QQ输入/login qq QQ号");
                    return true;
                }
                Long qq = Long.valueOf(args[1]);
                if(qq.toString().length()>=5 && qq.toString().length()<=10){
                    PlayerInfo playerInfo = PlayerConfig.getPlayerConfig(player).getPlayerInfo();
                    if (playerInfo != null){
                        if (playerInfo.getQQ()!=null){
                            sender.sendMessage("§c这个ID已经绑定QQ了！");
                        }else {
                            playerInfo.setQQ(qq);
                            playerInfo.commitChanges();
                            sender.sendMessage("§a修改成功！");
                        }
                    }else {
                        sender.sendMessage("§c找不到你的信息。");
                    }
                }else{
                    sender.sendMessage("§cQQ不合法！");
                }
            break;
            default:
                sender.sendMessage("§e修改密码输入/login up 新密码 新密码");
                sender.sendMessage("§e绑定QQ输入/login qq QQ号");
                sender.sendMessage("§e绑定邮箱输入/login email 邮箱");
            break;
        }
        return true;
    }
}
