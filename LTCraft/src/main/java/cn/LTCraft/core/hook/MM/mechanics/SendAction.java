package cn.LTCraft.core.hook.MM.mechanics;

import cn.LTCraft.core.utils.ClientUtils;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import org.bukkit.entity.Player;

public class SendAction extends SkillMechanic implements ITargetedEntitySkill {
    private String info;
    public SendAction(String info, MythicLineConfig mlc) {
        super(info, mlc);
        this.info = info.substring("sendAction".length());
    }
    @Override
    public boolean castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
        if (abstractEntity.isPlayer()){
            Player player = (Player) abstractEntity.getBukkitEntity();
            ClientUtils.sendActionMessage(player, info);
            return true;
        }
        return false;
    }
}
