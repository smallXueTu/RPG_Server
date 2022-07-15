package cn.LTCraft.core.game;

import cn.LTCraft.core.Config;
import cn.LTCraft.core.entityClass.MobSpawn;
import io.lumine.utils.config.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * MM刷怪点管理
 * Created by Angel、 on 2022/3/27 11:50
 */
public class MMSpawnManager {
    private static MMSpawnManager instance = null;

    public synchronized static MMSpawnManager getInstance() {
        if (instance == null){
            instance = new MMSpawnManager();
        }
        return instance;
    }
    private final List<MobSpawn> mobSpawns = new ArrayList<>();

    private MMSpawnManager(){

    }

    public void init(){
        loadSpawns();
    }

    /**
     * 加载刷怪点
     */
    private void loadSpawns(){
        YamlConfiguration spawnYaml = Config.getInstance().getMMSpawnYaml();
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
