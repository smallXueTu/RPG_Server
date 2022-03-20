package cn.LTCraft.core.game.skills;

import cn.LTCraft.core.game.skills.shields.BaseShield;
import cn.LTCraft.core.task.PlayerClass;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseSkill implements Skill{
    private static final Map<String, Class<? extends BaseSkill>> classMap = new HashMap<>();

    public static void init(){
        BaseShield.init();
        classMap.clear();
        classMap.put("救死扶伤", HealTheWounded.class);
        classMap.put("能量护盾", EnergyShield.class);
        classMap.put("缴械", Disarm.class);
        classMap.put("行刺之道", TheWayOfAssassination.class);
    }

    public static Class<? extends BaseSkill> getSkill(String skillName){
        return classMap.get(skillName);
    }

    public static BaseSkill getSkillObj(String skillName, Player player, int level, int awakenLevel, boolean awaken){
        try {
            Constructor<? extends BaseSkill> construct = getSkill(skillName).getConstructor(Player.class, int.class, int.class, boolean.class);
            return construct.newInstance(player, level, awakenLevel, awaken);
        } catch (Exception e) {
            return null;
        }
    }

    protected PlayerClass requirementClass = null;
    protected boolean awaken = false;
    protected int level = 1;
    protected int awakenLevel = 1;

    public BaseSkill(){
    }

    public BaseSkill(int level, int awakenLevel, boolean awaken){
        this.level = level;
        this.awakenLevel = awakenLevel;
        this.awaken = awaken;
    }

    /**
     * 已觉醒
     * @return 是否觉醒
     */
    public boolean isAwaken() {
        return awaken;
    }

    public void setAwaken(boolean awaken) {
        this.awaken = awaken;
    }

    /**
     *
     * @return 技能等级
     */
    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    /**
     *
     * @return 觉醒等级
     */
    public int getAwakenLevel() {
        return awakenLevel;
    }

    public void setAwakenLevel(int awakenLevel) {
        this.awakenLevel = awakenLevel;
    }

    @Override
    public boolean cast() {
        return cast(null);
    }
}
