package cn.LTCraft.core.hook.BQ.condition;

import cn.LTCraft.core.entityClass.ClutterItem;
import cn.LTCraft.core.utils.ItemUtils;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.utils.PlayerConverter;

public class ItemCondition extends Condition {
    private final ClutterItem[] items;

    public ItemCondition(Instruction instruction) throws InstructionParseException {
        super(instruction);
        String[] array = instruction.getArray();
        items = new ClutterItem[array.length];
        for (int i = 0; i < array.length; i++) {
            items[i] = ClutterItem.spawnClutterItem(array[i]);
        }
    }
    @Override
    public boolean check(String playerID) {
        Player player = PlayerConverter.getPlayer(playerID);
        return ItemUtils.sufficientItem(items, player);
    }
}
