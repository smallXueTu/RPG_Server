package cn.LTCraft.core.hook.BQ.objective;

import cn.LTCraft.core.Main;
import cn.LTCraft.core.utils.matcher.ItemMatcher;
import com.intellectualcrafters.plot.api.PlotAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.ArrayList;
import java.util.List;

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
            boolean sign = true;
            for (String playerID : dataMap.keySet()){
                Player player = PlayerConverter.getPlayer(playerID);
                sign = true;
                breakD:
                if (sign) {
                    for (ItemStack itemStack : player.getInventory().getContents()) {
                        for (ItemMatcher itemMatcher : itemMatchers) {
                            if (!itemMatcher.matches(itemStack, player)) {
                                sign = false;
                                break breakD;
                            }
                        }
                    }
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
