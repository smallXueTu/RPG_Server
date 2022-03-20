package cn.LTCraft.core.entityClass;

import cn.LTCraft.core.other.Temp;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;

public class Cooling implements Listener {
    /**
     * 随机TP
     */
    public static Map<String, Map<String, Long>> cooling = new HashMap<>();

    /**
     * 是否冷却
     * @param player 玩家
     * @param coolingGroup 冷却组
     * @return 是否冷却
     */
    public static boolean isCooling(Player player, String coolingGroup){
        if (cooling.get(player.getName()).get(coolingGroup) == null)return false;
        return cooling.get(player.getName()).get(coolingGroup) - System.currentTimeMillis() > 0;
    }

    /**
     * 冷却剩余时间
     * @param player 玩家
     * @param coolingGroup 冷却组
     * @return 冷却时间 秒
     */
    public static double getCooling(Player player, String coolingGroup){
        if (cooling.get(player.getName()).get(coolingGroup) == null)return 0d;
        return Math.max(cooling.get(player.getName()).get(coolingGroup) - System.currentTimeMillis(), 0d) / 1000;
    }

    /**
     * 冷却
     * @param player 玩家
     * @param coolingGroup 冷却组
     */
    public static void cooling(Player player, String coolingGroup, long time){
        cooling.get(player.getName()).put(coolingGroup, System.currentTimeMillis() + time * 1000);
    }
    public static void cooling(Player player, String coolingGroup, long time, String msg){
        cooling.get(player.getName()).put(coolingGroup, System.currentTimeMillis() + time * 1000);
        Temp.playerStates.get(player).add(new PlayerState(player, msg, () -> Cooling.getCooling(player, coolingGroup)));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        cooling.put(event.getPlayer().getName(), new HashMap<>());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        cooling.remove(event.getPlayer().getName());
    }
}
