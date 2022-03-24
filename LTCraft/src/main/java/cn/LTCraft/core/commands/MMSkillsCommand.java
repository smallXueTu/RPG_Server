package cn.LTCraft.core.commands;

import cn.LTCraft.core.Main;
import cn.LTCraft.core.utils.PlayerUtils;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.skills.Skill;
import io.lumine.xikage.mythicmobs.skills.SkillManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class MMSkillsCommand implements CommandExecutor {
    private final Main plugin;
    public MMSkillsCommand(){
        plugin = Main.getInstance();
        plugin.getCommand("mmskills").setExecutor(this);
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length <= 0)return false;
        Player player = null;
        if (commandSender instanceof Player){
            player = (Player) commandSender;
        }else return false;
        PlayerUtils.castMMSkill(player, args[0]);
        return true;
    }
}
