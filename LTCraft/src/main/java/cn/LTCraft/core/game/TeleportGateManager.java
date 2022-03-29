package cn.LTCraft.core.game;

import cn.LTCraft.core.Config;
import cn.LTCraft.core.entityClass.TeleportGate;
import cn.LTCraft.core.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Angel、 on 2022/3/27 16:20
 */
public class TeleportGateManager {
    private static TeleportGateManager instance = null;
    /**
     * prevent 阻止传送
     */
    private Location prevent = new Location(Bukkit.getWorld("world"), 0, 0, 0);

    public static TeleportGateManager getInstance() {
        if (instance == null){
            instance = new TeleportGateManager();
        }
        return instance;
    }

    private final List<TeleportGate> gates = new ArrayList();

    private TeleportGateManager(){

    }

    public void init(){
        Set<String> keys = Config.getInstance().getGateYaml().getKeys(false);
        for (String key : keys) {
            TeleportGate teleportGate = new TeleportGate(key);
            if (!teleportGate.isInvalid()) {
                gates.add(teleportGate);
            }
        }
    }

    public void reload(){
        gates.clear();
        init();
    }

    public List<TeleportGate> getGates() {
        return gates;
    }

    /**
     * 玩家经过传送门方块
     * @param player 玩家
     */
    public void onAfter(Player player){
        Location location = player.getLocation();
        for (TeleportGate gate : gates) {
            if (gate.isAfter(location)){
                Location to = gate.getTo().clone();
                if (!to.getWorld().getName().equals(prevent.getWorld().getName()) || to.distance(prevent) > 3) {
                    to.setYaw(player.getLocation().getYaw());
                    to.setPitch(player.getLocation().getPitch());
                    player.teleport(to, PlayerTeleportEvent.TeleportCause.ENDER_PEARL);
                }
                List<String> list = gate.getMythicSkills();
                for (String s : list) {
                    PlayerUtils.castMMSkill(player, s);
                }
            }
        }
    }
}
