package cn.LTCraft.core.commands;

import cn.LTCraft.core.Config;
import cn.LTCraft.core.Main;
import cn.LTCraft.core.entityClass.Spawn;
import cn.LTCraft.core.game.SpawnManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Angel、 on 2022/3/27 13:01
 */
public class LTSCommand implements CommandExecutor {
    private final Main plugin;
    public LTSCommand(){
        plugin = Main.getInstance();
        plugin.getCommand("lts").setExecutor(this);
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
                map.put("cooling", 10);
                map.put("range", 16);
                map.put("mobName", args[2]);
                Config.getInstance().getSpawnYaml().set(args[1], map);
                Config.getInstance().save();
                Config.getInstance().reload();
                SpawnManager.getInstance().getSpawns().add(new Spawn(args[1]));
                sender.sendMessage("§a添加成功！");
                break;
            case "delete":
            case "d":
            case "remove":
            case "del":
                Config.getInstance().getSpawnYaml().set(args[1], null);
                Config.getInstance().save();
                SpawnManager.getInstance().getSpawns().removeIf(spawn -> {
                    if (spawn.getInsideName().equals(args[1])){
                        spawn.close();
                        return true;
                    }else return false;
                });
                sender.sendMessage("§a删除成功！");
                break;
            case "reload":
            case "r":
                Config.getInstance().reload();
                SpawnManager.getInstance().reload();
                sender.sendMessage("§a重载完成！");
                break;
        }
        return true;
    }
}
