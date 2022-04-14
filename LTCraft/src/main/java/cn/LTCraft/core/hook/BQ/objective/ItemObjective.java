package cn.LTCraft.core.hook.BQ.objective;

import cn.LTCraft.core.Main;
import cn.LTCraft.core.entityClass.ClutterItem;
import cn.LTCraft.core.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import pl.betoncraft.betonquest.*;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.utils.PlayerConverter;

public class ItemObjective extends Objective implements Listener {
    private int amount = 0;
    private int taskID;
    private ClutterItem[] items;
    public ItemObjective(Instruction instruction) throws InstructionParseException {
        super(instruction);
        this.template = ItemObjective.IData.class;
        String[] items = instruction.getArray();
        String[] amounts = instruction.getArray(instruction.getOptional("amount"));
        this.items = new ClutterItem[items.length];
        for (int i = 0; i < items.length; i++) {
            String itemStr = items[i];
            this.items[i] = ClutterItem.spawnClutterItem(itemStr);
            if (amounts.length > i) {
                this.items[i].setNumber(Integer.parseInt(amounts[i]));
            }
            this.amount += this.items[i].getNumber();
        }
    }
    @Override
    public void start() {
        taskID = Bukkit.getScheduler().runTaskTimer(BetonQuest.getInstance(), () -> {
            if (dataMap == null)return;
            for (String playerID : dataMap.keySet()) {
                IData playerData = (IData)dataMap.get(playerID);
                if (playerData == null)continue;
                Player player = PlayerConverter.getPlayer(playerID);
                ItemStack[] contents = ItemUtils.clone(player.getInventory().getContents());
                int sum = 0;
                for (ClutterItem item : items) {
                    int i = ItemUtils.removeItem(contents, item, player);
                    sum += Math.max(i, 0);
                }
                playerData.setAmount(sum);
                if (sum <= 0){
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () ->
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
        return Integer.toString(this.amount);
    }
    public String getProperty(String name, String playerID) {
        if (name.equalsIgnoreCase("left")) {
            return Integer.toString(((ItemObjective.IData)this.dataMap.get(playerID)).getAmount());
        } else {
            return name.equalsIgnoreCase("amount") ? Integer.toString(this.amount - ((ItemObjective.IData)this.dataMap.get(playerID)).getAmount()) : "";
        }
    }

    public static class IData extends ObjectiveData {
        private int amount;

        public IData(String instruction, String playerID, String objID) {
            super(instruction, playerID, objID);
            this.amount = Integer.parseInt(instruction);
        }

        private void setAmount(int amount) {
            this.amount = amount;
        }

        private boolean isComplete() {
            return this.amount <= 0;
        }

        private int getAmount() {
            return this.amount;
        }

        public String toString() {
            return String.valueOf(this.amount);
        }
    }
}
