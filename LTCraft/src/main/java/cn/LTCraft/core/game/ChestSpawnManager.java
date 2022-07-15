package cn.LTCraft.core.game;

import cn.LTCraft.core.Config;
import cn.LTCraft.core.entityClass.MobSpawn;
import io.lumine.utils.config.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
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
    private final List<MobSpawn> mobSpawns = new ArrayList<>();

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
            MobSpawn mobSpawn = new MobSpawn(key);
            mobSpawns.add(mobSpawn);
        }
    }
    public void reload(){
        mobSpawns.removeIf(mobSpawn -> {
            mobSpawn.close();
            return true;
        });
        init();
    }
    public List<MobSpawn> getSpawns() {
        return mobSpawns;
    }
}
