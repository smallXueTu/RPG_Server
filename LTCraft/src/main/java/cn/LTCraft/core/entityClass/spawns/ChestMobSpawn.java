package cn.LTCraft.core.entityClass.spawns;

import cn.LTCraft.core.Config;
import io.lumine.utils.config.file.YamlConfiguration;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Angel、 on 2022/7/15 23:35
 */
public class ChestMobSpawn extends AbstractMobSpawn {
    protected final Map<String, Long> tryOpenTimer = new HashMap<>();
    protected final String quality;
    public ChestMobSpawn(String insideName) {
        super(insideName);
        quality = config.getString("品质", "普通");
    }

    public Map<String, Long> getTryOpenTimer() {
        return tryOpenTimer;
    }

    @Override
    public YamlConfiguration getYamlConfig() {
        return Config.getInstance().getChestSpawnYaml();
    }

    @Override
    public Location getAddLocation(Location location) {
        return location.add(0.5, 1, 0.5);
    }

    public String getQuality() {
        return quality;
    }
}
