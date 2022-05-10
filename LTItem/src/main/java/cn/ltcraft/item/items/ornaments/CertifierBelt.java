package cn.ltcraft.item.items.ornaments;

import cn.LTCraft.core.other.Temp;
import cn.ltcraft.item.base.interfaces.actions.AttackItem;
import cn.ltcraft.item.base.interfaces.actions.CombustItem;
import cn.ltcraft.item.base.interfaces.actions.DamageItem;
import cn.ltcraft.item.base.interfaces.actions.TickItem;
import cn.ltcraft.item.items.Ornament;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * 证明者腰带
 * Created by Angel、 on 2022/5/11 1:04
 */
public class CertifierBelt extends Ornament implements TickItem, DamageItem, AttackItem {
    public CertifierBelt(MemoryConfiguration configuration) {
        super(configuration);
    }

    /**
     * 脱离战斗10s赐予玩家药水
     * @param tick tick
     * @param player 玩家
     * @param invIndex 物品在玩家背包的索引
     */
    @Override
    public void doTick(long tick, Player player, int invIndex) {
        Long last = Temp.lastBattleTime.getOrDefault(player, 0L);
        if (tick - last > 20 * 10){
            if (player.hasPotionEffect(PotionEffectType.SPEED)){
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10, 1));
            }
        }
    }

    /**
     * 进入战斗取消药水
     * @param event 受伤的事件
     * @param player 玩家
     * @param invIndex 物品在玩家背包的索引
     */
    @Override
    public void onDamage(EntityDamageEvent event, Player player, int invIndex) {
        if (player.getPotionEffect(PotionEffectType.SPEED).getAmplifier() == 1)
            player.removePotionEffect(PotionEffectType.SPEED);
    }


    /**
     * 进入战斗取消药水
     * @param event 攻击的事件
     * @param player 玩家
     * @param invIndex 物品在玩家背包的索引
     */
    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player player, int invIndex) {
        if (player.getPotionEffect(PotionEffectType.SPEED).getAmplifier() == 1)
            player.removePotionEffect(PotionEffectType.SPEED);
    }
}
