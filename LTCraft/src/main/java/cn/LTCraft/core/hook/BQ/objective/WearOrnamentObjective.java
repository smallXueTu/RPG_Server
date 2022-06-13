package cn.LTCraft.core.hook.BQ.objective;

import cn.ltcraft.item.items.Ornament;
import cn.ltcraft.item.objs.PlayerAttribute;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.betonquest.utils.Utils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Angel、 on 2022/5/7 17:48
 */
public class WearOrnamentObjective extends Objective implements Listener {
    private String ornamentName;
    private int taskID;
    public WearOrnamentObjective(Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = ObjectiveData.class;
        ornamentName = instruction.next();
    }
    @Override
    public void start() {
        taskID = Bukkit.getScheduler().runTaskTimer(BetonQuest.getInstance(), () -> {
            if (dataMap == null)return;
            Set<Map.Entry<String, ObjectiveData>> entries = dataMap.entrySet();
            new HashSet<>(dataMap.keySet()).forEach((playerID) -> {
                Player player = PlayerConverter.getPlayer(playerID);
                PlayerAttribute playerAttribute = PlayerAttribute.getPlayerAttribute(player);
                Ornament[] ornamentsAtt = playerAttribute.getOrnamentsAtt();
                for (Ornament ornament : ornamentsAtt) {
                    if (ornament == null)continue;
                    if (ornament.getName().equals(ornamentName)){
                        this.completeObjective(playerID);
                        break;
                    }
                }
            });
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
