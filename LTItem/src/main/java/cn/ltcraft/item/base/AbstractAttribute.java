package cn.ltcraft.item.base;

import cn.LTCraft.core.entityClass.Additional;
import cn.LTCraft.core.entityClass.RandomValue;
import cn.ltcraft.item.base.interfaces.Attribute;
import cn.ltcraft.item.base.subAttrbute.PotionAttribute;
import cn.ltcraft.item.base.subAttrbute.PotionMap;
import cn.ltcraft.item.base.subAttrbute.SkillMap;
import cn.ltcraft.item.items.Armor;
import io.lumine.xikage.mythicmobs.MythicMobs;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 抽象属性
 * 目前实现Attribute接口的类只有这个
 * 属于Attribute的必定可强转为AbstractAttribute
 */
public abstract class AbstractAttribute implements cn.ltcraft.item.base.interfaces.Attribute {
    public final static RandomValue negative = new RandomValue(-1);
    public final static Additional negativeCritical = new Additional();
    /**
     * 伤害
     */
    protected RandomValue PVEDamage = new RandomValue(0);
    protected RandomValue PVPDamage = new RandomValue(0);
    /**
     * 吸血
     */
    protected double PVESuckingBlood = 0;
    protected double PVPSuckingBlood = 0;
    /**
     * 燃烧
     */
    protected int PVEBurning = 0;
    protected int PVPBurning = 0;
    /**
     * 雷击几率
     */
    protected double PVPLightning = 0;
    protected double PVELightning = 0;
    /**
     * 击飞
     */
    protected double PVPStrikeFly = 0;
    protected double PVEStrikeFly = 0;
    /**
     * 击退
     */
    protected double PVPRepel = 0;
    protected double PVERepel = 0;
    /**
     * 攻击恢复
     */
    protected int PVEAttackRecovery = 0;
    protected int PVPAttackRecovery = 0;
    /**
     * 穿甲
     */
    protected double PVENailPiercing = 0;
    protected double PVPNailPiercing = 0;
    /**
     * 免伤
     */
    protected double PVEInjuryFree = 0;
    protected double PVPInjuryFree = 0;
    /**
     * 暴击
     * TODO 删除原版暴击 增加暴击效果
     */
    protected Additional PVECritical = new Additional();
    protected Additional PVPCritical = new Additional();
    /**
     * 暴击率
     */
    protected double PVECriticalRate = 0;
    protected double PVPCriticalRate = 0;
    /**
     * 群回
     */
    protected int groupGyrus = 0;
    /**
     * 反伤
     */
    protected double backInjury = 0;
    /**
     * 闪避
     */
    protected double dodge = 0;
    /**
     * 韧性
     */
    protected double tenacity = 0;
    /**
     * 速度
     */
    protected double speed = 0;
    /**
     * 护甲
     */
    protected int armor = 0;
    /**
     * 幸运
     */
    protected int lucky = 0;
    /**
     * 附加生命值
     */
    protected int healthValue = 0;
    /**
     * 攻击触发自身药水
     */
    protected Map<PotionEffectType, PotionAttribute> PVESelfPotion = new PotionMap<>();
    protected Map<PotionEffectType, PotionAttribute> PVPSelfPotion = new PotionMap<>();
    /**
     * 攻击触发对方药水
     */
    protected Map<PotionEffectType, PotionAttribute> PVEEntityPotion = new PotionMap<>();
    protected Map<PotionEffectType, PotionAttribute> PVPEntityPotion = new PotionMap<>();
    /**
     * 攻击触发技能
     */
    protected Map<String, Double> PVEAttackSkill = new SkillMap<>();
    protected Map<String, Double> PVPAttackSkill = new SkillMap<>();
    /**
     * 受伤触发技能
     */
    protected Map<String, Double> PVEInjuredSkill = new SkillMap<>();
    protected Map<String, Double> PVPInjuredSkill = new SkillMap<>();
    /**
     * 药水效果
     */
    protected Map<PotionEffectType, PotionAttribute> potion = new PotionMap<>();

    /**
     * 获取伤害
     * @param entity 被攻击的实体
     * @return 伤害
     */
    @Override
    public RandomValue getDamage(Entity entity) {
        if (MythicMobs.inst().getMobManager().isActiveMob(entity.getUniqueId())){
            return PVEDamage;
        }else if (entity instanceof Player){
            return PVPDamage;
        }else {
            return negative;
        }
    }

    @Override
    public RandomValue getDamage(Type type) {
        if (type == Type.PVE){
            return PVEDamage;
        }else if (type == Type.PVP){
            return PVPDamage;
        }else {
            return negative;
        }
    }

    @Override
    public void setDamage(Type type, RandomValue value) {
        if (type == Type.PVE){
            PVEDamage = value;
        }else if (type == Type.PVP){
            PVPDamage = value;
        }
    }

    /**
     * 获取吸血百分比
     * @param entity 被攻击的实体
     * @return 百分比 0.00 - 1.00
     */
    @Override
    public double getSuckingBlood(Entity entity) {
        if (MythicMobs.inst().getMobManager().isActiveMob(entity.getUniqueId())){
            return PVESuckingBlood / 100;
        }else if (entity instanceof Player){
            return PVPSuckingBlood / 100;
        }else {
            return 0;
        }
    }

