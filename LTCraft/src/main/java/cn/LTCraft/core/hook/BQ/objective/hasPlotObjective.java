package cn.LTCraft.core.hook.BQ.objective;

import cn.LTCraft.core.Main;
import com.intellectualcrafters.plot.api.PlotAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * 拥有一块地皮
 * Created by Angel、 on 2022/5/25 9:58
 */
public class hasPlotObjective extends Objective implements Listener {
    private int taskID;
    public hasPlotObjective(Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = ObjectiveData.class;
    }

    @Override
    public void start() {
        taskID = Bukkit.getScheduler().runTaskTimer(BetonQuest.getInstance(), () -> {
            if (dataMap == null)return;
            for (String playerID : dataMap.keySet()){
                Player player = PlayerConverter.getPlayer(playerID);
                PlotAPI plotAPI = Main.getInstance().getPlotAPI();
                if (plotAPI.getPlayerPlots(player).size() > 0) {
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
