package cn.LTCraft.core.hook.MM.mechanics;

import cn.LTCraft.core.entityClass.ClutterItem;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class ModifySlotItem extends SkillMechanic implements ITargetedEntitySkill  {
    private final String slot;
    private ClutterItem clutterItem;

    public ModifySlotItem(String skill, MythicLineConfig mlc){
        super(skill, mlc);
        target_creative = true;
        slot = mlc.getString(new String[]{"slot", "s"}, "HAND");
        String stringItem = mlc.getString(new String[]{"item", "i"}, "AIR");
        int amount = mlc.getInteger(new String[]{"amount", "a", "count", "c"}, 1);
        clutterItem = ClutterItem.spawnClutterItem(stringItem);
        clutterItem.setNumber(amount);
    }
    @Override
    public boolean castAtEntity(SkillMetadata skillMetadata, AbstractEntity target) {
        if (target.isPlayer()) {
            Entity entity = target.getBukkitEntity();
            if (entity instanceof Player){
                Player player = (Player)entity;
                switch (slot){
                    case "HAND":
                        player.getInventory().setItemInMainHand(clutterItem.generate());
                    break;
                    case "OFFHAND":
                        player.getInventory().setItemInOffHand(clutterItem.getItemStack());
                    break;
                    default:
                        int slot;
                        try {
                            slot = Integer.parseInt(this.slot);
                        }catch (NumberFormatException e){
                            return false;
                        }
                        player.getInventory().setItem(slot, clutterItem.getItemStack());
                    break;
                }
                return true;
            }
        }
        return false;
    }
}