    @Override
    public double getSuckingBlood(Type type) {
        if (type == Type.PVE){
            return PVESuckingBlood / 100;
        }else if (type == Type.PVP){
            return PVPSuckingBlood / 100;
        }else {
            return 0;
        }
    }

    @Override
    public void setSuckingBlood(Type type, double value) {
        if (type == Type.PVE){
            PVESuckingBlood = value * 100;
        }else if (type == Type.PVP){
            PVPSuckingBlood = value * 100;
        }
    }

    /**
     * 获取击飞高度
     * @param entity 被攻击的实体
     * @return 高度
     */
    @Override
    public double getStrikeFly(Entity entity) {
        if (MythicMobs.inst().getMobManager().isActiveMob(entity.getUniqueId())){
            return PVEStrikeFly;
        }else if (entity instanceof Player){
            return PVPStrikeFly;
        }else {
            return 0;
        }
    }

    @Override
    public double getStrikeFly(Type type) {
        if (type == Type.PVE){
            return PVEStrikeFly;
        }else if (type == Type.PVP){
            return PVPStrikeFly;
        }else {
            return 0;
        }
    }

    @Override
    public void setStrikeFly(Type type, double value) {
        if (type == Type.PVE){
            PVEStrikeFly = value;
        }else if (type == Type.PVP){
            PVPStrikeFly = value;
        }
    }

    /**
     * 获取击退距离
     * @param entity 被攻击的实体
     * @return 距离
     */
    @Override
    public double getRepel(Entity entity) {
        if (MythicMobs.inst().getMobManager().isActiveMob(entity.getUniqueId())){
            return PVERepel;
        }else if (entity instanceof Player){
            return PVPRepel;
        }else {
            return 0;
        }
    }

    @Override
    public double getRepel(Type type) {
        if (type == Type.PVE){
            return PVERepel;
        }else if (type == Type.PVP){
            return PVPRepel;
        }else {
            return 0;
        }
    }

    @Override
    public Additional getCritical(Entity entity) {
        if (MythicMobs.inst().getMobManager().isActiveMob(entity.getUniqueId())){
            return PVECritical;
        }else if (entity instanceof Player){
            return PVPCritical;
        }else {
            return negativeCritical;
        }
    }

    @Override
    public Additional getCritical(Type type) {
        if (type == Type.PVE){
            return PVECritical;
        }else if (type == Type.PVP){
            return PVPCritical;
        }else {
            return negativeCritical;
        }
    }

    @Override
    public void setCritical(Type type, Additional additional) {
        if (type == Type.PVE){
            PVECritical = additional;
        }else if (type == Type.PVP){
            PVPCritical = additional;
        }
    }

    @Override
    public double getCriticalRate(Entity entity) {
        if (MythicMobs.inst().getMobManager().isActiveMob(entity.getUniqueId())){
            return PVECriticalRate / 100;
        }else if (entity instanceof Player){
            return PVPCriticalRate / 100;
        }else {
            return 0;
        }
    }

    @Override
    public double getCriticalRate(Type type) {
        if (type == Type.PVE){
            return PVECriticalRate / 100;
        }else if (type == Type.PVP){
            return PVPCriticalRate / 100;
        }else {
            return 0;
        }
    }

    @Override
    public void setCriticalRate(Type type, double additionalRate) {
        if (type == Type.PVE){
            PVECriticalRate = additionalRate * 100;
        }else if (type == Type.PVP){
            PVPCriticalRate = additionalRate * 100;
        }
    }

    @Override
    public void setRepel(Type type, double value) {
        if (type == Type.PVE){
            PVERepel = value;
        }else if (type == Type.PVP){
            PVPRepel = value;
        }
    }

    /**
     * 获取燃烧tick
     * @param entity 被攻击的实体
     * @return 燃烧
     */
    @Override
    public int getBurning(Entity entity) {
        if (MythicMobs.inst().getMobManager().isActiveMob(entity.getUniqueId())){
            return PVEBurning;
        }else if (entity instanceof Player){
            return PVPBurning;
        }else {
            return 0;
        }
    }

    @Override
    public int getBurning(Type type) {
        if (type == Type.PVE){
            return PVEBurning;
        }else if (type == Type.PVP){
            return PVPBurning;
        }else {
            return 0;
        }
    }

    @Override
    public void setBurning(Type type, int value) {
        if (type == Type.PVE){
            PVEBurning = value;
        }else if (type == Type.PVP){
            PVPBurning = value;
        }
    }

    /**
     * 是否触发闪电
     * @param entity 被攻击的实体
     * @return 是否触发雷击
     */
    @Override
    public double getLightning(Entity entity) {
        if (MythicMobs.inst().getMobManager().isActiveMob(entity.getUniqueId())){
            return PVELightning / 100;
        }else if (entity instanceof Player){
            return PVPLightning / 100;
        }else return 0;
    }

    @Override
    public double getLightning(Type type) {
        if (type == Type.PVE){
            return PVELightning / 100;
        }else if (type == Type.PVP){
            return PVPLightning / 100;
        }else return 0;
    }

