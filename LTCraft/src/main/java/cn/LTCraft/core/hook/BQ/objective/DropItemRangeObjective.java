package cn.LTCraft.core.hook.BQ.objective;

import cn.LTCraft.core.Main;
import cn.LTCraft.core.entityClass.ClutterItem;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftItem;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.utils.LocationData;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DropItemRangeObjective  extends Objective implements Listener {
    private int amount;
    private ClutterItem item;
    private LocationData location;
    private double range;

    public DropItemRangeObjective(Instruction instruction) throws InstructionParseException {
        super(instruction);
        this.template = DropItemRangeObjective.DData.class;
        item = ClutterItem.spawnClutterItem(instruction.next());
        amount = instruction.getInt();
        location = instruction.getLocation();
        range = instruction.getDouble();
    }
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) throws QuestRuntimeException {
        if (item.compare(event.getItemDrop().getItemStack())){
            String playerID = PlayerConverter.getID(event.getPlayer());
            if (this.containsPlayer(playerID) && this.checkConditions(playerID) && location.getLocation(playerID).distance(event.getPlayer().getLocation()) < range) {
                DropItemRangeObjective.DData playerData =  (DropItemRangeObjective.DData)this.dataMap.get(playerID);
                ItemStack yItemStack = event.getItemDrop().getItemStack();
                int amount = yItemStack.getAmount();
                playerData.drop(amount);
                List<String> yLore = yItemStack.getItemMeta().getLore();
                CraftItem yItem = (CraftItem)event.getItemDrop();
                yItemStack.setAmount(1);
                ItemStack itemStack = null;
                for (int i = 0; i < amount ; i++) {
                    List<String> lore = new ArrayList<>(yLore);
                    lore.add("sign" + UUID.randomUUID());
                    itemStack = yItemStack.clone();
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    itemMeta.setLore(lore);
                    itemStack.setItemMeta(itemMeta);
                    if (i < amount - 1) {
                        Item item = event.getPlayer().getWorld().dropItem(event.getPlayer().getLocation(), itemStack);
                        ((CraftItem) item).getHandle().f(yItem.getHandle().motX, yItem.getHandle().motY, yItem.getHandle().motZ);
                        item.setPickupDelay(Integer.MAX_VALUE);
                    }
                }
                event.getItemDrop().setItemStack(itemStack);
                event.getItemDrop().setPickupDelay(Integer.MAX_VALUE);
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
            return Integer.toString(((DropItemRangeObjective.DData) this.dataMap.get(playerID)).getAmount());
        } else {
            return name.equalsIgnoreCase("amount") ? Integer.toString(this.amount - ((DropItemRangeObjective.DData) this.dataMap.get(playerID)).getAmount()) : "";
        }
    }
    public static class DData extends ObjectiveData {
        private int amount;

        public DData(String instruction, String playerID, String objID) {
            super(instruction, playerID, objID);
            this.amount = Integer.parseInt(instruction);
        }

        private void drop(int amount) {
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
