package cn.LTCraft.core.hook.BQ.condition;

import cn.LTCraft.core.utils.matcher.ItemMatcher;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Angel、 on 2022/7/7 10:53
 */
public class ItemMatchingCondition extends Condition {

    private final ItemMatcher[] itemMatchers;
    public ItemMatchingCondition(Instruction instruction) throws InstructionParseException {
        super(instruction);
        String[] split = instruction.next().split(",,,");
        itemMatchers = new ItemMatcher[split.length];
        for (int i = 0; i < split.length; i++) {
            itemMatchers[i] = ItemMatcher.parse(split[i]);
        }
    }

    @Override
    public boolean check(String s) throws QuestRuntimeException {
        List<ItemMatcher> itemMatchers = new ArrayList<>(Arrays.asList(this.itemMatchers));
        Player player = PlayerConverter.getPlayer(s);
        List<ItemStack> matched = new ArrayList<>();
        ItemStack[] contents = player.getInventory().getContents();
        for (ItemStack itemStack : contents) {
            if (itemMatchers.size() <= 0)break;
            if (itemStack == null || matched.contains(itemStack))continue;
            for (Iterator<ItemMatcher> iterator = itemMatchers.iterator(); iterator.hasNext();) {
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
        return itemMatchers.size() <= 0;
    }
}
