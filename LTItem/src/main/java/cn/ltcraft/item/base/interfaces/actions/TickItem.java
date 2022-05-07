package cn.ltcraft.item.base.interfaces.actions;

import org.bukkit.entity.Player;

/**
 * Created by Angel、 on 2022/5/3 21:30
 * 每tick调用的物品需要实现这个接口
 * todo 添加更多的 onUse (使用) onAttack (玩家发动攻击) onXxxx (更多...)....
 */
public interface TickItem {
    /**
     * @param tick tick
     * @param player 玩家
     * @param invIndex 物品在玩家背包的索引
     */
    void doTick(long tick, Player player, int invIndex);
}
