package cn.LTCraft.core.game;

import cn.LTCraft.core.Config;
import cn.LTCraft.core.entityClass.spawns.TimerMobSpawn;
import io.lumine.utils.config.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * MM刷怪点管理
 * Created by Angel、 on 2022/3/27 11:50
 */
public class TimerSpawnManager {
    private static TimerSpawnManager instance = null;

    public synchronized static TimerSpawnManager getInstance() {
        if (instance == null){
            instance = new TimerSpawnManager();
        }
        return instance;
    }
    private final List<TimerMobSpawn> mobSpawns = new ArrayList<>();

    private TimerSpawnManager(){

    }

    public void init(){
        loadSpawns();
    }

    /**
     * 加载刷怪点
     */
    private void loadSpawns(){
        YamlConfiguration spawnYaml = Config.getInstance().getTimerSpawnYaml();
        for (String key : spawnYaml.getKeys(false)) {
            TimerMobSpawn mobSpawn = new TimerMobSpawn(key);
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
    public List<TimerMobSpawn> getSpawns() {
        return mobSpawns;
    }
}