    @Override
    public void setLightning(Type type, double value) {
        if (type == Type.PVE){
            PVELightning = value * 100;
        }else if (type == Type.PVP){
            PVPLightning = value * 100;
        }
    }

    /**
     * 获取群回
     * @return 群回量
     */
    @Override
    public int getGroupGyrus() {
        return groupGyrus;
    }

    @Override
    public void setGroupGyrus(int groupGyrus) {
        this.groupGyrus = groupGyrus;
    }

    /**
     * 获取穿甲
     * @return 穿甲
     */
    @Override
    public double getNailPiercing(Entity entity) {
        if (MythicMobs.inst().getMobManager().isActiveMob(entity.getUniqueId())){
            return PVENailPiercing;
        }else if (entity instanceof Player){
            return PVPNailPiercing;
        }else {
            return 0;
        }
    }

    @Override
    public double getNailPiercing(Type type) {
        if (type == Type.PVE){
            return PVENailPiercing;
        }else if (type == Type.PVP){
            return PVPNailPiercing;
        }else {
            return 0;
        }
    }

    @Override
    public void setNailPiercing(Type type, double value) {
        if (type == Type.PVE){
            PVENailPiercing = value;
        }else if (type == Type.PVP){
            PVPNailPiercing = value;
        }
    }

    /**
     * 获取自身药水效果
     * @return 药水效果
     */
    @Override
    public Map<PotionEffectType, PotionAttribute> getSelfPotion(Entity entity) {
        if (MythicMobs.inst().getMobManager().isActiveMob(entity.getUniqueId())){
            return PVESelfPotion;
        }else if (entity instanceof Player){
            return PVPSelfPotion;
        }else {
            return new PotionMap<>();
        }
    }

    @Override
    public Map<PotionEffectType, PotionAttribute> getSelfPotion(Type type) {
        if (type == Type.PVE){
            return PVESelfPotion;
        }else if (type == Type.PVP){
            return PVPSelfPotion;
        }else {
            return new PotionMap<>();
        }
    }

    @Override
    public void setSelfPotion(Type type, Map<PotionEffectType, PotionAttribute> value) {
        if (type == Type.PVE){
            PVESelfPotion = value;
        }else if (type == Type.PVP){
            PVPSelfPotion = value;
        }
    }

    /**
     * 获取受伤者药水效果
     * @return 药水效果
     */
    @Override
    public Map<PotionEffectType, PotionAttribute> getEntityPotion(Entity entity) {
        if (MythicMobs.inst().getMobManager().isActiveMob(entity.getUniqueId())){
            return PVEEntityPotion;
        }else if (entity instanceof Player){
            return PVPEntityPotion;
        }else {
            return new PotionMap<>();
        }
    }

    @Override
    public Map<PotionEffectType, PotionAttribute> getEntityPotion(Type type) {
        if (type == Type.PVE){
            return PVEEntityPotion;
        }else if (type == Type.PVP){
            return PVPEntityPotion;
        }else {
            return new PotionMap<>();
        }
    }

    @Override
    public void setEntityPotion(Type type, Map<PotionEffectType, PotionAttribute> value) {
        if (type == Type.PVE){
            PVEEntityPotion = value;
        }else if (type == Type.PVP){
            PVPEntityPotion = value;
        }
    }


    @Override
    public Map<PotionEffectType, PotionAttribute> getPotion() {
        return potion;
    }

    public void setPotion(Map<PotionEffectType, PotionAttribute> potion) {
        this.potion = potion;
    }

    public Map<String, Double> getAttackSkill(Entity entity) {
        if (MythicMobs.inst().getMobManager().isActiveMob(entity.getUniqueId())){
            return PVEAttackSkill;
        }else if (entity instanceof Player){
            return PVPAttackSkill;
        }else {
            return new SkillMap<>();
        }
    }

    public Map<String, Double> getAttackSkill(Type type) {
        if (type == Type.PVE){
            return PVEAttackSkill;
        }else if (type == Type.PVP){
            return PVPAttackSkill;
        }else {
            return new SkillMap<>();
        }
    }

    @Override
    public void setAttackSkill(Type type, Map<String, Double> map) {
        if (type == Type.PVE){
            PVEAttackSkill = map;
        }else if(type == Type.PVP){
            PVPAttackSkill = map;
        }
    }

    public Map<String, Double> getInjuredSkill(Entity entity) {
        if (MythicMobs.inst().getMobManager().isActiveMob(entity.getUniqueId())){
            return PVEInjuredSkill;
        }else if (entity instanceof Player){
            return PVPInjuredSkill;
        }else {
            return new SkillMap<>();
        }
    }

    public Map<String, Double> getInjuredSkill(Type type) {
        if (type == Type.PVE){
            return PVEInjuredSkill;
        }else if (type == Type.PVP){
            return PVPInjuredSkill;
        }else {
            return new SkillMap<>();
        }
    }

    @Override
    public void setInjuredSkill(Type type, Map<String, Double> map) {
        if (type == Type.PVE){
            PVEInjuredSkill = map;
        }else if(type == Type.PVP){
            PVPInjuredSkill = map;
        }
    }

