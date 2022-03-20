package cn.LTCraft.core.hook.BQ.event;

import cn.LTCraft.core.entityClass.ClutterItem;
import cn.LTCraft.core.utils.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.utils.PlayerConverter;

public class TakeEvent extends QuestEvent {
    private final ClutterItem[] items;
    private final boolean notify;

    public TakeEvent(Instruction instruction) throws InstructionParseException {
        super(instruction);
        String[] array = instruction.getArray();
        items = new ClutterItem[array.length];
        for (int i = 0; i < array.length; i++) {
            items[i] = ClutterItem.spawnClutterItem(array[i]);
        }
        notify = instruction.hasArgument("notify");
    }
    @Override
    public void run(String playerID)  {
        Player player = PlayerConverter.getPlayer(playerID);
        for (ClutterItem item : items) {
            if (this.notify) {
                Config.sendMessage(playerID, "items_taken", new String[]{item.toString(), String.valueOf(item.getNumber())});
            }
            PlayerInventory inventory = player.getInventory();
            ItemStack[] contents = inventory.getContents();
            int amount = ItemUtils.removeItem(contents, item);
            inventory.setContents(contents);
            if (amount > 0) {
                ItemStack[] armorContents = player.getInventory().getArmorContents();
                ItemUtils.removeItem(armorContents, item);
                player.getInventory().setArmorContents(armorContents);
            }
        }
    }
}
