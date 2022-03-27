package cn.LTCraft.core.game;

import cn.LTCraft.core.Config;
import cn.LTCraft.core.entityClass.Spawn;
import io.lumine.utils.config.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Angel、 on 2022/3/27 11:50
 */
public class SpawnManager {
    private static SpawnManager instance = null;

    public static SpawnManager getInstance() {
        if (instance == null){
            instance = new SpawnManager();
        }
        return instance;
    }
    private List<Spawn> spawns = new ArrayList<>();

    private SpawnManager(){
    }

    public void init(){
        loadSpawns();
    }

    /**
     * 加载刷怪点
     */
    private void loadSpawns(){
        YamlConfiguration spawnYaml = Config.getInstance().getSpawnYaml();
        for (String key : spawnYaml.getKeys(false)) {
            Spawn spawn = new Spawn(key);
            spawns.add(spawn);
        }
    }
    public void reload(){
        spawns.removeIf(spawn -> {
            spawn.close();
            return true;
        });
        init();
    }
    public List<Spawn> getSpawns() {
        return spawns;
    }
}
