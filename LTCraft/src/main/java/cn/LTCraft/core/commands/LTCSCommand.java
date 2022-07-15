package cn.LTCraft.core.commands;

import cn.LTCraft.core.Config;
import cn.LTCraft.core.Main;
import cn.LTCraft.core.entityClass.TimerMobSpawn;
import cn.LTCraft.core.game.TimerSpawnManager;
import cn.LTCraft.core.utils.GameUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Angel、 on 2022/3/27 13:01
 */
public class LTCSCommand implements CommandExecutor {
    private final Main plugin;
    public LTCSCommand(){
        plugin = Main.getInstance();
        plugin.getCommand("ltcs").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String cmd, String[] args) {
        if (args.length < 1)return false;
        Player player = null;
        if (sender instanceof Player) player = (Player)sender;
        switch (args[0]){
            case "create":
            case "c":
                Map<String, Object> map = new HashMap<>();
                map.put("world", player.getWorld().getName());
                map.put("x", player.getLocation().getBlockX() + 0.5);
                map.put("y", player.getLocation().getBlockY());
                map.put("z", player.getLocation().getBlockZ() + 0.5);
                map.put("maxMobs", 3);
                map.put("mobName", args[2]);
                ArrayList<Object> list = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    list.add(GameUtils.spawnLocationString(player.getLocation()));
                }
                map.put("locations", list);
                Config.getInstance().getChestSpawnYaml().set(args[1], map);
                Config.getInstance().save();
                Config.getInstance().reload();
                TimerSpawnManager.getInstance().getSpawns().add(new TimerMobSpawn(args[1]));
                sender.sendMessage("§a添加成功！");
                break;
            case "delete":
            case "d":
            case "remove":
            case "del":
                Config.getInstance().getChestSpawnYaml().set(args[1], null);
                Config.getInstance().save();
                TimerSpawnManager.getInstance().getSpawns().removeIf(mobSpawn -> {
                    if (mobSpawn.getInsideName().equals(args[1])){
                        mobSpawn.close();
                        return true;
                    }else return false;
                });
                sender.sendMessage("§a删除成功！");
                break;
            case "reload":
            case "r":
                Config.getInstance().reload();
                TimerSpawnManager.getInstance().reload();
                sender.sendMessage("§a重载完成！");
                break;
        }
        return true;
    }
}
