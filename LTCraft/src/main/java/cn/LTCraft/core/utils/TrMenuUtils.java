package cn.LTCraft.core.utils;

import cn.LTCraft.core.entityClass.PlayerConfig;
import org.bukkit.entity.Player;

/**
 * Created by Angel、 on 2022/3/21 23:26
 */
public class TrMenuUtils {
    private static TrMenuUtils instance = null;

    public static TrMenuUtils getInstance() {
        if (instance == null){
            instance = new TrMenuUtils();
        }
        return instance;
    }
    public boolean test(){
        System.out.println("This is test...");
        return true;
    }
    public boolean hasGoldCoins(Player player, double quantity){
        return PlayerConfig.getPlayerConfig(player).getPlayerInfo().getGold() >= quantity;
    }
}
