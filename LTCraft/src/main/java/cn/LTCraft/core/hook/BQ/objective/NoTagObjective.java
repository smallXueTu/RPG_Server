package cn.LTCraft.core.hook.BQ.objective;

import cn.LTCraft.core.Main;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.database.PlayerData;
import pl.betoncraft.betonquest.utils.Utils;

public class NoTagObjective extends Objective implements Listener {
    private String tag;
    private int taskID;
    public NoTagObjective(Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = ObjectiveData.class;
        tag = Utils.addPackage(instruction.getPackage(), instruction.next());
    }
    @Override
    public void start() {
        taskID = Bukkit.getScheduler().runTaskTimer(BetonQuest.getInstance(), () -> {
            if (dataMap == null)return;
            for (String playerID : dataMap.keySet()){
                PlayerData playerData = BetonQuest.getInstance().getPlayerData(playerID);
                if (!playerData.hasTag(tag)){
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () ->
                            this.completeObjective(playerID), 0);
                }
            }
        }, 10, 10).getTaskId();
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
