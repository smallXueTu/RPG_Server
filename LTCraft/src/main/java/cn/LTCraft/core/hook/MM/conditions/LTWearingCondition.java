package cn.LTCraft.core.hook.MM.conditions;

import cn.LTCraft.core.entityClass.ClutterItem;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.logging.MythicLogger;
import io.lumine.xikage.mythicmobs.skills.SkillCondition;
import io.lumine.xikage.mythicmobs.skills.conditions.IEntityCondition;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Angel、 on 2022/3/23 0:07
 */
public class LTWearingCondition extends SkillCondition implements IEntityCondition {
    private final String slot;
    private final ClutterItem clutterItem;
    public LTWearingCondition(String line, MythicLineConfig mlc) {
        super(line);
        String s = mlc.getString(new String[]{"armorslot", "slot", "s"}, "helmet").toLowerCase();
        String item = mlc.getString(new String[]{"material", "mat", "m", "mythicmobsitem", "mmitem", "mmi", "item"}, "DIRT", this.conditionVar);
        this.slot = s.toLowerCase();
        clutterItem = ClutterItem.spawnClutterItem(item);
    }

    public boolean check(AbstractEntity e) {
        ItemStack slotItem = null;
        if (e.isLiving()) {
            switch (this.slot) {
                case "helmet":
                    slotItem = ((LivingEntity) e.getBukkitEntity()).getEquipment().getHelmet();
                    break;
                case "chestplate":
                    slotItem = ((LivingEntity) e.getBukkitEntity()).getEquipment().getChestplate();
                    break;
                case "leggings":
                    slotItem = ((LivingEntity) e.getBukkitEntity()).getEquipment().getLeggings();
                    break;
                case "boots":
                    slotItem = ((LivingEntity) e.getBukkitEntity()).getEquipment().getBoots();
                    break;
                case "mainhand":
                    slotItem = ((LivingEntity) e.getBukkitEntity()).getEquipment().getItemInMainHand();
                    break;
                case "offhand":
                    slotItem = ((LivingEntity) e.getBukkitEntity()).getEquipment().getItemInOffHand();
                    break;
                default:
                    MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Invalid slot used: {0}", this.slot);
                    break;
            }

            if (slotItem == null) {
                MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Slot item was null, returning false");
                return false;
            } else {
                return clutterItem.isSimilar(slotItem);
            }
        } else {
            return false;
        }
    }
}