    /**
     * 获取攻击恢复生命值
     * @return 生命值
     */
    @Override
    public int getAttackRecovery(Entity entity) {
        if (MythicMobs.inst().getMobManager().isActiveMob(entity.getUniqueId())){
            return PVEAttackRecovery;
        }else if (entity instanceof Player){
            return PVPAttackRecovery;
        }else {
            return 0;
        }
    }

    @Override
    public int getAttackRecovery(Type type) {
        if (type == Type.PVE){
            return PVEAttackRecovery;
        }else if (type == Type.PVP){
            return PVPAttackRecovery;
        }else {
            return 0;
        }
    }

    @Override
    public void setAttackRecovery(Type type, int value) {
        if (type == Type.PVE){
            PVEAttackRecovery = value;
        }else if (type == Type.PVP){
            PVPAttackRecovery = value;
        }
    }

    /**
     * 获取附加生命值
     * @return 生命值
     */
    @Override
    public int getHealthValue() {
        return healthValue;
    }

    @Override
    public void setHealthValue(int value) {
        healthValue = value;
    }

    /**
     * 获取附加幸运值
     * @return 幸运值
     */
    @Override
    public int getLucky() {
        return lucky;
    }

    @Override
    public void setLucky(int lucky) {
        this.lucky = lucky;
    }

    /**
     * 获取反伤
     * @return 反伤0.00-1.00
     */
    @Override
    public double getBackInjury() {
        return backInjury;
    }

    @Override
    public void setBackInjury(double value) {
        this.backInjury = value;
    }

    /**
     * 获取免伤
     * @return 免伤 0.00-1.00
     */
    @Override
    public double getInjuryFree(Entity entity) {
        if (MythicMobs.inst().getMobManager().isActiveMob(entity.getUniqueId())){
            return PVEInjuryFree;
        }else if (entity instanceof Player){
            return PVPInjuryFree;
        }else {
            return 0;
        }
    }

    @Override
    public double getInjuryFree(Type type) {
        if (type == Type.PVE){
            return PVEInjuryFree;
        }else if (type == Type.PVP){
            return PVPInjuryFree;
        }else {
            return 0;
        }
    }

    @Override
    public void setInjuryFree(Type type, double value) {
        if (type == Type.PVE){
            PVEInjuryFree = value;
        }else if (type == Type.PVP){
            PVPInjuryFree = value;
        }
    }

    /**
     * 获取护甲
     * @return 护甲
     */
    @Override
    public int getArmorValue() {
        return armor;
    }

    @Override
    public void setArmorValue(int value) {
        armor = value;
    }

    /**
     * 获取闪避
     * @return 护甲 0.00-1.00
     */
    @Override
    public double getDodge() {
        return dodge / 100;
    }

    @Override
    public void setDodge(double value) {
        dodge = value * 100;
    }

    /**
     * 获取坚韧
     * 抗击退击飞控制
     * @return 坚韧 0.00-1.00
     */
    @Override
    public double getTenacity() {
        return tenacity;
    }

    @Override
    public void setTenacity(double value) {
        tenacity = value;
    }

    /**
     * 获取速度附加
     * @return 附加 0.00-1.00
     */
    @Override
    public double getSpeed() {
        return speed;
    }

