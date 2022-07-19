package cn.LTCraft.core.game;

import cn.LTCraft.core.Config;
import cn.LTCraft.core.entityClass.spawns.ChestMobSpawn;
import cn.LTCraft.core.utils.GameUtils;
import io.lumine.utils.config.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 箱子刷怪点 玩家点击某个箱子后生成怪物 玩家击杀完怪物即可打开
 * Created by Angel、 on 2022/7/15 12:33
 */
public class ChestSpawnManager {
    private static ChestSpawnManager instance = null;

    public synchronized static ChestSpawnManager getInstance() {
        if (instance == null){
            instance = new ChestSpawnManager();
        }
        return instance;
    }
    private final Map<String, ChestMobSpawn> mobSpawns = new HashMap<>();

    private ChestSpawnManager(){

    }

    public void init(){
        loadSpawns();
    }

    /**
     * 加载刷怪点
     */
    private void loadSpawns(){
        YamlConfiguration spawnYaml = Config.getInstance().getChestSpawnYaml();
        for (String key : spawnYaml.getKeys(false)) {
            ChestMobSpawn mobSpawn = new ChestMobSpawn(key);
            mobSpawns.put(GameUtils.spawnLocationString(mobSpawn.getOriginalLocation()), mobSpawn);
        }
    }
    public void reload(){
        mobSpawns.values().removeIf(mobSpawn -> {
            mobSpawn.close();
            return true;
        });
        init();
    }

    public Map<String, ChestMobSpawn> getSpawns() {
        return mobSpawns;
    }

    /**
     * 获取箱子刷怪点
     * @param location 坐标 方块坐标
     * @return 刷怪点
     */
    public ChestMobSpawn getMobSpawn(String location){
        return mobSpawns.get(location);
    }
}
