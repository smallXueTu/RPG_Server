package cn.LTCraft.core.hook.MM.mechanics;

import eos.moe.dragoncore.network.PacketSender;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import org.bukkit.entity.LivingEntity;

public class StartAnimation extends SkillMechanic implements ITargetedEntitySkill {
    private final String animation;
    private final int time;
    public StartAnimation(String skill, MythicLineConfig mlc){
        super(skill, mlc);
        animation = mlc.getString(new String[]{"animation", "a"}, "");
        time = mlc.getInteger(new String[]{"time", "t"}, 0);
    }

    @Override
    public boolean castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
        if (abstractEntity.isLiving())
            PacketSender.setModelEntityAnimation((LivingEntity) abstractEntity.getBukkitEntity(), animation, time);
        return false;
    }
}