    /**
     * 设置速度
     * @param speed 设置速度
     */
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void addAttribute(cn.ltcraft.item.base.interfaces.Attribute attribute){
        //伤害
        RandomValue PVEAttackDamage = new RandomValue(getDamage(Type.PVE).getMin() + attribute.getDamage(Type.PVE).getMin(), getDamage(Type.PVE).getMax() + attribute.getDamage(Type.PVE).getMax());
        setDamage(Type.PVE, PVEAttackDamage);
        RandomValue PVPAttackDamage = new RandomValue(getDamage(Type.PVP).getMin() + attribute.getDamage(Type.PVP).getMin(), getDamage(Type.PVP).getMax() + attribute.getDamage(Type.PVP).getMax());
        setDamage(Type.PVP, PVPAttackDamage);
        //吸血
        setSuckingBlood(Type.PVE, getSuckingBlood(Type.PVE) + attribute.getSuckingBlood(Type.PVE));
        setSuckingBlood(Type.PVP, getSuckingBlood(Type.PVP) + attribute.getSuckingBlood(Type.PVP));
        //击飞
        setStrikeFly(Type.PVE, getStrikeFly(Type.PVE) + attribute.getStrikeFly(Type.PVE));
        setStrikeFly(Type.PVP, getStrikeFly(Type.PVP) + attribute.getStrikeFly(Type.PVP));
        //击退
        setRepel(Type.PVE, getRepel(Type.PVE) + attribute.getRepel(Type.PVE));
        setRepel(Type.PVP, getRepel(Type.PVP) + attribute.getRepel(Type.PVP));
        //燃烧
        setBurning(Type.PVE, getBurning(Type.PVE) + attribute.getBurning(Type.PVE));
        setBurning(Type.PVP, getBurning(Type.PVP) + attribute.getBurning(Type.PVP));
        //雷击
        setLightning(Type.PVE, getLightning(Type.PVE) + attribute.getLightning(Type.PVE));
        setLightning(Type.PVP, getLightning(Type.PVP) + attribute.getLightning(Type.PVP));
        //群回
        setGroupGyrus(getGroupGyrus() + attribute.getGroupGyrus());
        //穿甲
        setNailPiercing(Type.PVE, getNailPiercing(Type.PVE) + attribute.getNailPiercing(Type.PVE));
        setNailPiercing(Type.PVP, getNailPiercing(Type.PVP) + attribute.getNailPiercing(Type.PVP));
        //自身药水
        getSelfPotion(Type.PVE).putAll(attribute.getSelfPotion(Type.PVE));
        getSelfPotion(Type.PVP).putAll(attribute.getSelfPotion(Type.PVP));
        //暴击
        getCritical(Type.PVE).addAdditional(attribute.getCritical(Type.PVE));
        getCritical(Type.PVP).addAdditional(attribute.getCritical(Type.PVP));
        //暴击率
        setCriticalRate(Type.PVE, getCriticalRate(Type.PVE) + attribute.getCriticalRate(Type.PVE));
        setCriticalRate(Type.PVP, getCriticalRate(Type.PVP) + attribute.getCriticalRate(Type.PVP));
        //目标药水
        getEntityPotion(Type.PVE).putAll(attribute.getEntityPotion(Type.PVE));
        getEntityPotion(Type.PVP).putAll(attribute.getEntityPotion(Type.PVP));
        getPotion().putAll(attribute.getPotion());
        //攻击触发技能
        SkillMap<String, Double> PVEAttackSkillMap = new SkillMap<>();
        getAttackSkill(Type.PVE).forEach((s, d) -> {
            if (PVEAttackSkillMap.containsKey(s)){
                PVEAttackSkillMap.put(s, d + PVEAttackSkillMap.get(s));
            }else {
                PVEAttackSkillMap.put(s, d);
            }
        });
        attribute.getAttackSkill(Type.PVE).forEach((s, d) -> {
            if (PVEAttackSkillMap.containsKey(s)){
                PVEAttackSkillMap.put(s, d + PVEAttackSkillMap.get(s));
            }else {
                PVEAttackSkillMap.put(s, d);
            }
        });
        setAttackSkill(Type.PVE, PVEAttackSkillMap);
        SkillMap<String, Double> PVPAttackSkillMap = new SkillMap<>();
        getAttackSkill(Type.PVP).forEach((s, d) -> {
            if (PVPAttackSkillMap.containsKey(s)){
                PVPAttackSkillMap.put(s, d + PVPAttackSkillMap.get(s));
            }else {
                PVPAttackSkillMap.put(s, d);
            }
        });
        attribute.getAttackSkill(Type.PVP).forEach((s, d) -> {
            if (PVPAttackSkillMap.containsKey(s)){
                PVPAttackSkillMap.put(s, d + PVPAttackSkillMap.get(s));
            }else {
                PVPAttackSkillMap.put(s, d);
            }
        });
        setAttackSkill(Type.PVP, PVPAttackSkillMap);
        SkillMap<String, Double> PVEInjuredSkillMap = new SkillMap<>();
        getInjuredSkill(Type.PVE).forEach((s, d) -> {
            if (PVEInjuredSkillMap.containsKey(s)){
                PVEInjuredSkillMap.put(s, d + PVEInjuredSkillMap.get(s));
            }else {
                PVEInjuredSkillMap.put(s, d);
            }
        });
        attribute.getInjuredSkill(Type.PVE).forEach((s, d) -> {
            if (PVEInjuredSkillMap.containsKey(s)){
                PVEInjuredSkillMap.put(s, d + PVEInjuredSkillMap.get(s));
            }else {
                PVEInjuredSkillMap.put(s, d);
            }
        });
        setInjuredSkill(Type.PVE, PVEInjuredSkillMap);
        SkillMap<String, Double> PVPInjuredSkillMap = new SkillMap<>();
        getInjuredSkill(Type.PVP).forEach((s, d) -> {
            if (PVPInjuredSkillMap.containsKey(s)){
                PVPInjuredSkillMap.put(s, d + PVPInjuredSkillMap.get(s));
            }else {
                PVPInjuredSkillMap.put(s, d);
            }
        });
        attribute.getInjuredSkill(Type.PVP).forEach((s, d) -> {
            if (PVPInjuredSkillMap.containsKey(s)){
                PVPInjuredSkillMap.put(s, d + PVPInjuredSkillMap.get(s));
            }else {
                PVPInjuredSkillMap.put(s, d);
            }
        });
        setInjuredSkill(Type.PVP, PVPInjuredSkillMap);
        //恢复生命值
        setAttackRecovery(Type.PVE, getAttackRecovery(Type.PVE) + attribute.getAttackRecovery(Type.PVE));
        setAttackRecovery(Type.PVP, getAttackRecovery(Type.PVP) + attribute.getAttackRecovery(Type.PVP));
        //附加生命值
        setHealthValue(getHealthValue() + attribute.getHealthValue());
        //幸运
        setLucky(getLucky() + attribute.getLucky());
        //反伤
        setBackInjury(getBackInjury() + attribute.getBackInjury());
        //免伤
        setInjuryFree(Type.PVE, getInjuryFree(Type.PVE) + attribute.getInjuryFree(Type.PVE));
        setInjuryFree(Type.PVP, getInjuryFree(Type.PVP) + attribute.getInjuryFree(Type.PVP));
        //护甲
        setArmorValue(getArmorValue() + attribute.getArmorValue());
        //miss
        setDodge(getDodge() + attribute.getDodge());
        //闪避
        setTenacity(getTenacity() + attribute.getTenacity());
        //速度
        setSpeed(getSpeed() + attribute.getSpeed());
    }

