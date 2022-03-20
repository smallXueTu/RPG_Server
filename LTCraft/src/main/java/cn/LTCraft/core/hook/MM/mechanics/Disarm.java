package cn.LTCraft.core.hook.MM.mechanics;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Disarm extends SkillMechanic implements ITargetedEntitySkill {
    public Disarm(String skill, MythicLineConfig mlc){
        super(skill, mlc);
    }

    @Override
    public boolean castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
        if (abstractEntity.isPlayer()){
            Player player = (Player) abstractEntity.getBukkitEntity();
            PlayerInventory inventory = player.getInventory();
            int emptySlot = -1;
            for (int i = 35; i > 0; i--) {
                if(inventory.getItem(i) == null || inventory.getItem(i).getTypeId() == 0){
                    emptySlot = i;
                    break;
                }
            }
            if (emptySlot == -1)return false;
            ItemStack itemStack = inventory.getItemInMainHand();
            inventory.setItemInMainHand(new ItemStack(Material.AIR));
            inventory.setItem(emptySlot, itemStack);
        }
        return true;
    }
}
