package cn.ltcraft.love;

import cn.LTCraft.core.entityClass.Cooling;
import cn.LTCraft.core.entityClass.PlayerConfig;
import io.lumine.utils.config.file.YamlConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
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
        String love;
        String myLove;
        Player player = null;
        if (sender instanceof Player)player = ((Player) sender);
        switch (arg1){
            case "求婚"://todo 戒指
                if (!(sender instanceof Player)){
                    sender.sendMessage("§c你不是一个玩家！");
                    return true;
                }
                if (args.length < 2){
                    sender.sendMessage("§c输入§d/结婚 求婚 目标ID§e来向一个玩家求婚！");
                    return true;
                }
                playerConfig = PlayerConfig.getPlayerConfig(player);
                sex = Love.getSex(player, playerConfig);
                if (sex == Love.Sex.NONE){
                    sender.sendMessage("§e你还没选择性别，输入§d/结婚 性别 [男/女]§e选择自己的性别!");
                    return true;
                }
                myLove = Love.getLove(player, playerConfig);

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
                if (player == target){
                    sender.sendMessage("§e你不能跟自己结婚！");
                    return true;
                }
                targetConfig = PlayerConfig.getPlayerConfig(target);
                Love.Sex targetSex = Love.getSex(target, targetConfig);
                if (targetSex == Love.Sex.NONE){
                    sender.sendMessage("§e你还不知道对方性别，你敢结婚吗！");
                    return true;
                }
                love = Love.getLove(target, targetConfig);
                if (love != null && !love.isEmpty()){
                    sender.sendMessage("§e哦 太悲催了，你的" + targetSex.getName() + "神已经结婚了！");
                    return true;
                }
                request.put(target.getName(), player.getName());
                sender.sendMessage("§a求婚成功，等待你的" + targetSex.getName() + "神回应吧(≧ω≦)！");
                Bukkit.broadcastMessage("§a" + Love.getUnmarriedCall(sex, targetSex) + "" + player.getName() + "向" + target.getName() + "求婚了！");
                target.sendMessage("§a" + Love.getUnmarriedCall(sex, targetSex) + player.getName() + "向你求婚了！");
                target.sendMessage("§a输入§d/结婚 同意§c来同意跟" + sex.getPrefix() + "成为"+ Love.getCall(sex, targetSex) +"！");
                target.sendMessage("§a输入§d/结婚 拒绝§c来拒绝跟" + sex.getPrefix() + "成为"+ Love.getCall(sex, targetSex) +"！");
                break;
            case "性别":
                if (!(sender instanceof Player)){
                    sender.sendMessage("§c你不是一个玩家！");
                    return true;
                }
                if (args.length < 2){
                    sender.sendMessage("§c输入§d/结婚 性别 [男/女]§e选择自己的性别！");
                    return true;
                }
                playerConfig = PlayerConfig.getPlayerConfig(player);
                sex = Love.getSex(player, playerConfig);
                if (sex != Love.Sex.NONE){
                    sender.sendMessage("§e你已经选择性别了，不能再选了");
                    return true;
                }
                String sexName = args[1];
                sex = Love.Sex.byName(sexName);
                if (sex== Love.Sex.NONE){
                    sender.sendMessage("§c输入的性别不正确!");
                    return true;
                }
                Love.setSex(player, sex);
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
                    sender.sendMessage("§e你的" + targetName + "已经离线，你必须等它上线！");
                    return true;
                }
                targetConfig = PlayerConfig.getPlayerConfig(target);
                playerConfig = PlayerConfig.getPlayerConfig(player);
                Love.setLove(player, target.getName());
                Love.setLove(target, player.getName());
                sex = Love.getSex(player, playerConfig);
                targetSex = Love.getSex(target, targetConfig);
                Bukkit.broadcastMessage("§a恭喜"+targetName+"和"+player.getName()+"成为" + Love.getCall(sex, targetSex) + "，一起为他们送上最真挚的祝福吧~~");
                Bukkit.broadcastMessage("§a温馨提示不可以闹洞房哦！");
                player.sendMessage("§a相思相见知何日，此时此夜难为情");
                target.sendMessage("§a相思相见知何日，此时此夜难为情");
                break;
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
                playerConfig = PlayerConfig.getPlayerConfig(player);
                sex = Love.getSex(player, playerConfig);
                if (target != null && target.isOnline()) {
                    target.sendMessage("§c你的" + sex.getName() + "神" + player.getName() + "拒绝了你的求婚！");
                    target.sendMessage("§c不要灰心哦！");
                    target.sendMessage("§c舍却爱难留，情丝断遥夜!");
                }
                request.remove(player.getName());
                player.sendMessage("§a拒绝成功！");
                Bukkit.broadcastMessage("§a太悲催了" + player.getName() + "拒绝了" + targetName + "的求婚...");
                break;
            case "离婚":
                if (!(sender instanceof Player)){
                    sender.sendMessage("§c你不是一个玩家！");
                    return true;
                }
                playerConfig = PlayerConfig.getPlayerConfig(player);
                love = playerConfig.getConfig().getString("伴侣");
                if (love == null || love.isEmpty()){
                    sender.sendMessage("§e你还没结婚呢！");
                    return true;
                }
                sex = Love.getSex(player, playerConfig);
                if (args.length >= 2 && args[1].equals("确认离婚")){
                    target = Bukkit.getPlayer(love);
                    if (target != null && target.isOnline()) {
                        target.sendMessage("§e你的" + Love.getAfterMarriageCall(sex) + " " + player.getName() + "跟你离婚了！");
                        target.sendMessage("§e舍却爱难留，情丝断遥夜!");
                    }
                    YamlConfiguration config = PlayerConfig.getPlayerConfig(love);
                    config.set("伴侣", "");
                    PlayerConfig.savePlayerConfig(love, config);
                    Love.setLove(player, "", playerConfig);
                    sender.sendMessage("§a离婚成功！");
                    Bukkit.broadcastMessage("§a" + player.getName() + "和" + love + "离婚了，一片悲伤☹...");
                }else {
                    sender.sendMessage("§e相识满天下，知己有几人！");
                    sender.sendMessage("§e请三思而后行！");
                    sender.sendMessage("§e再次输入§d/结婚 离婚 确认离婚§e来确认离婚！");
                }
                break;
            case "Tp":
            case "TP":
            case "tP":
            case "tp":
                if (!(sender instanceof Player)){
                    sender.sendMessage("§c你不是一个玩家！");
                    return true;
                }
                playerConfig = PlayerConfig.getPlayerConfig(player);
                love = Love.getLove(player, playerConfig);
                if (love == null || love.isEmpty()){
                    sender.sendMessage("§e你还没结婚呢！");
                    return true;
                }
                target = Bukkit.getPlayer(love);
                if (target == null || !target.isOnline()) {
                    sender.sendMessage("§e你的伴侣不在线！");
                    return true;
                }
                if (Cooling.isCooling(player, "传送命令")) {
                    return true;
                }
                Cooling.cooling(player, "传送命令", 10, "传送命令剩余冷却时间：%s%S");
                targetConfig = PlayerConfig.getPlayerConfig(target);
                sex = Love.getSex(player, playerConfig);
                targetSex = Love.getSex(target, targetConfig);
                player.teleport(target.getLocation());
                sender.sendMessage("§a成功传送到你的" + Love.getAfterMarriageCall(targetSex) + "身边，快贴贴吧！");
                target.sendMessage("§a你的" + Love.getAfterMarriageCall(sex) + "传送到你身边了，贴贴~~~");
                break;
            case "mua":
            case "Mua":
            case "亲亲":
                if (!(sender instanceof Player)){
                    sender.sendMessage("§c你不是一个玩家！");
                    return true;
                }
                playerConfig = PlayerConfig.getPlayerConfig(player);
                love = Love.getLove(player, playerConfig);
                if (love == null || love.isEmpty()){
                    sender.sendMessage("§e你还没结婚呢！");
                    return true;
                }
                target = Bukkit.getPlayer(love);
                if (target == null || !target.isOnline()) {
                    sender.sendMessage("§e你的伴侣不在线！");
                    return true;
                }
                if (Cooling.isCooling(player, "亲亲")) {
                    return true;
                }
                Cooling.cooling(player, "亲亲", 10, "亲亲剩余冷却时间：%s%S");
                targetConfig = PlayerConfig.getPlayerConfig(target);
                sex = Love.getSex(player, playerConfig);
                targetSex = Love.getSex(target, targetConfig);
                World world = player.getWorld();
                if (target.getWorld() != player.getWorld() || target.getLocation().distance(player.getLocation()) > 2){
                    sender.sendMessage("§e你的" + Love.getAfterMarriageCall(targetSex) + "不在你身边~");
                    target.sendMessage("§e你的" + Love.getAfterMarriageCall(sex) + "想和你亲亲但你不在" + sex.getPrefix() + "身边，快去主动找她吧！");
                    return true;
                }
                world.spawnParticle(Particle.HEART, player.getLocation().add(0, 1.8, 0), 5, 0.3, 0.3, 0.3);
                world.spawnParticle(Particle.HEART, target.getLocation().add(0, 1.8, 0), 5, 0.3, 0.3, 0.3);
                sender.sendMessage("§aMua~~~");
                target.sendMessage("§aMua~~~");
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if (onlinePlayer == player || onlinePlayer == target) continue;
                    PlayerConfig onlinePlayerConfig = PlayerConfig.getPlayerConfig(onlinePlayer);
                    String partner = onlinePlayerConfig.getConfig().getString("伴侣");
                    if (partner == null || partner.isEmpty()){
                        onlinePlayer.sendMessage("§e" + player.getName() + "和" + target.getName() + "亲亲了，你还没有对象~~~");
                    }else {
                        Player partnerP = Bukkit.getPlayer(partner);
                        if (partnerP != null && partnerP.isOnline()){
                            Love.Sex partnerSex = Love.getSex(partnerP);
                            onlinePlayer.sendMessage("§e" + player.getName() + "和" + target.getName() + "亲亲了，你也快去跟你的" + Love.getAfterMarriageCall(partnerSex) + "亲亲吧~~~");
                        }else {
                            onlinePlayer.sendMessage("§e" + player.getName() + "和" + target.getName() + "亲亲了，快叫上你的伴侣上线亲亲吧~~~");
                        }
                    }
                }
                break;
            case "帮助":
            default:
                sender.sendMessage("§e--------------§a结婚系统§e--------------");
                sender.sendMessage("§e求婚--§d/结婚 求婚 目标ID§e来向一个玩家求婚！");
                sender.sendMessage("§e同意求婚--§d/结婚 同意§e来同意一个求婚！");
                sender.sendMessage("§e拒绝求婚--§d/结婚 拒绝§e来拒绝一个求婚！");
                sender.sendMessage("§e离婚--§d/结婚 离婚§e向自己的伴侣提出离婚！");
                sender.sendMessage("§eTP--§d/结婚 tp§e传送到你的伴侣身边！");
                sender.sendMessage("§e亲亲--§d/结婚 亲亲§e跟你的伴侣亲亲！");
                sender.sendMessage("§e选择性别--§d/结婚 性别 [男/女]§e选择自己的性别！");
                break;
        }
        return true;
    }
}
