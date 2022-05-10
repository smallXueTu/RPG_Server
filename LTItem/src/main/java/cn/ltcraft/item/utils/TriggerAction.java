package cn.ltcraft.item.utils;

import cn.ltcraft.item.base.interfaces.LTItem;
import cn.ltcraft.item.base.interfaces.actions.AttackItem;
import cn.ltcraft.item.base.interfaces.actions.CombustItem;
import cn.ltcraft.item.base.interfaces.actions.DamageItem;
import cn.ltcraft.item.objs.PlayerAttribute;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Created by Angel、 on 2022/5/7 14:57
 * 对于物品在玩家背包的索引
 * 0 == 手持
 * 1 == 副手
 * 以下全为包含
 * 2 - 5 == 盔甲
 * 6 - 12 == 饰品
 * @see PlayerAttribute#getLTItems()
 */
public class TriggerAction {
    /**
     * 触发一个受伤
     * @param player 玩家
     * @param event 事件主体
     */
    public static void onDamage(Player player, EntityDamageEvent event){
        onDamage(PlayerAttribute.getPlayerAttribute(player), event);
    }
    public static void onDamage(PlayerAttribute playerAttribute, EntityDamageEvent event){
        LTItem[] LTItems = playerAttribute.getLTItems();
        for (int i = 0; i < LTItems.length; i++) {
            LTItem ltItem = LTItems[i];
            if (ltItem instanceof DamageItem){
                ((DamageItem) ltItem).onDamage(event, playerAttribute.getOwner(), i);
            }
        }
    }
    /**
     * 触发一个攻击
     * @param player 玩家
     * @param event 事件主体
     */
    public static void onAttack(Player player, EntityDamageByEntityEvent event){
        onAttack(PlayerAttribute.getPlayerAttribute(player), event);
    }
    public static void onAttack(PlayerAttribute playerAttribute, EntityDamageByEntityEvent event){
        LTItem[] LTItems = playerAttribute.getLTItems();
        for (int i = 0; i < LTItems.length; i++) {
            LTItem ltItem = LTItems[i];
            if (ltItem instanceof AttackItem){
                ((AttackItem) ltItem).onAttack(event, playerAttribute.getOwner(), i);
            }
        }
    }
    /**
     * 触发一个燃烧
     * @param player 玩家
     * @param event 事件主体
     */
    public static void onCombust(Player player, EntityCombustEvent event){
        onCombust(PlayerAttribute.getPlayerAttribute(player), event);
    }
    public static void onCombust(PlayerAttribute playerAttribute, EntityCombustEvent event){
        LTItem[] LTItems = playerAttribute.getLTItems();
        for (int i = 0; i < LTItems.length; i++) {
            LTItem ltItem = LTItems[i];
            if (ltItem instanceof CombustItem){
                ((CombustItem) ltItem).onCombust(event, playerAttribute.getOwner(), i);
            }
        }
    }
}
