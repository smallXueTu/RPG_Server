package cn.LTCraft.core.entityClass.spawns;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by Angel、 on 2022/7/15 23:35
 */
public class ChestMobSpawn extends AbstractMobSpawn {
    private final Map<String, Long> tryOpenTimer = new HashMap<>();
    public ChestMobSpawn(String insideName) {
        super(insideName);
    }

    public Map<String, Long> getTryOpenTimer() {
        return tryOpenTimer;
    }
}
