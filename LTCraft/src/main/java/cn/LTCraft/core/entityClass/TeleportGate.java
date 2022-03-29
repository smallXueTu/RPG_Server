package cn.LTCraft.core.entityClass;

import cn.LTCraft.core.Config;
import cn.LTCraft.core.utils.GameUtils;
import cn.LTCraft.core.utils.MathUtils;
import io.lumine.xikage.mythicmobs.io.MythicConfig;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Angel、 on 2022/3/27 11:37
 */
public class TeleportGate {
    public static final List<Integer> blockIDs = Arrays.asList(119, 209, 90);
    private final String gateName;
    private final List<String> locations = new ArrayList<>();
    private String worldName = null;
    private final Location to;
    private boolean invalid = false;
    private final List<String> mythicSkills;
    public TeleportGate(String gateName){
        this.gateName = gateName;
        MythicConfig config = new MythicConfig(gateName, Config.getInstance().getGateYaml());
        to = GameUtils.spawnLocation(config.getString("to"));
        if (to == null){
            invalid = true;
        }else {
            List<String> locations = config.getStringList("locations");
            for (String location : locations) {
                Location loc = GameUtils.spawnLocation(location);
                if (loc == null)continue;
                if (worldName == null)worldName = loc.getWorld().getName();
                this.locations.add(GameUtils.spawnLocationString(loc));
            }
        }
        mythicSkills = config.getStringList("mythicSkills");
    }

    /**
     * 指定坐标是否经过此门
     * @param location 坐标
     * @return 如果经过
     */
    public boolean isAfter(Location location){
        if (!location.getWorld().getName().equalsIgnoreCase(worldName)) {
            return false;
        }
        return locations.contains(GameUtils.spawnLocationString(location));
    }

    public String getGateName() {
        return gateName;
    }

    public Location getTo() {
        return to;
    }

    public List<String> getMythicSkills() {
        return mythicSkills;
    }

    public boolean isInvalid() {
        return invalid;
    }
}
