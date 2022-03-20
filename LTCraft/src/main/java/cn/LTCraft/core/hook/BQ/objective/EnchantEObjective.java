package cn.LTCraft.core.hook.BQ.objective;

import cn.LTCraft.core.Main;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.utils.PlayerConverter;

public class EnchantEObjective extends Objective implements Listener {
    private EnchantEObjective.EnchantmentData enchantmentData;

    public EnchantEObjective(Instruction instruction) throws InstructionParseException {
        super(instruction);
        this.template = ObjectiveData.class;
        try {
            this.enchantmentData = EnchantEObjective.EnchantmentData.convert(instruction.next());
        }catch (InstructionParseException exception){
            throw new InstructionParseException("Not enough arguments");
        }
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        String playerID = PlayerConverter.getID(event.getEnchanter());
        if (this.containsPlayer(playerID)) {
            if(event.getEnchantsToAdd().containsKey(enchantmentData.getEnchantment())){
                if (this.checkConditions(playerID)) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(cn.LTCraft.core.Main.getInstance(), () ->
                            this.completeObjective(playerID), 0);
                }
            }
        }
    }

    public void start() {
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    public void stop() {
        HandlerList.unregisterAll(this);
    }

    public String getDefaultDataInstruction() {
        return "";
    }

    public static class EnchantmentData {
        private final Enchantment enchantment;

        public EnchantmentData(Enchantment enchantment) {
            this.enchantment = enchantment;
        }

        public Enchantment getEnchantment() {
            return this.enchantment;
        }

        public static EnchantEObjective.EnchantmentData convert(String string) throws InstructionParseException {
            String[] parts = string.split(":");
            Enchantment enchantment = Enchantment.getByName(parts[0].toUpperCase());
            if (enchantment == null) {
                throw new InstructionParseException("Enchantment type '" + parts[0] + "' does not exist");
            }
            return new EnchantEObjective.EnchantmentData(enchantment);
        }
    }
}
