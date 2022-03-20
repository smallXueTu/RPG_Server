package cn.LTCraft.core.hook.BQ.event;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerClass;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.utils.PlayerConverter;

public class SetClassLevelEvent extends QuestEvent {
    private int level;
    public SetClassLevelEvent(Instruction instruction) throws InstructionParseException {
        super(instruction);
        level = Math.max(instruction.getInt(), 1);
    }
    @Override
    public void run(String playerID){
        Player player =  PlayerConverter.getPlayer(playerID);
        PlayerClass data = SkillAPI.getPlayerData(player).getMainClass();
        data.setLevel(level);
        data.setExp(0);
        data.setPoints((int) (data.getData().getGroupSettings().getPointsPerLevel() * level));
        data.getPlayerData().setAttribPoints((int) (data.getData().getGroupSettings().getAttribsPerLevel() * (level - 1)));
    }
}
