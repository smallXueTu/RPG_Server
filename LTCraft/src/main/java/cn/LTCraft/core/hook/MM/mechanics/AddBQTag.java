package cn.LTCraft.core.hook.MM.mechanics;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.utils.PlayerConverter;

public class AddBQTag extends SkillMechanic implements ITargetedEntitySkill {
    private String tag = null;
    public AddBQTag(String skill, MythicLineConfig mlc){
        super(skill, mlc);
        tag = mlc.getString(new String[]{"tag", "t",}, null);
    }
    @Override
    public boolean castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
        if (abstractEntity.isPlayer() && tag != null){
            BetonQuest.getInstance().getPlayerData(PlayerConverter.getID((Player) abstractEntity.getBukkitEntity())).addTag(tag);
            return true;
        }
        return false;
    }
}
