package cn.LTCraft.core.other;

import cn.LTCraft.core.entityClass.PlayerState;
import cn.LTCraft.core.game.skills.shields.BaseShield;
import cn.LTCraft.core.game.skills.shields.Shield;
import cn.LTCraft.core.utils.Utils;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * TODO: remove it
 */
public class Temp {
    //以下所有字段的锁
    //对于get的代码 没必要进行锁机制
    public static final ReentrantLock lock = new ReentrantLock();
    /**
     * 玩家丢弃的物品
     * K:丢弃物
     * V:玩家名字
     * 考虑到玩家可能退出游戏，所以更适合使用玩家名字
     */
    public static final Map<Entity, String> playerDropItem = new HashMap<>();
    /**
     * 仅丢弃者可以捡起的物品
     */
    public static final List<Item> discardOnly = new ArrayList<>();
    /**
     * 垃圾计数器
     */
    public static Map<Player, Integer> dropCount = new HashMap<>();
    /**
     * 最后战斗时间
     */
    public static Map<Player, Long> lastBattleTime = new ConcurrentHashMap<>();
    /**
     * 重伤
     */
    public static final Map<Entity, Integer> injured = new HashMap<>();
    /**
     * 破甲
     */
    public static final Map<Entity, ArmorBreaking> armorBreaking = new HashMap<>();
    public static class ArmorBreaking{
        public int surplusTick = 0;//剩余tick
        public double value = 0;//破甲百分比
        public ArmorBreaking(double value, int surplusTick){
            this.value = value;
            this.surplusTick = surplusTick;
        }
    }
    /**
     * 沉默
     */
    public static final Map<Entity, Integer> silence = new HashMap<>();
    /**
     * 护盾
     */
    public static final Map<Entity, Shield> shield = new HashMap<>();
    /**
     * 玩家状态
     */
    public static final Map<Player, List<cn.LTCraft.core.entityClass.PlayerState>>  playerStates = new HashMap<>();
    public static void onPlayerQuit(Player player){
        Temp.lock.lock();
        injured.remove(player);
        silence.remove(player);
        shield.remove(player);
        lastBattleTime.remove(player);
        playerStates.remove(player);
        Temp.lock.unlock();
    }
    public static void onPlayerJoin(Player player){
        Temp.lock.lock();
        Temp.playerStates.put(player, new ArrayList<>());
        Temp.lock.unlock();
    }

    /**
     * 为一个实体添加护盾效果
     * @param entity 实体
     * @param s 时间 tick
     */
    public static void addShield(Entity entity, Shield s){
        Temp.lock.lock();
        if (!shield.containsKey(entity) && entity instanceof Player) {
            playerStates.get(entity).add(new cn.LTCraft.core.entityClass.PlayerState(((Player) entity), "护盾 %s%S", () -> shield.get(entity)==null?0:((BaseShield)shield.get(entity)).getRemainingTick() / 20d));
        }
        shield.put(entity, s);
        Temp.lock.unlock();
    }

    /**
     * 为一个实体添加重伤效果
     * @param entity 实体
     * @param s 时间 tick
     */
    public static void addInjured(Entity entity, int s){
        Temp.lock.lock();
        if (!injured.containsKey(entity) && entity instanceof Player) {
            playerStates.get(entity).add(new cn.LTCraft.core.entityClass.PlayerState(((Player) entity), "沉默 %s%S", () -> injured.getOrDefault(entity, 0) / 20d));
        }
        injured.put(entity, s);
        Temp.lock.unlock();
    }

    /**
     * 为一个实体添加沉默效果
     * @param entity 实体
     * @param s 时间 tick
     */
    public static void addSilence(Entity entity, int s){
        Temp.lock.lock();
        if (!silence.containsKey(entity) && entity instanceof Player) {
            playerStates.get(entity).add(new PlayerState(((Player) entity), "沉默 %s%S", () -> silence.getOrDefault(entity, 0) / 20d));
        }
        silence.put(entity, s);
        Temp.lock.unlock();
    }

    /**
     * 为一个实体添加破甲效果 新的效果会覆盖旧的效果
     * @param entity 实体
     * @param armorBreaking 破甲
     * @see ArmorBreaking
     */
    public static void addArmorBreaking(Entity entity, ArmorBreaking armorBreaking){
        Temp.lock.lock();
        if (!Temp.armorBreaking.containsKey(entity) && entity instanceof Player) {
            playerStates.get(entity).add(new PlayerState(((Player) entity), "破甲 " + Utils.formatNumber((armorBreaking.value * 100)) + "% %s%S", () -> Temp.armorBreaking.getOrDefault(entity, new ArmorBreaking(0, 0)).surplusTick / 20d));
        }
        Temp.armorBreaking.put(entity, armorBreaking);
        Temp.lock.unlock();
    }

    /**
     * 保护这个掉落物
     * @param player 可以捡起的玩家
     * @param item 掉落物
     */
    public static void protectItem(Player player, Item item){
        discardOnly.add(item);
        playerDropItem.put(item, player.getName());
    }
}
