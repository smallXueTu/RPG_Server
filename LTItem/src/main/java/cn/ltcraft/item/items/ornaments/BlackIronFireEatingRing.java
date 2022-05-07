package cn.ltcraft.item.items.ornaments;

import cn.ltcraft.item.base.interfaces.actions.CombustItem;
import cn.ltcraft.item.base.interfaces.actions.DamageItem;
import cn.ltcraft.item.items.Ornament;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * 黑铁食火之戒
 * Created by Angel、 on 2022/5/7 17:21
 */
public class BlackIronFireEatingRing extends Ornament implements DamageItem, CombustItem {
    public BlackIronFireEatingRing(MemoryConfiguration configuration) {
        super(configuration);
    }

    /**
     * 阻止佩戴黑铁食火之戒的玩家受到伤害 < 2的火烧伤害
     * @param event 受伤的事件
     * @param player 玩家
     * @param invIndex 物品在玩家背包的索引
     */
    @Override
    public void onDamage(EntityDamageEvent event, Player player, int invIndex) {
        if ((event.getCause() == EntityDamageEvent.DamageCause.FIRE || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) && event.getDamage() < 2){
            event.setCancelled(true);
            player.setFireTicks(0);
        }
    }

    /**
     * 阻止佩戴黑铁食火之戒的玩家燃烧
     * @param event 受伤的事件
     * @param player 玩家
     * @param invIndex 物品在玩家背包的索引
     */
    @Override
    public void onCombust(EntityCombustEvent event, Player player, int invIndex) {
        event.setCancelled(true);
    }
}
