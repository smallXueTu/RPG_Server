package cn.LTCraft.core.hook.BQ.objective;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import pl.betoncraft.betonquest.*;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class ConditionObjective extends Objective implements Listener {
    private List<String> conditions = new ArrayList<>();
    private int taskID;
    public ConditionObjective(Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = ObjectiveData.class;
        String cs = instruction.next();
        for (String con : cs.split(";")){
            if (con.length() <= 0 )continue;
            conditions.add(Utils.addPackage(instruction.getPackage(), con));
        }
    }
    @Override
    public void start() {
        taskID = Bukkit.getScheduler().runTaskTimer(BetonQuest.getInstance(), () -> {
            if (dataMap == null)return;
            for (String playerID : dataMap.keySet()){
                boolean sign = true;
                for (String condition : conditions) {
                    ConditionID conditionID;
                    try {
                        conditionID = new ConditionID(null, condition);
                    } catch (ObjectNotFoundException e) {
                        sign = false;
                        continue;
                    }
                    if (!BetonQuest.condition(playerID, conditionID))sign = false;
                }
                if (sign){
                    Bukkit.getScheduler().scheduleSyncDelayedTask(cn.LTCraft.core.Main.getInstance(), () ->
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
