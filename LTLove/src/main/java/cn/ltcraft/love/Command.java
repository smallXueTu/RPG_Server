package cn.ltcraft.love;

import cn.LTCraft.core.entityClass.PlayerConfig;
import io.lumine.utils.config.file.YamlConfiguration;
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
        String myLove;
        Player player = null;
        if (sender instanceof Player)player = ((Player) sender);
        switch (arg1){
            case "求婚":
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
                myLove = playerConfig.getConfig().getString("伴侣");

                if (myLove != null && !myLove.isEmpty()){
                    sender.sendMessage("§e你已经结婚了，你还想找小三？！！！");
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
                request.put(target.getName(), player.getName());
                sender.sendMessage("§a求婚成功，等待回应......");
                target.sendMessage("§c玩家" + player.getName() + "向你求婚了！");
                target.sendMessage("§c输入§d/结婚 同意§c来同意这个请求！");
                target.sendMessage("§c输入§d/结婚 拒绝§c来拒绝这个请求！");
                break;
            case "性别":
                if (!(sender instanceof Player)){
                    sender.sendMessage("§c你不是一个玩家！");
                    return true;
                }
                if (args.length < 2){
                    sender.sendMessage("§e输入§d/结婚 性别 [男/女]§e选择自己的性别！");
                    return true;
                }
                playerConfig = PlayerConfig.getPlayerConfig(player);
                sex = Love.Sex.byName(playerConfig.getConfig().getString("性别"));
                if (sex != Love.Sex.NONE){
                    sender.sendMessage("§e你已经选择性别了，不能再选了");
                    return true;
                }
                String sexName = args[1];
                Love.Sex sex1 = Love.Sex.byName(sexName);
                if (sex1== Love.Sex.NONE){
                    sender.sendMessage("§e输入的性别不正确!");
                    return true;
                }
                playerConfig.getConfig().set("性别",sex1.getName());
                sender.sendMessage("§a成功选择性别!");
                break;
            case "同意":
                if (!(sender instanceof Player)){
                    sender.sendMessage("§c你不是一个玩家！");
                    return true;
                }
                targetName = request.get(player.getName());
                if (targetName ==null){
                    sender.sendMessage("§c还没有玩家向你发起求婚！");
                }
                target = Bukkit.getPlayer(targetName);
                if (target == null || !target.isOnline()){
                    sender.sendMessage("§e求婚者" + targetName + "已经离线！");
                    return true;
                }
                targetConfig = PlayerConfig.getPlayerConfig(player);
                playerConfig = PlayerConfig.getPlayerConfig(player);
                targetConfig.getConfig().set("伴侣",player.getName());
                playerConfig.getConfig().set("伴侣",target.getName());
                Bukkit.broadcastMessage("§a恭喜"+targetName+"和"+player.getName()+"§c成为情侣，一起为他们送上最真挚的祝福吧");
                Bukkit.broadcastMessage("§c相思相见知何日，此时此夜难为情");
                Bukkit.broadcastMessage("§a温馨提示不可以闹洞房哦！");
            case "拒绝":
                if (!(sender instanceof Player)){
                    sender.sendMessage("§c你不是一个玩家！");
                    return true;
                }
                targetName = request.get(player.getName());
                if (targetName ==null){
                    sender.sendMessage("§c还没有玩家向你发起求婚！");
                }

                target = Bukkit.getPlayer(targetName);
                if (target != null && target.isOnline()) {
                    target.sendMessage("§c" + player.getName() + "拒绝了你的求婚！");
                    target.sendMessage("§c不要灰心哦！");
                    target.sendMessage("§c舍却爱难留，情丝断遥夜!");
                }
                request.remove(player.getName());
                player.sendMessage("§a拒绝成功！");
                break;
            case "离婚":
                if (!(sender instanceof Player)){
                    sender.sendMessage("§c你不是一个玩家！");
                    return true;
                }
                playerConfig = PlayerConfig.getPlayerConfig(player);
                love = playerConfig.getConfig().getString("伴侣");
                if (love == null || love.isEmpty()){
                    sender.sendMessage("§c你还没结婚呢！");
                    return true;
                }
                if (args.length >= 2 && args[1].equals("确认离婚")){
                    target = Bukkit.getPlayer(love);
                    if (target != null && target.isOnline()) {
                        target.sendMessage("§c" + player.getName() + "跟你离婚了！");
                        target.sendMessage("§c舍却爱难留，情丝断遥夜!");
                    }
                    YamlConfiguration config = PlayerConfig.getPlayerConfig(love);
                    config.set("伴侣", "");
                    PlayerConfig.savePlayerConfig(love, config);
                    playerConfig.getConfig().set("伴侣", "");
                    sender.sendMessage("§a离婚成功！");
                }else {
                    sender.sendMessage("§c相识满天下，知己有几人！");
                    sender.sendMessage("§c请三思而后行！");
                    sender.sendMessage("§c再次输入§d/结婚 离婚 确认离婚§c来确认离婚！");
                }
                break;
            case "帮助":
            default:
                sender.sendMessage("§e输入§d/结婚 求婚 目标ID§e来向一个玩家求婚！");
                sender.sendMessage("§e输入§d/结婚 同意§e来同意一个求婚！");
                sender.sendMessage("§e输入§d/结婚 拒绝§e来拒绝一个求婚！");
                sender.sendMessage("§e输入§d/结婚 离婚§e向自己的伴侣提出离婚！");
                sender.sendMessage("§e输入§d/结婚 性别 [男/女]§e选择自己的性别！");
                break;
        }
        return true;
    }
}
