package cn.LTCraft.core.commands;

import cn.LTCraft.core.Main;
import cn.LTCraft.core.dataBase.bean.PlayerInfo;
import cn.LTCraft.core.entityClass.Cooling;
import cn.LTCraft.core.entityClass.PlayerConfig;
import cn.LTCraft.core.game.Game;
import cn.LTCraft.core.utils.DateUtils;
import cn.LTCraft.core.utils.PlayerUtils;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.DisguiseConfig;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MiscDisguise;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.utilities.DisguiseParser;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LightningStrike;
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
                if (!commandSender.isOp())return true;
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
                if (!target.isOp())PlayerUtils.setGroup(target, vip1.getLevel().toStringClean().toUpperCase());
                playerInfo.commitChanges();
                commandSender.sendMessage("§c添加成功！");
            break;
            case "del":
                if (!commandSender.isOp())return true;
                if (args.length < 2){
                    commandSender.sendMessage("§c用法/vip del [player]");
                    return true;
                }
                target = Bukkit.getPlayer(args[1]);
                if (target == null){
                    commandSender.sendMessage("§c玩家不在线" + args[1] + "！");
                    return true;
                }
                playerInfo = PlayerConfig.getPlayerConfig(target).getPlayerInfo();
                vip = playerInfo.getVipStatus().clone();
                vip.setLevel(PlayerInfo.VIP.Level.NONE);
                playerInfo.setVipStatus(vip);
                playerInfo.commitChanges();
                commandSender.sendMessage("§c删除成功！");
            break;
            case "电击":
                if (args.length < 2){
                    commandSender.sendMessage("§c用法/vip 电击 [player]");
                    return true;
                }
                if (player == null || Cooling.isCooling(player, "VIP电击")) {
                    return true;
                }
                playerInfo = PlayerConfig.getPlayerConfig(player).getPlayerInfo();
                vip = playerInfo.getVipStatus();
                if (vip.compareTo(PlayerInfo.VIP.Level.VIP) < 0){
                    commandSender.sendMessage("§c你没有VIP权益！");
                    return true;
                }
                target = Bukkit.getPlayer(args[1]);
                LightningStrike strike = target.getWorld().strikeLightningEffect(target.getLocation());
                target.damage(0, strike);
                target.getWorld().playSound(target.getLocation(), "entity.lightning.impact", 2f, 0.5f);
                commandSender.sendMessage("§e玩家" + target.getName() + "受到了" +  vip.getLevel().toString() + "§e玩家" + commandSender.getName() + "的电击！");
                Cooling.cooling(player, "VIP电击", 40 - vip.getLevel().getGrade() * 10L, "VIP电击剩余冷却时间：%s%S");
            break;
            case "伪装":
                if (args.length < 2){
                    commandSender.sendMessage("§c用法/vip 伪装 伪装类型");
                    commandSender.sendMessage("§c伪装类型请查阅：https://minecraft.fandom.com/zh/wiki/Java%E7%89%88%E6%95%B0%E6%8D%AE%E5%80%BC/%E6%89%81%E5%B9%B3%E5%8C%96%E5%89%8D/%E5%AE%9E%E4%BD%93ID");
                    return true;
                }
                if (player == null || Cooling.isCooling(player, "VIP生物伪装")) {
                    return true;
                }
                playerInfo = PlayerConfig.getPlayerConfig(player).getPlayerInfo();
                vip = playerInfo.getVipStatus();
                if (vip.compareTo(PlayerInfo.VIP.Level.VIP) < 0){
                    commandSender.sendMessage("§c你没有VIP权益！");
                    return true;
                }
                if (vip.compareTo(PlayerInfo.VIP.Level.MVIP) < 0){
                    commandSender.sendMessage("§c你的VIP等级不足，你至少需要MVIP才可以伪装！");
                    return true;
                }
                if (Game.rpgWorlds.contains(player.getWorld().getName())) {
                    commandSender.sendMessage("§cRPG世界不可以伪装！");
                    return true;
                }
                DisguiseType disguiseType;
                try {
                    disguiseType = DisguiseType.valueOf(args[1].toUpperCase());
                } catch (Throwable e) {
                    commandSender.sendMessage("§c§l类型" + args[1] + "不存在！");
                    commandSender.sendMessage("§c§l支持的类型有：");
                    StringBuilder message = new StringBuilder("§c");
                    int newLine = 0;
                    for (DisguiseType value : DisguiseType.values()) {
                        message.append(value.getEntityType().getName() + ", ");
                        if (newLine++ > 10){
                            newLine = 0;
                            message.append("\n");
                        }
                    }
                    commandSender.sendMessage(message.substring(0, message.length() - 2));
                    return true;
                }
                Disguise entity;
                if (disguiseType.isMob()) {
                    entity = new MobDisguise(disguiseType);
                }else if (disguiseType.isPlayer()){
                    Disguise disguise = DisguiseAPI.getDisguise(player);
                    if (disguise != null)disguise.stopDisguise();
                    commandSender.sendMessage("§a取消伪装！");
                    return true;
                }else {
                    entity = new MiscDisguise(disguiseType);
                }
                DisguiseAPI.disguiseEntity(player, entity);
                commandSender.sendMessage("§a伪装成功！输入§d/vip 伪装 player§a来取消伪装");
                Cooling.cooling(player, "VIP生物伪装", 10, "VIP生物伪装：%s%S");
            break;
        }
        return true;
    }
}
