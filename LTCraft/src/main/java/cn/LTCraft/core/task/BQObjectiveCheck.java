package cn.LTCraft.core.task;

import cn.LTCraft.core.Main;
import cn.LTCraft.core.game.more.tickEntity.TickEntity;
import cn.LTCraft.core.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Angel、 on 2022/4/27 9:29
 */
public class BQObjectiveCheck implements TickEntity {
    private static final Map<String, List<String>> lastObj = new HashMap<>();
    private static YamlConfiguration waypoint = null;
    private static BQObjectiveCheck instance = null;
    public synchronized static BQObjectiveCheck getInstance() {
        if (instance == null){
            instance = new BQObjectiveCheck();
        }
        return instance;
    }
    private BQObjectiveCheck(){
        File dir = new File(Main.getInstance().getDataFolder().getParentFile(), "DragonGPS");
        waypoint = YamlConfiguration.loadConfiguration(new File(dir, "waypoint.yml"));
        GlobalRefresh.addTickEntity(this);
    }

    public static Map<String, List<String>> getLastObj() {
        return lastObj;
    }
    @Override
    public boolean doTick(long tick) {
        /*
        BQ笔记
         */
        synchronized (lastObj) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                List<Objective> objectives = BetonQuest.getInstance().getPlayerObjectives(PlayerConverter.getID(player));
                if (lastObj.containsKey(player.getName())) {
                    List<String> list = lastObj.get(player.getName());
                    for (Objective playerObjective : objectives) {
                        if (!list.contains(playerObjective.getLabel())) {
                            String objName = playerObjective.getLabel().substring(playerObjective.getLabel().indexOf('.') + 1);
                            if (waypoint.contains(objName) && objName.startsWith("【主线")) {
                                if (PlayerUtils.hasBQTag(player, "default.自动导航"))
                                    Bukkit.getServer().dispatchCommand(player, "dgps to " + objName);
                            }
                        }
                    }
                }
                lastObj.put(player.getName(), new ArrayList<String>() {
                    {
                        for (Objective objective : objectives) {
                            add(objective.getLabel());
                        }
                    }
                });
            }
        }
        return true;
    }

    @Override
    public int getTickRate() {
        return 20;
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
