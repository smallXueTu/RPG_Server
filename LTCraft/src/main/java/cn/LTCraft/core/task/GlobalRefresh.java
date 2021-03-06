package cn.LTCraft.core.task;

import cn.LTCraft.core.Main;
import cn.LTCraft.core.dataBase.SQLQueue;
import cn.LTCraft.core.dataBase.SQLServer;
import cn.LTCraft.core.dataBase.mappers.PlayerMapper;
import cn.LTCraft.core.entityClass.PlayerState;
import cn.LTCraft.core.game.Game;
import cn.LTCraft.core.game.more.tickEntity.TickEntity;
import cn.LTCraft.core.game.skills.BaseSkill;
import cn.LTCraft.core.other.Temp;
import cn.LTCraft.core.utils.PlayerUtils;
import cn.ltcraft.teleport.Teleport;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class GlobalRefresh {
    private static Main plugin = null;
    private static long tick = 0;
    private static final List<TickEntity> tickEntities = new ArrayList<>();
    public static List<String> warnings = new ArrayList<>();
    public static void refresh(){
        tick++;
        /*
          SQL
         */
        SQLQueue sqlQueue = null;
        Iterator<SQLQueue> iterator = plugin.getSQLManage().getDoneQueue().iterator();
        while (iterator.hasNext()){
            sqlQueue = iterator.next();
            if (sqlQueue.getCallBack() != null)sqlQueue.getCallBack().onComplete(sqlQueue);
            iterator.remove();
        }
        synchronized (tickEntities) {
            tickEntities.removeIf(tickEntity -> !tickEntity.isAsync() && tick % tickEntity.getTickRate() == 0 && !tickEntity.doTick(tick));
        }
        /*
         * 保存
         */
        if (tick % 6000 == 0){
            Teleport.getInstance().save();
            Temp.dropCount = new HashMap<>();
        }
    }
    public static void init(Main plugin) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getInstance(), () -> {
            synchronized (tickEntities) {
                tickEntities.removeIf(tickEntity -> tickEntity.isAsync() && tick % tickEntity.getTickRate() == 0 && !tickEntity.doTick(tick));
            }
            Game.tickEquipment(tick);
            //玩家状态
            Temp.lock.lock();
            Temp.injured.replaceAll((k, v) -> v - 1);
            Temp.injured.entrySet().removeIf(entry -> entry.getValue() <= 0);
            Temp.silence.replaceAll((k, v) -> v - 1);
            Temp.armorBreaking.values().removeIf(v -> v.surplusTick -- <= 0);
            Temp.silence.entrySet().removeIf(entry -> entry.getValue() <= 0);
            Temp.playerStates.forEach((player, playerStates) -> playerStates.removeIf(PlayerState::complete));
            Temp.lock.unlock();
        }, 1, 1);
        GlobalRefresh.plugin = plugin;
        BQObjectiveCheck.getInstance();
        GarbageClear.getInstance();
        BaseSkill.init();
        /*
        MyBatis
         */
        SQLServer sqlServer = plugin.getSQLServer();
        sqlServer.getConfiguration().addMapper(PlayerMapper.class);
        PlayerUtils.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    }

    public static long getTick() {
        return tick;
    }
    public static void addTickEntity(TickEntity tickEntity){
        synchronized (tickEntities) {
            tickEntities.add(tickEntity);
        }
    }
}
