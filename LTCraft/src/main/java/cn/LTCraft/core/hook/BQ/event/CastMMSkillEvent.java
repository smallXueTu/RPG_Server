package cn.LTCraft.core.hook.BQ.event;

import cn.LTCraft.core.utils.PlayerUtils;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.skills.Skill;
import io.lumine.xikage.mythicmobs.skills.SkillManager;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Created by Angel、 on 2022/3/24 10:54
 */
public class CastMMSkillEvent extends QuestEvent {
    private final String skillName;
    public CastMMSkillEvent(Instruction instruction) throws InstructionParseException {
        super(instruction);
        skillName = instruction.next();
    }
    @Override
    public void run(String playerID) throws QuestRuntimeException {
        Player player =  PlayerConverter.getPlayer(playerID);
        SkillManager skillManager = MythicMobs.inst().getSkillManager();
        PlayerUtils.castMMSkill(player, skillName);
    }
}
