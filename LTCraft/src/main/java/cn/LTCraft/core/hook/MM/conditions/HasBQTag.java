package cn.LTCraft.core.hook.MM.conditions;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.SkillCondition;
import io.lumine.xikage.mythicmobs.skills.conditions.IEntityCondition;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.database.PlayerData;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.List;

public class HasBQTag extends SkillCondition implements IEntityCondition {
    private String tag;
    public HasBQTag(String line, MythicLineConfig config) {
        super(line);
        tag = config.getString(new String[]{"t", "tag"});
    }

    @Override
    public boolean check(AbstractEntity abstractEntity) {
        if (abstractEntity == null)return false;
        if (abstractEntity.getBukkitEntity() instanceof Player) {
            String playerID = PlayerConverter.getID((Player) abstractEntity.getBukkitEntity());
            PlayerData playerData = BetonQuest.getInstance().getPlayerData(playerID);
            return playerData.hasTag(tag);
        }
        return false;
    }
}