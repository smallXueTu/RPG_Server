package cn.LTCraft.core.hook.BQ.event;

import cn.LTCraft.core.entityClass.ClutterItem;
import cn.LTCraft.core.utils.PlayerUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.utils.PlayerConverter;

public class GiveEvent extends QuestEvent {
    private final ClutterItem[] items;
    private final boolean notify;

    public GiveEvent(Instruction instruction) throws InstructionParseException {
        super(instruction);
        String[] items = instruction.getArray();
        this.items = new ClutterItem[items.length];
        for (int i = 0; i < items.length; i++) {
            String itemStr = items[i];
            this.items[i] = ClutterItem.spawnClutterItem(itemStr);
        }
        this.notify = instruction.hasArgument("notify");
    }

    public void run(String playerID) {
        Player player = PlayerConverter.getPlayer(playerID);
        for (ClutterItem clutterItem : items) {
            int number = clutterItem.getNumber();
            while (number > 0) {
                int stackSize = Math.min(number, 64);
                ItemStack itemStack = clutterItem.generate(stackSize);
                PlayerUtils.securityAddItem(player, itemStack);
                number -= stackSize;
            }
        }
    }
}
