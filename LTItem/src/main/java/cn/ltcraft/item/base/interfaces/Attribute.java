package cn.ltcraft.item.base.interfaces;

import cn.LTCraft.core.entityClass.Additional;
import cn.LTCraft.core.entityClass.RandomValue;
import cn.ltcraft.item.base.subAttrbute.PotionAttribute;
import org.bukkit.entity.Entity;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;

public interface Attribute extends Cloneable{
    enum Type{
        PVE,
        PVP,
        OTHER
    }
    /**
     * 获取伤害
     * @param entity 被攻击的实体
     * @return 伤害
     */
    RandomValue getDamage(Entity entity);
    RandomValue getDamage(Type type);
    void setDamage(Type type, RandomValue value);
    /**
     * 获取吸血百分比
     * @param entity 被攻击的实体
     * @return 百分比 0.00 - 1.00
     */
    double getSuckingBlood(Entity entity);
    double getSuckingBlood(Type type);
    void setSuckingBlood(Type type, double value);

    /**
     * 获取击飞高度
     * @param entity 被攻击的实体
     * @return 高度
     */
    double getStrikeFly(Entity entity);
    double getStrikeFly(Type type);
    void setStrikeFly(Type type, double value);

    /**
     * 获取击退距离
     * @param entity 被攻击的实体
     * @return 距离
     */
    double getRepel(Entity entity);
    double getRepel(Type type);
    void setRepel(Type type, double value);

    /**
     * 获取燃烧tick
     * @param entity 被攻击的实体
     * @return 燃烧
     */
    int getBurning(Entity entity);
    int getBurning(Type type);
    void setBurning(Type type, int value);

    /**
     * 是否触发闪电
     * @param entity 被攻击的实体
     * @return 是否触发雷击
     */
    double getLightning(Entity entity);
    double getLightning(Type type);
    void setLightning(Type type, double value);

    /**
     * 获取群回
     * @return 群回量
     */
    int getGroupGyrus();
    void setGroupGyrus(int value);

    /**
     * 获取穿甲
     * @return 穿甲
     */
    double getNailPiercing(Entity entity);
    double getNailPiercing(Type type);
    void setNailPiercing(Type type, double value);

    /**
     * 获取自身药水效果
     * @return 药水效果
     */
    Map<PotionEffectType, PotionAttribute> getSelfPotion(Entity entity);
    Map<PotionEffectType, PotionAttribute> getSelfPotion(Type type);
    void setSelfPotion(Type type, Map<PotionEffectType, PotionAttribute> value);

    /**
     * 获取受伤者药水效果
     * @return 药水效果
     */
    Map<PotionEffectType, PotionAttribute> getEntityPotion(Entity entity);
    Map<PotionEffectType, PotionAttribute> getEntityPotion(Type type);
    void setEntityPotion(Type type, Map<PotionEffectType, PotionAttribute> value);

    /**
     * 获取攻击恢复生命值
     * @return 生命值
     */
    int getAttackRecovery(Entity entity);
    int getAttackRecovery(Type type);
    void setAttackRecovery(Type type, int value);

    /**
     * 获取附加生命值
     * @return 生命值
     */
    int getHealthValue();
    void setHealthValue(int value);

    /**
     * 获取附加幸运值
     * @return 幸运值
     */
    int getLucky();
    void setLucky(int value);

    /**
     * 获取反伤
     * @return 反伤0.00-1.00
     */
    double getBackInjury();
    void setBackInjury(double value);

    /**
     * 获取免伤
     * @return 免伤 0.00-1.00
     */
    double getInjuryFree(Entity entity);
    double getInjuryFree(Type type);
    void setInjuryFree(Type type, double value);

    /**
     * 获取护甲
     * @return 护甲
     */
    int getArmorValue();
    void setArmorValue(int value);

    /**
     * 获取闪避
     * @return 护甲 0.00-1.00
     */
    double getDodge();
    void setDodge(double value);

    /**
     * 获取坚韧
     * 抗击退击飞控制
     * @return 坚韧 0.00-1.00
     */
    double getTenacity();
    void setTenacity(double value);

    /**
     * 获取速度附加
     * @return 附加 0.0+
     */
    double getSpeed();
    void setSpeed(double value);

    /**
     * 获取暴击
     * @param entity 被攻击的实体
     * @return 暴击
     */
    Additional getCritical(Entity entity);
    Additional getCritical(Type type);
    void setCritical(Type type, Additional additional);

    /**
     * 获取暴击率
     * @param entity 被攻击的实体
     * @return 暴击率
     */
    double getCriticalRate(Entity entity);
    double getCriticalRate(Type type);
    void setCriticalRate(Type type, double value);

    /**
     * 攻击触发的技能
     * @return 攻击触发的技能
     */
    Map<String, Double> getAttackSkill(Entity entity);
    Map<String, Double> getAttackSkill(Type type);
    void setAttackSkill(Type type, Map<String, Double> map);

    /**
     * 受伤触发的技能
     * @return 受伤触发的技能
     */
    Map<String, Double> getInjuredSkill(Entity entity);
    Map<String, Double> getInjuredSkill(Type type);
    void setInjuredSkill(Type type, Map<String, Double> map);

    /**
     * 药水效果
     * @return 药水效果
     */
    Map<PotionEffectType, PotionAttribute> getPotion();
    void setPotion(Map<PotionEffectType, PotionAttribute> map);

    /**
     * 增加属性
     * @param attribute 属性
     */
    void addAttribute(Attribute attribute);

    /**
     * 移除属性
     * @param attribute 属性
     */
    void removeAttribute(Attribute attribute);

    /**
     * 重置属性
     */
    void reset();
}
