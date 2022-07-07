package cn.LTCraft.core.hook.BQ.objective;

import cn.LTCraft.core.Main;
import cn.LTCraft.core.utils.matcher.ItemMatcher;
import com.intellectualcrafters.plot.api.PlotAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import pl.betoncraft.betonquest.*;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.*;

/**
 * Created by Angel、 on 2022/6/26 21:57
 */
public class ItemMatchingObjective extends Objective {
    private int taskID;
   private ItemMatcher[] itemMatchers;
    public ItemMatchingObjective(Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = ObjectiveData.class;
        String next = instruction.next();
        String[] split = next.split(",,,");
        itemMatchers = new ItemMatcher[split.length];
        for (int i = 0; i < split.length; i++) {
            itemMatchers[i] = ItemMatcher.parse(split[i]);
        }
    }

    @Override
    public void start() {
        taskID = Bukkit.getScheduler().runTaskTimer(BetonQuest.getInstance(), () -> {
            if (dataMap == null)return;
            for (String playerID : dataMap.keySet()){
                try {
                    if (BetonQuest.condition(playerID, new ConditionID(null, "itemMatching"))) {
                        this.completeObjective(playerID);
                    }
                } catch (ObjectNotFoundException e) {
                    e.printStackTrace();
                }
                return;
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