    public void removeAttribute(Attribute attribute){
        //伤害
        RandomValue PVEAttackDamage = new RandomValue(getDamage(Type.PVE).getMin() - attribute.getDamage(Type.PVE).getMin(), getDamage(Type.PVE).getMax() - attribute.getDamage(Type.PVE).getMax());
        setDamage(Type.PVE, PVEAttackDamage);
        RandomValue PVPAttackDamage = new RandomValue(getDamage(Type.PVP).getMin() - attribute.getDamage(Type.PVP).getMin(), getDamage(Type.PVP).getMax() - attribute.getDamage(Type.PVP).getMax());
        setDamage(Type.PVP, PVPAttackDamage);
        //吸血
        setSuckingBlood(Type.PVE, getSuckingBlood(Type.PVE) - attribute.getSuckingBlood(Type.PVE));
        setSuckingBlood(Type.PVP, getSuckingBlood(Type.PVP) - attribute.getSuckingBlood(Type.PVP));
        //击飞
        setStrikeFly(Type.PVE, getStrikeFly(Type.PVE) - attribute.getStrikeFly(Type.PVE));
        setStrikeFly(Type.PVP, getStrikeFly(Type.PVP) - attribute.getStrikeFly(Type.PVP));
        //击退
        setRepel(Type.PVE, getRepel(Type.PVE) - attribute.getRepel(Type.PVE));
        setRepel(Type.PVP, getRepel(Type.PVP) - attribute.getRepel(Type.PVP));
        //燃烧
        setBurning(Type.PVE, getBurning(Type.PVE) - attribute.getBurning(Type.PVE));
        setBurning(Type.PVP, getBurning(Type.PVP) - attribute.getBurning(Type.PVP));
        //雷击
        setLightning(Type.PVE, getLightning(Type.PVE) - attribute.getLightning(Type.PVE));
        setLightning(Type.PVP, getLightning(Type.PVP) - attribute.getLightning(Type.PVP));
        //群回
        setGroupGyrus(getGroupGyrus() - attribute.getGroupGyrus());
        //穿甲
        setNailPiercing(Type.PVE, getNailPiercing(Type.PVE) - attribute.getNailPiercing(Type.PVE));
        setNailPiercing(Type.PVP, getNailPiercing(Type.PVP) - attribute.getNailPiercing(Type.PVP));
        //暴击
        getCritical(Type.PVE).removeAdditional(attribute.getCritical(Type.PVE));
        getCritical(Type.PVP).removeAdditional(attribute.getCritical(Type.PVP));
        //暴击率
        setCriticalRate(Type.PVE, getCriticalRate(Type.PVE) - attribute.getCriticalRate(Type.PVE));
        setCriticalRate(Type.PVP, getCriticalRate(Type.PVP) - attribute.getCriticalRate(Type.PVP));
        //自身药水
        getSelfPotion(Type.PVE).keySet().removeIf(k -> attribute.getSelfPotion(Type.PVE).containsKey(k));
        getSelfPotion(Type.PVP).keySet().removeIf(k -> attribute.getSelfPotion(Type.PVP).containsKey(k));
        //目标药水
        getEntityPotion(Type.PVE).keySet().removeIf(k -> attribute.getEntityPotion(Type.PVE).containsKey(k));
        getEntityPotion(Type.PVP).keySet().removeIf(k -> attribute.getEntityPotion(Type.PVP).containsKey(k));
        getPotion().keySet().removeIf(k -> attribute.getPotion().containsKey(k));

        //技能
        Map<String, Double> PVEAttackSkill = new SkillMap<>(getAttackSkill(Type.PVE));
        for (Map.Entry<String, Double> stringDoubleEntry : attribute.getAttackSkill(Type.PVE).entrySet()) {
            if (!PVEAttackSkill.containsKey(stringDoubleEntry.getKey()))continue;
            Double aDouble = PVEAttackSkill.get(stringDoubleEntry.getKey()) - stringDoubleEntry.getValue();
            if (aDouble - stringDoubleEntry.getValue() <= 0){
                PVEAttackSkill.remove(stringDoubleEntry.getKey());
            }else {
                PVEAttackSkill.put(stringDoubleEntry.getKey(), aDouble);
            }
        }
        if (!PVEAttackSkill.equals(getAttackSkill(Type.PVE))){
            setAttackSkill(Type.PVE, PVEAttackSkill);
        }

        Map<String, Double> PVPAttackSkill = new SkillMap<>(getAttackSkill(Type.PVP));
        for (Map.Entry<String, Double> stringDoubleEntry : attribute.getAttackSkill(Type.PVP).entrySet()) {
            if (!PVPAttackSkill.containsKey(stringDoubleEntry.getKey()))continue;
            Double aDouble = PVPAttackSkill.get(stringDoubleEntry.getKey()) - stringDoubleEntry.getValue();
            if (aDouble - stringDoubleEntry.getValue() <= 0){
                PVPAttackSkill.remove(stringDoubleEntry.getKey());
            }else {
                PVPAttackSkill.put(stringDoubleEntry.getKey(), aDouble);
            }
        }
        if (!PVPAttackSkill.equals(getAttackSkill(Type.PVP))){
            setAttackSkill(Type.PVP, PVPAttackSkill);
        }
        Map<String, Double> PVEInjuredSkill = new SkillMap<>(getInjuredSkill(Type.PVE));
        for (Map.Entry<String, Double> stringDoubleEntry : attribute.getInjuredSkill(Type.PVE).entrySet()) {
            if (!PVEInjuredSkill.containsKey(stringDoubleEntry.getKey()))continue;
            Double aDouble = PVEInjuredSkill.get(stringDoubleEntry.getKey()) - stringDoubleEntry.getValue();
            if (aDouble - stringDoubleEntry.getValue() <= 0){
                PVEInjuredSkill.remove(stringDoubleEntry.getKey());
            }else {
                PVEInjuredSkill.put(stringDoubleEntry.getKey(), aDouble);
            }
        }
        if (!PVEInjuredSkill.equals(getInjuredSkill(Type.PVE))){
            setInjuredSkill(Type.PVE, PVEInjuredSkill);
        }

        Map<String, Double> PVPInjuredSkill = new SkillMap<>(getInjuredSkill(Type.PVP));
        for (Map.Entry<String, Double> stringDoubleEntry : attribute.getInjuredSkill(Type.PVP).entrySet()) {
            if (!PVPInjuredSkill.containsKey(stringDoubleEntry.getKey()))continue;
            Double aDouble = PVPInjuredSkill.get(stringDoubleEntry.getKey()) - stringDoubleEntry.getValue();
            if (aDouble - stringDoubleEntry.getValue() <= 0){
                PVPInjuredSkill.remove(stringDoubleEntry.getKey());
            }else {
                PVPInjuredSkill.put(stringDoubleEntry.getKey(), aDouble);
            }
        }
        if (!PVPInjuredSkill.equals(getInjuredSkill(Type.PVP))){
            setInjuredSkill(Type.PVP, PVPInjuredSkill);
        }
        //恢复生命值
        setAttackRecovery(Type.PVE, getAttackRecovery(Type.PVE) - attribute.getAttackRecovery(Type.PVE));
        setAttackRecovery(Type.PVP, getAttackRecovery(Type.PVP) - attribute.getAttackRecovery(Type.PVP));
        //附加生命值
        setHealthValue(getHealthValue() - attribute.getHealthValue());
        //幸运
        setLucky(getLucky() - attribute.getLucky());
        //反伤
        setBackInjury(getBackInjury() - attribute.getBackInjury());
        //免伤
        setInjuryFree(Type.PVE, getInjuryFree(Type.PVE) - attribute.getInjuryFree(Type.PVE));
        setInjuryFree(Type.PVP, getInjuryFree(Type.PVP) - attribute.getInjuryFree(Type.PVP));
        //护甲
        setArmorValue(getArmorValue() - attribute.getArmorValue());
        //miss
        setDodge(getDodge() - attribute.getDodge());
        //闪避
        setTenacity(getTenacity() - attribute.getTenacity());
        //速度
        setSpeed(getSpeed() - attribute.getSpeed());
    }
    public void reset(){
        PVEDamage = new RandomValue(0);
        PVPDamage = new RandomValue(0);
        PVESuckingBlood = 0;
        PVPSuckingBlood = 0;
        PVEBurning = 0;
        PVPBurning = 0;
        PVPLightning = 0;
        PVELightning = 0;
        PVPStrikeFly = 0;
        PVEStrikeFly = 0;
        PVPRepel = 0;
        PVERepel = 0;
        PVEAttackRecovery = 0;
        PVPAttackRecovery = 0;
        PVENailPiercing = 0;
        PVPNailPiercing = 0;
        PVEInjuryFree = 0;
        PVPInjuryFree = 0;
        groupGyrus = 0;
        backInjury = 0;
        dodge = 0;
        tenacity = 0;
        speed = 0;
        armor = 0;
        lucky = 0;
        healthValue = 0;
        PVESelfPotion = new PotionMap<>();
        PVPSelfPotion = new PotionMap<>();
        PVEEntityPotion = new PotionMap<>();
        PVPEntityPotion = new PotionMap<>();
        PVPAttackSkill = new SkillMap<>();
        PVEAttackSkill = new SkillMap<>();
        PVEInjuredSkill = new SkillMap<>();
        PVPInjuredSkill = new SkillMap<>();
        PVPCritical = new Additional();
        PVECritical = new Additional();
        potion = new PotionMap<>();
    }
    @Override
    public AbstractAttribute clone() {
        AbstractAttribute attribute = null;
        try {
            attribute = (AbstractAttribute) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
        attribute.PVESelfPotion = new PotionMap<>(PVESelfPotion);
        attribute.PVEEntityPotion = new PotionMap<>(PVEEntityPotion);
        attribute.PVPSelfPotion = new PotionMap<>(PVPSelfPotion);
        attribute.PVPEntityPotion = new PotionMap<>(PVPEntityPotion);
        attribute.PVPAttackSkill = new SkillMap<>(PVPAttackSkill);
        attribute.PVEAttackSkill = new SkillMap<>(PVEAttackSkill);
        attribute.PVPInjuredSkill = new SkillMap<>(PVPInjuredSkill);
        attribute.PVEInjuredSkill = new SkillMap<>(PVEInjuredSkill);
        attribute.potion = new PotionMap<>(potion);
        attribute.PVPCritical = attribute.PVPCritical.clone();
        attribute.PVECritical = attribute.PVECritical.clone();
        return attribute;
    }

    @Override
    public String toString() {
        return "AbstractAttribute{" +
                "PVEDamage=" + PVEDamage +
                ", PVPDamage=" + PVPDamage +
                ", PVESuckingBlood=" + PVESuckingBlood +
                ", PVPSuckingBlood=" + PVPSuckingBlood +
                ", PVEBurning=" + PVEBurning +
                ", PVPBurning=" + PVPBurning +
                ", PVPLightning=" + PVPLightning +
                ", PVELightning=" + PVELightning +
                ", PVPStrikeFly=" + PVPStrikeFly +
                ", PVEStrikeFly=" + PVEStrikeFly +
                ", PVPRepel=" + PVPRepel +
                ", PVERepel=" + PVERepel +
                ", PVEAttackRecovery=" + PVEAttackRecovery +
                ", PVPAttackRecovery=" + PVPAttackRecovery +
                ", PVENailPiercing=" + PVENailPiercing +
                ", PVPNailPiercing=" + PVPNailPiercing +
                ", PVEInjuryFree=" + PVEInjuryFree +
                ", PVPInjuryFree=" + PVPInjuryFree +
                ", groupGyrus=" + groupGyrus +
                ", backInjury=" + backInjury +
                ", dodge=" + dodge +
                ", tenacity=" + tenacity +
                ", speed=" + speed +
                ", armor=" + armor +
                ", lucky=" + lucky +
                ", healthValue=" + healthValue +
                ", PVESelfPotion=" + PVESelfPotion +
                ", PVPSelfPotion=" + PVPSelfPotion +
                ", PVEEntityPotion=" + PVEEntityPotion +
                ", PVPEntityPotion=" + PVPEntityPotion +
                ", PVEAttackSkill=" + PVEAttackSkill +
                ", PVPAttackSkill=" + PVPAttackSkill +
                ", PVEInjuredSkill=" + PVEInjuredSkill +
                ", PVPInjuredSkill=" + PVPInjuredSkill +
                ", potion=" + potion +
                '}';
    }
    public List<String> toStringRead() {
        List<String> list = new ArrayList<>();
            list.add("PVE伤害:" + PVEDamage);
            list.add("PVP伤害:" + PVPDamage);
            list.add("PVE吸血:" + PVESuckingBlood + "%");
            list.add("PVP吸血:" + PVPSuckingBlood + "%");
            list.add("PVE燃烧:" + PVEBurning);
            list.add("PVP燃烧:" + PVPBurning);
            list.add("PVE雷击:" + PVELightning);
            list.add("PVP雷击:" + PVPLightning);
            list.add("PVE击飞:" + PVEStrikeFly);
            list.add("PVP击飞:" + PVPStrikeFly);
            list.add("PVE击退:" + PVERepel);
            list.add("PVP击退:" + PVPRepel);
            list.add("PVE攻击恢复:" + PVEAttackRecovery);
            list.add("PVP攻击恢复:" + PVPAttackRecovery);
            list.add("PVE穿甲:" + PVENailPiercing + "%");
            list.add("PVP穿甲:" + PVPNailPiercing + "%");
            list.add("PVE免伤:" + PVEInjuryFree);
            list.add("PVP免伤:" + PVPInjuryFree);
            list.add("PVE群回:" + groupGyrus);
            list.add("反伤:" + backInjury + "%");
            list.add("闪避几率:" + dodge + "%");
            list.add("控制减少:" + tenacity + "%");
            list.add("速度加成:" + speed + "%");
            list.add("护甲:" + armor);
            list.add("幸运:" + lucky);
            list.add("附加生命值:" + healthValue);
            list.add("PVE攻击自身药水:" + PVESelfPotion);
            list.add("PVP攻击自身药水:" + PVPSelfPotion);
            list.add("PVE攻击目标药水:" + PVEEntityPotion);
            list.add("PVP攻击目标药水:" + PVPEntityPotion);
            list.add("PVE攻击技能:" + PVEAttackSkill);
            list.add("PVP攻击技能:" + PVPAttackSkill);
            list.add("被怪物攻击技能:" + PVEInjuredSkill);
            list.add("被玩家攻击技能:" + PVPInjuredSkill);
            list.add("实时药水效果:" + potion);
            return list;
    }
}
