package cn.LTCraft.core.hook.BQ.condition;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.Collection;
import java.util.Optional;


public class SkillAPILevelCondition extends Condition {

    private int level;
    public SkillAPILevelCondition(Instruction instruction) throws InstructionParseException {
        super(instruction);
        level = instruction.getInt();
    }

    @Override
    public boolean check(String playerID) {
        PlayerData data = SkillAPI.getPlayerData(PlayerConverter.getPlayer(playerID));
        PlayerClass playerClass = data.getMainClass();
        return !playerClass.getData().getName().equals("无职业") && playerClass.getLevel() >= level;
    }

}
