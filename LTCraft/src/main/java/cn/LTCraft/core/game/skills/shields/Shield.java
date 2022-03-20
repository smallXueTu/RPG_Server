package cn.LTCraft.core.game.skills.shields;

import org.bukkit.event.entity.EntityDamageEvent;

public interface Shield {
    /**
     * 受伤处理方法
     * @param event 事件
     * @return 如果触发护盾
     */
    boolean block(EntityDamageEvent event);

    /**
     * 护盾 刷新 用于渲染护盾外表
     * @param tick tick
     * @return 返回false代表护盾结束
     */
    boolean doTick(long tick);
}
