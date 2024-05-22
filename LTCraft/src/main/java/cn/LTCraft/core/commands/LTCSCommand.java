package cn.LTCraft.core.commands;

import cn.LTCraft.core.Config;
import cn.LTCraft.core.Main;
import cn.LTCraft.core.entityClass.spawns.ChestMobSpawn;
import cn.LTCraft.core.entityClass.spawns.TimerMobSpawn;
import cn.LTCraft.core.game.ChestSpawnManager;
import cn.LTCraft.core.game.TimerSpawnManager;
import cn.LTCraft.core.utils.GameUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Angel、 on 2022/3/27 13:01
 */
public class LTCSCommand implements CommandExecutor, Listener {
    private final Main plugin;
    public LTCSCommand(){
        plugin = Main.getInstance();
        plugin.getCommand("ltcs").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, plugin);
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
                map.put("x", player.getLocation().getBlockX());
                map.put("y", player.getLocation().getBlockY());
                map.put("z", player.getLocation().getBlockZ());
                map.put("maxMobs", 3);
                map.put("spawnRange", 1);
                map.put("mobName", args[2]);
                ArrayList<Object> list = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    list.add(GameUtils.spawnLocationString(player.getLocation()));
                }
                map.put("locations", list);
                Config.getInstance().getChestSpawnYaml().set(args[1], map);
                Config.getInstance().save();
                Config.getInstance().reload();
                ChestSpawnManager.getInstance().reload();
                sender.sendMessage("§a添加成功！");
                break;
            case "delete":
            case "d":
            case "remove":
            case "del":
                Config.getInstance().getChestSpawnYaml().set(args[1], null);
                Config.getInstance().save();
                ChestSpawnManager.getInstance().getSpawns().values().removeIf(mobSpawn -> {
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
                ChestSpawnManager.getInstance().reload();
                sender.sendMessage("§a重载完成！");
                break;
            case "ccs"://创建箱子刷怪点
                nextId = Integer.parseInt(args[1]);
                LTCSCommand.map = new HashMap<>();
                createChestSpawn = 1;
                sender.sendMessage("§a请放置箱子来设置箱子刷怪点！");
                break;
            case "cccs"://创建箱子刷怪点
                createChestSpawn = 0;
                Config.getInstance().getChestSpawnYaml().set("普通宝箱守卫者" + nextId++, LTCSCommand.map);
                Config.getInstance().save();
                sender.sendMessage("§a已结束！");
                break;
        }
        return true;
    }
    private static int createChestSpawn = 0;
    private static int nextId = 5;
    private static HashMap<String, Object> map = new HashMap<>();
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        Block block = event.getBlock();
        Player player = event.getPlayer();
        Location location = block.getLocation();
        switch (createChestSpawn){
            case 1:
                map.put("mobName", "普通宝箱守卫者");
                map.put("world", "t5");
                map.put("x", location.getBlockX());
                map.put("y", location.getBlockY());
                map.put("z", location.getBlockZ());
                map.put("maxMobs", 0);
                map.put("locations", new ArrayList<String>());
                map.put("spawnRange", 1);
                map.put("dropTable", "普通宝箱掉落");
                createChestSpawn++;
                player.sendMessage("§a请放置方块来设置刷怪点。");
                break;
            case 2:
                List<String> list = ((List<String>) map.get("locations"));
                int anInt = (int)map.get("maxMobs");
                list.add(GameUtils.spawnLocationString(location));
                map.put("locations", list);
                map.put("maxMobs", anInt + 1);
                event.setCancelled(true);
                break;
        }
    }
}
