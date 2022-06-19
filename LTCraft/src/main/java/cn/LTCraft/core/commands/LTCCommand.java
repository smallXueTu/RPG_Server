package cn.LTCraft.core.commands;

import cn.LTCraft.core.Main;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.classes.RPGClass;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.listener.AttributeListener;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Angel、 on 2022/6/19 12:09
 */
public class LTCCommand implements CommandExecutor {
    private final Main plugin;
    public LTCCommand(){
        plugin = Main.getInstance();
        plugin.getCommand("ltc").setExecutor(this);
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String cmd, String[] args) {
        Player player = null;
        if (commandSender instanceof Player) {
            player = (Player) commandSender;
        }
        if (!commandSender.isOp()) return true;
        if (args.length < 1) {
            commandSender.sendMessage("/ltc String");
            return true;
        }
        Player target = null;
        if (args.length >=2){
            target = Bukkit.getPlayer(args[1]);
        }
        assert target != null;
        int level;
        PlayerClass data;
        switch (args[0]) {
            case "level":
                if (args.length < 3){
                    commandSender.sendMessage("§c/ltc level [Player] [Level]");
                    return true;
                }
                level = Integer.parseInt(args[2]);
                data = SkillAPI.getPlayerData(target).getMainClass();
                data.setLevel(level);
                data.setExp(0);
                data.setPoints((int) (data.getData().getGroupSettings().getPointsPerLevel() * level));
                data.getPlayerData().setAttribPoints((int) (data.getData().getGroupSettings().getAttribsPerLevel() * (level - 1)));
                commandSender.sendMessage("§a修改成功！");
            break;
            case "class":
                if (args.length < 3){
                    commandSender.sendMessage("§c/ltc class [Player] [class]");
                    return true;
                }
                PlayerData playerData = SkillAPI.getPlayerData(target);
                RPGClass rpgClass = SkillAPI.getClass(args[2]);
                if (rpgClass == null){
                    commandSender.sendMessage("§c找不到职业" + args[2] + "！");
                    return true;
                }
                playerData.setClass(rpgClass);
                playerData.updateScoreboard();
                AttributeListener.updatePlayer(playerData);
                commandSender.sendMessage("§a修改成功！");
            break;
        }
        return true;
    }
}
