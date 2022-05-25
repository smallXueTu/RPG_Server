package cn.LTCraft.core.hook.BQ.objective;

import cn.LTCraft.core.Main;
import cn.ltcraft.item.items.Ornament;
import cn.ltcraft.item.objs.PlayerAttribute;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * 传送到指定世界路线
 * Created by Angel、 on 2022/5/25 9:49
 */
public class WorldObjective extends Objective implements Listener {
    private final String worldName;
    private int taskID;
    public WorldObjective(Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = ObjectiveData.class;
        worldName = instruction.next();
    }

    @Override
    public void start() {
        taskID = Bukkit.getScheduler().runTaskTimer(BetonQuest.getInstance(), () -> {
            if (dataMap == null)return;
            for (String playerID : dataMap.keySet()){
                Player player = PlayerConverter.getPlayer(playerID);
                if (player.getWorld().getName().equalsIgnoreCase(worldName)){
                    this.completeObjective(playerID);
                }
            }
        }, 20, 20).getTaskId();
    }

    @Override
    public void stop() {
        Bukkit.getScheduler().cancelTask(taskID);
    }

    @Override
    public String getDefaultDataInstruction() {
        return "";
    }
}