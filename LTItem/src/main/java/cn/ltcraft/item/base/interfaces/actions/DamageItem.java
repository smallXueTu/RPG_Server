package cn.ltcraft.item.base.interfaces.actions;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * 每次受伤触发的物品
 * Created by Angel、 on 2022/5/7 17:03
 */
public interface DamageItem {
    /**
     * @param event 受伤的事件
     * @param player 玩家
     * @param invIndex 物品在玩家背包的索引
     */
    void onDamage(EntityDamageEvent event, Player player, int invIndex);
}
