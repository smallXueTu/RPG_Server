package cn.LTCraft.core.hook.MM.conditions;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.SkillCondition;
import io.lumine.xikage.mythicmobs.skills.conditions.IEntityCondition;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.List;

public class HasObjective extends SkillCondition implements IEntityCondition {
    private String objective;
    public HasObjective(String line, MythicLineConfig config) {
        super(line);
        objective = config.getString(new String[]{"objective", "obj", "o"});
    }

    @Override
    public boolean check(AbstractEntity abstractEntity) {
        if (abstractEntity == null)return false;
        if (abstractEntity.getBukkitEntity() instanceof Player) {
            String playerID = PlayerConverter.getID((Player) abstractEntity.getBukkitEntity());
            List<Objective> list = BetonQuest.getInstance().getPlayerObjectives(playerID);
            for (Objective objective1 : list) {
                if (objective1.getLabel().equalsIgnoreCase(objective))return true;
            }
        }
        return false;
    }
}