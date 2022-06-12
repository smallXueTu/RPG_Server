package cn.LTCraft.core.hook.MM.mechanics;

import cn.LTCraft.core.utils.PlayerUtils;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import io.lumine.xikage.mythicmobs.skills.placeholders.parsers.PlaceholderString;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TargetCommand extends SkillMechanic implements ITargetedEntitySkill {
    protected PlaceholderString command;
    protected boolean asOP = false;

    public TargetCommand(String skill, MythicLineConfig mlc) {
        super(skill, mlc);
        this.ASYNC_SAFE = false;
        this.target_creative = true;
        this.command = PlaceholderString.of(mlc.getString(new String[]{"command", "cmd", "c"}));
        this.asOP = mlc.getBoolean(new String[]{"asop", "op"}, false);
    }

    @Override
    public boolean castAtEntity(SkillMetadata data, AbstractEntity target) {
        String parsedCommand = this.command.get(data, target);
        if (target.isPlayer()) {
            Player player = (Player) target.getBukkitEntity();
            if (this.asOP) {
                PlayerUtils.sudoExec(player, parsedCommand);
            } else {
                Bukkit.getServer().dispatchCommand(player, parsedCommand);
            }
        }
        return true;
    }
}
