package cn.ltcraft.item.base.interfaces.actions;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityCombustEvent;

/**
 * 玩家燃烧触发的物品
 * Created by Angel、 on 2022/5/7 17:30
 */
public interface CombustItem {
    /**
     * @param event 燃烧的事件
     * @param player 玩家
     * @param invIndex 物品在玩家背包的索引
     */
    void onCombust(EntityCombustEvent event, Player player, int invIndex);
}
