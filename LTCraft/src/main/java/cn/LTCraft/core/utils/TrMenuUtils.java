package cn.LTCraft.core.utils;

import cn.LTCraft.core.entityClass.PlayerConfig;
import org.bukkit.entity.Player;

/**
 * Created by Angel、 on 2022/3/21 23:26
 */
public class TrMenuUtils {
    private static TrMenuUtils instance = null;

    public synchronized static TrMenuUtils getInstance() {
        if (instance == null){
            instance = new TrMenuUtils();
        }
        return instance;
    }

    /**
     * 是否拥有指定数量的金币
     * @param player 玩家
     * @param quantity 金币
     * @return 是否满足
     */
    public boolean hasGoldCoins(Player player, double quantity){
        return PlayerConfig.getPlayerConfig(player).getPlayerInfo().getGold() >= quantity;
    }

    /**
     * @see PlayerUtils#hasBQTag(Player, String)
     */
    public boolean hasBQTag(Player player, String tag){
        return PlayerUtils.hasBQTag(player, tag);
    }
}
