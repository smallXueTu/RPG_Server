package cn.LTCraft.core.task;

import cn.LTCraft.core.dataBase.mappers.PlayerMapper;
import cn.LTCraft.core.dataBase.SQLQueue;
import cn.LTCraft.core.dataBase.SQLServer;
import cn.LTCraft.core.entityClass.PlayerState;
import cn.LTCraft.core.Main;
import cn.LTCraft.core.entityClass.MobSpawn;
import cn.LTCraft.core.game.Game;
import cn.LTCraft.core.game.SpawnManager;
import cn.LTCraft.core.game.TargetOnlyMobsManager;
import cn.LTCraft.core.game.skills.BaseSkill;
import cn.LTCraft.core.other.Temp;
import cn.LTCraft.core.utils.PlayerUtils;
import cn.LTCraft.core.utils.Utils;
import cn.ltcraft.teleport.Teleport;
import net.minecraft.server.v1_12_R1.EntityRabbit;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.io.File;
import java.util.*;

public class GlobalRefresh {
    private static Main plugin = null;
    private static long tick = 0;
    private static Map<String, List<String>> lastObj = new HashMap<>();
    private static YamlConfiguration waypoint = null;
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
        if (tick % 20 == 0) {
            /*
             * 针对怪物
             */
            TargetOnlyMobsManager.getInstance().doTick();
            /*
            任务
             */
            ClientCheckTask.checkTask();
            /*
            BQ笔记
             */
            for (Player player : Bukkit.getOnlinePlayers()) {
                List<Objective> objectives = BetonQuest.getInstance().getPlayerObjectives(PlayerConverter.getID(player));
                if (lastObj.containsKey(player.getName())) {
                    List<String> list = lastObj.get(player.getName());
                    for (Objective playerObjective : objectives) {
                        if (!list.contains(playerObjective.getLabel())){
                            String objName = playerObjective.getLabel().substring(playerObjective.getLabel().indexOf('.') + 1);
                            if (waypoint.contains(objName) && objName.startsWith("【主线")) {
                                if (PlayerUtils.hasBQTag(player, "default.自动导航"))
                                    Bukkit.getServer().dispatchCommand(player, "dgps to " + objName);
                            }
                        }
                    }
                }
                lastObj.put(player.getName(), new ArrayList<String>(){
                    {
                        for (Objective objective : objectives) {
                            add(objective.getLabel());
                        }
                    }
                });
            }
            Temp.playerStates.forEach((player, playerStates) -> playerStates.removeIf(PlayerState::complete));
            GarbageClear.getInstance().onTick();
            SpawnManager.getInstance().getSpawns().forEach(MobSpawn::onUpdate);
        }
//        if(tick % 200 == 0){
//            File dir = new File(plugin.getDataFolder().getParentFile(), "DragonGPS");
//            waypoint = YamlConfiguration.loadConfiguration(new File(dir, "waypoint.yml"));
//        }
        /*
         * 保存
         */
        if (tick % 6000 == 0){
            Teleport.getInstance().save();
            Temp.dropCount = new HashMap<>();
        }
        //效果
        Temp.injured.replaceAll((k, v) -> v - 1);
        Temp.injured.entrySet().removeIf(entry -> entry.getValue() <= 0);
        Temp.silence.replaceAll((k, v) -> v - 1);
        Temp.silence.entrySet().removeIf(entry -> entry.getValue() <= 0);
        Temp.shield.entrySet().removeIf(entry -> !entry.getValue().doTick(tick));
    }

    public static Map<String, List<String>> getLastObj() {
        return lastObj;
    }
    public static void init(Main plugin) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getInstance(), () -> {
            Game.tickEquipment(tick);
        }, 1, 1);
        GlobalRefresh.plugin = plugin;
        File dir = new File(plugin.getDataFolder().getParentFile(), "DragonGPS");
        waypoint = YamlConfiguration.loadConfiguration(new File(dir, "waypoint.yml"));
        BaseSkill.init();
        /*
        MyBatis
         */
        SQLServer sqlServer = plugin.getSQLServer();
        sqlServer.getConfiguration().addMapper(PlayerMapper.class);
    }
}
