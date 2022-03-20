package cn.LTCraft.core.hook.MM.conditions;

import cn.LTCraft.core.other.Temp;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.SkillCondition;
import io.lumine.xikage.mythicmobs.skills.conditions.IEntityCondition;

/**
 * 是否被沉默
 */
public class IsSilence extends SkillCondition implements IEntityCondition {
    public IsSilence(String line, MythicLineConfig config) {
        super(line);
    }
    @Override
    public boolean check(AbstractEntity abstractEntity) {
        return Temp.silence.containsKey(abstractEntity.getBukkitEntity());
    }
}
