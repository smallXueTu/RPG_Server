package cn.LTCraft.core.commands;

import cn.LTCraft.core.Main;
import cn.LTCraft.core.dataBase.bean.PlayerInfo;
import cn.LTCraft.core.entityClass.PlayerConfig;
import cn.LTCraft.core.utils.DateUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Angel、 on 2022/4/6 10:23
 */
public class VIPCommand implements CommandExecutor {
    private final static Map<String, PlayerInfo.VIP.Level> levels = new HashMap<String, PlayerInfo.VIP.Level>(){
        {
            put("VIP", PlayerInfo.VIP.Level.VIP);
            put("SVIP", PlayerInfo.VIP.Level.SVIP);
            put("MVIP", PlayerInfo.VIP.Level.MVIP);
            put("V", PlayerInfo.VIP.Level.VIP);
            put("S", PlayerInfo.VIP.Level.SVIP);
            put("M", PlayerInfo.VIP.Level.MVIP);
        }
    };
    private final Main plugin;
    public VIPCommand(){
        plugin = Main.getInstance();
        plugin.getCommand("vip").setExecutor(this);
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length <= 0){
            return false;
        }
        Player player = null;
        if (commandSender instanceof Player)
            player = (Player) commandSender;
        PlayerInfo playerInfo;
        Player target;
        PlayerInfo.VIP vip;
        PlayerInfo.VIP.Level level;
        switch (args[0]){
            case "add":
                if (args.length < 3){
                    commandSender.sendMessage("§c用法/vip add [player] [天数] [level]");
                    return true;
                }
                target = Bukkit.getPlayerExact(args[1]);
                if (target == null){
                    commandSender.sendMessage("§c玩家不在线" + args[1] + "！");
                    return true;
                }
                playerInfo = PlayerConfig.getPlayerConfig(target).getPlayerInfo();
                vip = playerInfo.getVipStatus();
                if (args.length > 3){
                    level = levels.get(args[3]);
                    if (level == null){
                        commandSender.sendMessage("§c不存在等级" + args[3] + "！");
                        return true;
                    }
                }else {
                    level = vip.getLevel();
                }
                int days = Integer.parseInt(args[2]);
                PlayerInfo.VIP vip1;
                if (level != vip.getLevel()){
                    if (days == 0){
                        vip1 = vip.clone();
                        vip1.setLevel(level);
                    }else {
                        vip1 = new PlayerInfo.VIP(level, System.currentTimeMillis() / 1000 + DateUtils.getDaysTimeStamp(days));
                    }
                }else {
                    vip1 = vip.clone();
                    vip1.setExpirationTime(new Date(vip.getExpirationTime().getTime() + DateUtils.getDaysTimeStamp(days)));
                }
                playerInfo.setVipStatus(vip1);
                playerInfo.commitChanges();
                commandSender.sendMessage("§c添加成功！");
            break;
            case "del":
                if (args.length < 2){
                    commandSender.sendMessage("§c用法/vip del [player]");
                    return true;
                }
                target = Bukkit.getPlayerExact(args[1]);
                if (target == null){
                    commandSender.sendMessage("§c玩家不在线" + args[1] + "！");
                    return true;
                }
                playerInfo = PlayerConfig.getPlayerConfig(target).getPlayerInfo();
                PlayerInfo.VIP vipStatus = playerInfo.getVipStatus().clone();
                vipStatus.setLevel(PlayerInfo.VIP.Level.NONE);
                playerInfo.setVipStatus(vipStatus);
                playerInfo.commitChanges();
                commandSender.sendMessage("§c删除成功！");
                break;
        }
        return true;
    }
}
