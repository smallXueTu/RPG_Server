package cn.LTCraft.core.game.skills.shields;

import cn.LTCraft.core.game.skills.BaseSkill;
import cn.LTCraft.core.other.Temp;
import cn.LTCraft.core.task.GlobalRefresh;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * 基础护盾
 *
 */
public abstract class BaseShield implements Shield {
    private static final Map<String, Class<? extends BaseShield>> classMap = new HashMap<>();

    public static void init(){
        classMap.clear();
        classMap.put("能量护盾", EnergyShield.class);
        classMap.put("闪避护盾", DodgeShield.class);
    }

    public static Class<? extends BaseShield> getShield(String shieldName){
        return classMap.get(shieldName);
    }

    public static BaseShield getShieldObj(String shieldName, Player player, BaseSkill skill){
        try {
            Constructor<? extends BaseShield> construct = getShield(shieldName).getConstructor(Player.class, BaseSkill.class);
            return construct.newInstance(player, skill);
        } catch (Exception e) {
            return null;
        }
    }
    /**
     * 护盾剩余tick
     */
    protected int remainingTick;
    /**
     * 上次刷新的TICK
     */
    protected long lastTick = 0;
    /**
     * 觉醒
     */
    protected boolean awaken = false;
    /**
     * 等级
     */
    protected int level = 1;
    /**
     * 觉醒等级
     */
    protected int awakenLevel = 1;

    public BaseShield(BaseSkill baseSkill){
        awaken = baseSkill.isAwaken();
        level = baseSkill.getLevel();
        awakenLevel = baseSkill.getAwakenLevel();
        GlobalRefresh.addTickEntity(this);
    }

    public BaseShield(int level, int awakenLevel, boolean awaken){
        this.level = level;
        this.awakenLevel = awakenLevel;
        this.awaken = awaken;
        GlobalRefresh.addTickEntity(this);
    }
    public boolean doTick(long tick){
        boolean sign = remainingTick-- > 0;
        lastTick = tick;
        if (!sign){
            destroy();
        }
        return sign;
    }

    public int getRemainingTick() {
        return remainingTick;
    }

    public void setRemainingTick(int remainingTick) {
        this.remainingTick = remainingTick;
    }

    public long getLastTick() {
        return lastTick;
    }

    /**
     * 销毁护盾
     */
    public void destroy(){
        remainingTick = 0;
    }

    /**
     *
     * @return 是否被销毁
     */
    public boolean isDestroy(){
        return remainingTick > 0;
    }

    public int getAwakenLevel() {
        return awakenLevel;
    }

    public int getLevel() {
        return level;
    }

    public boolean isAwaken() {
        return awaken;
    }


    @Override
    public boolean isAsync() {
        return true;
    }
}
