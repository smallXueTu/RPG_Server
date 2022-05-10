package cn.ltcraft.item.base.interfaces.actions;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * 每次发动攻击触发的物品
 * Created by Angel、 on 2022/5/11 1:09
 */
public interface AttackItem {
    /**
     * @param event 攻击的事件
     * @param player 玩家
     * @param invIndex 物品在玩家背包的索引
     */
    void onAttack(EntityDamageByEntityEvent event, Player player, int invIndex);
}
