package cn.LTCraft.core.other;

import cn.LTCraft.core.entityClass.PlayerState;
import cn.LTCraft.core.game.skills.shields.BaseShield;
import cn.LTCraft.core.game.skills.shields.Shield;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Temp {
    /**
     * 玩家丢弃的物品
     */
    public static final Map<Entity, String> playerDropItem = new HashMap<>();
    /**
     * 仅丢弃者可以捡起的物品
     */
    public static final List<Integer> discardOnly = new ArrayList<>();
    /**
     * 重伤
     */
    public static final Map<Entity, Integer> injured = new HashMap<>();
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
    public static final Map<Player, List<cn.LTCraft.core.entityClass.PlayerState>> playerStates = new HashMap<>();
    public static void onPlayerQuit(Player player){
        injured.remove(player);
        silence.remove(player);
        shield.remove(player);
        playerStates.remove(player);
    }
    public static void onPlayerJoin(Player player){
        Temp.playerStates.put(player, new ArrayList<>());
    }

    /**
     * 为一个实体添加护盾效果
     * @param entity 实体
     * @param s 时间 tick
     */
    public static void addShield(Entity entity, Shield s){
        if (!shield.containsKey(entity) && entity instanceof Player) {
            playerStates.get(entity).add(new cn.LTCraft.core.entityClass.PlayerState(((Player) entity), "护盾 %s%S", () -> shield.get(entity)==null?0:((BaseShield)shield.get(entity)).getRemainingTick() / 20d));
        }
        shield.put(entity, s);
    }

    /**
     * 为一个实体添加重伤效果
     * @param entity 实体
     * @param s 时间 tick
     */
    public static void addInjured(Entity entity, int s){
        if (!injured.containsKey(entity) && entity instanceof Player) {
            playerStates.get(entity).add(new cn.LTCraft.core.entityClass.PlayerState(((Player) entity), "沉默 %s%S", () -> injured.get(entity) / 20d));
        }
        injured.put(entity, s);
    }

    /**
     * 为一个实体添加沉默效果
     * @param entity 实体
     * @param s 时间 tick
     */
    public static void addSilence(Entity entity, int s){
        if (!silence.containsKey(entity) && entity instanceof Player) {
            playerStates.get(entity).add(new PlayerState(((Player) entity), "沉默 %s%S", () -> silence.get(entity) / 20d));
        }
        silence.put(entity, s);
    }
}
