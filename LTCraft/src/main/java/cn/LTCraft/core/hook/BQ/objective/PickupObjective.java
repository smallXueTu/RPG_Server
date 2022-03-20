package cn.LTCraft.core.hook.BQ.objective;

import cn.LTCraft.core.Main;
import cn.LTCraft.core.entityClass.ClutterItem;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.utils.PlayerConverter;

public class PickupObjective extends Objective implements Listener {
    private int amount;
    private ClutterItem item;
    public PickupObjective(Instruction instruction) throws InstructionParseException {
        super(instruction);
        this.template = PickupObjective.PData.class;
        item = ClutterItem.spawnClutterItem(instruction.next());
        amount = instruction.getInt(instruction.getOptional("amount"), 1);
    }

    @EventHandler
    public void onPickupItem(PlayerPickupItemEvent event) {
        if (item.compare(event.getItem().getItemStack())){
            String playerID = PlayerConverter.getID(event.getPlayer());
            if (this.containsPlayer(playerID) && this.checkConditions(playerID)) {
                PickupObjective.PData playerData = (PickupObjective.PData)this.dataMap.get(playerID);
                playerData.pickup(event.getItem().getItemStack().getAmount());
                if (playerData.isComplete()) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(cn.LTCraft.core.Main.getInstance(), () ->
                            this.completeObjective(playerID), 0);
                }
            }
        }
    }
    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public String getDefaultDataInstruction() {
        return Integer.toString(this.amount);
    }

    public String getProperty(String name, String playerID) {
        if (name.equalsIgnoreCase("left")) {
            return Integer.toString(((PickupObjective.PData)this.dataMap.get(playerID)).getAmount());
        } else {
            return name.equalsIgnoreCase("amount") ? Integer.toString(this.amount - ((PickupObjective.PData)this.dataMap.get(playerID)).getAmount()) : "";
        }
    }

    public static class PData extends ObjectiveData {
        private int amount;

        public PData(String instruction, String playerID, String objID) {
            super(instruction, playerID, objID);
            this.amount = Integer.parseInt(instruction);
        }

        private void pickup(int amount) {
            this.amount -= amount;
            this.update();
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
