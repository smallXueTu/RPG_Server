package cn.ltcraft.spawn;

import io.lumine.utils.config.file.YamlConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class LTSpawn extends JavaPlugin {
    private YamlConfiguration spawnConfig = null;
    private static LTSpawn instance = null;
    private List<Spawn> spawns = new ArrayList<>();

    private File spawnFile = null;
    public static LTSpawn getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        spawnFile = new File(getDataFolder(), "spawn.yml");
        spawnConfig = YamlConfiguration.loadConfiguration(spawnFile);
        if (!spawnFile.exists()) {
            try {
                spawnConfig.save(spawnFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        instance = this;
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, this::loadSpawns, 1);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> spawns.forEach(Spawn::onUpdate), 20, 20);
    }
    private void loadSpawns(){
        for (String key : spawnConfig.getKeys(false)) {
            Spawn spawn = new Spawn(key);
            spawns.add(spawn);
        }
    }
    private void save(){
        File spawnFile = new File(getDataFolder(), "spawn.yml");
        try {
            spawnConfig.save(spawnFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1)return false;
        if (!(sender instanceof Player))return false;
        Player player = (Player)sender;
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
                spawnConfig.set(args[1], map);
                saveAndLoad();
                spawns.add(new Spawn(args[1]));
            break;
            case "delete":
            case "d":
            case "remove":
            case "del":
                spawnConfig.set(args[1], null);
                saveAndLoad();
                for (Iterator<Spawn> iterator = spawns.iterator();iterator.hasNext();){
                    Spawn spawn = iterator.next();
                    if (spawn.getInsideName().equals(args[1])){
                        spawn.close();
                        iterator.remove();
                    }
                }
            break;
            case "reload":
            case "r":
                reload(true);
                sender.sendMessage("§a重载完成！");
            break;
        }
        return true;
    }

    public YamlConfiguration getSpawnConfig() {
        return spawnConfig;
    }
    public void saveAndLoad(){
        save();
        reload();
    }
    public void reload(){
        reload(false);
    }
    public void reload(boolean all){
        spawnConfig = YamlConfiguration.loadConfiguration(spawnFile);
        if (all){
            spawns.forEach(Spawn::close);
            spawns.clear();
            loadSpawns();
        }
    }
}
