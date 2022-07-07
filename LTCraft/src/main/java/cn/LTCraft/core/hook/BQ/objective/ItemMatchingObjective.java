package cn.LTCraft.core.hook.BQ.objective;

import cn.LTCraft.core.utils.matcher.ItemMatcher;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Angel、 on 2022/6/26 21:57
 */
public class ItemMatchingObjective extends Objective {
    private int taskID;
   private final ItemMatcher[] itemMatchers;
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
                List<ItemStack> matched = new ArrayList<>();
                List<ItemMatcher> itemMatchers = new ArrayList<>(Arrays.asList(this.itemMatchers));
                Player player = PlayerConverter.getPlayer(playerID);
                ItemStack[] contents = player.getInventory().getContents();
                for (ItemStack itemStack : contents) {
                    if (itemMatchers.size() <= 0)break;
                    if (itemStack == null || matched.contains(itemStack))continue;
                    for (Iterator<ItemMatcher> iterator = itemMatchers.iterator();iterator.hasNext();) {
                        ItemMatcher itemMatcher = iterator.next();
                        if (itemMatcher.matches(itemStack, player)) {
                            matched.add(itemStack);
                            iterator.remove();
                            if (itemMatchers.size() <= 0){
                                break;
                            }
                        }
                    }
                }
                if (itemMatchers.size() <= 0)this.completeObjective(playerID);
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
