package cn.LTCraft.core.hook.MM.mechanics;

import cn.LTCraft.core.Main;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.EventID;
import pl.betoncraft.betonquest.ObjectNotFoundException;
import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.utils.PlayerConverter;

public class ExecBQEvent extends SkillMechanic implements ITargetedEntitySkill {
    private String event = "";
    private EventID eventID = null;
    public ExecBQEvent(String skill, MythicLineConfig mlc){
        super(skill, mlc);
        event = mlc.getString(new String[]{"event", "e",}, "");
    }
    @Override
    public boolean castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
        if (abstractEntity.isPlayer()){
            try {
                eventID = new EventID((ConfigPackage)null, event);
            } catch (ObjectNotFoundException e) {
                Main.getInstance().getLogger().warning("找不到BQEvent：" + event);
            }
            Player player = (Player) abstractEntity.getBukkitEntity();
            BetonQuest.event(PlayerConverter.getID(player), eventID);
            return true;
        }
        return false;
    }
}
