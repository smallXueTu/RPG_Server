package cn.LTCraft.core.commands;

import cn.LTCraft.core.Main;
import cn.LTCraft.core.task.GarbageClear;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

public class ClearCommand implements CommandExecutor {
    private final Main plugin;
    public ClearCommand(){
        plugin = Main.getInstance();
        plugin.getCommand("clear").setExecutor(this);
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        int drop = 0;
        switch (args.length > 0 ? args[0]:""){
            case "all":
                int count = 0;
                for (World world : Bukkit.getWorlds()) {
                    List<Entity> entities =  world.getEntities();
                    for (Entity entity : entities) {
                        if (entity instanceof Player)continue;
                        if (entity instanceof LivingEntity) {
                            entity.remove();
                            count++;
                        }
                    }
                }
                Bukkit.broadcastMessage("§l§e[扫地娘]§c管理员指示杀死了§d" + count + "§c只生物！");
            break;
            case "f":
                drop = GarbageClear.getInstance().clearDrop(true);
                Bukkit.broadcastMessage("§l§e[扫地娘]§c管理员指示清理了§d" + drop + "§c个掉落物！");
            break;
            default:
                drop = GarbageClear.getInstance().clearDrop();
                Bukkit.broadcastMessage("§l§e[扫地娘]§c管理员指示清理了§d" + drop + "§c个掉落物！");
            break;
        }
        return true;
    }
}
