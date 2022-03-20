package cn.LTCraft.core.game.skills;


import org.bukkit.entity.Entity;

public interface Skill {
    /**
     * 释放技能
     * @param entity 目标实体
     * @return 如果释放成功
     */
    boolean cast(Entity entity);
    /**
     * 释放技能
     * @return 如果释放成功
     */
    boolean cast();
}
