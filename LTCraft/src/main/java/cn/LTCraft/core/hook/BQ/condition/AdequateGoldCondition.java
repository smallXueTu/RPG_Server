package cn.LTCraft.core.hook.BQ.condition;

import cn.LTCraft.core.entityClass.PlayerConfig;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Created by Angel、 on 2022/4/1 12:33
 */
public class AdequateGoldCondition extends Condition {
    private double quantity = 0;
    public AdequateGoldCondition(Instruction instruction) throws InstructionParseException {
        super(instruction);
        quantity = instruction.getDouble();
    }

    @Override
    public boolean check(String playerID) throws QuestRuntimeException {
        Player player = PlayerConverter.getPlayer(playerID);
        return PlayerConfig.getPlayerConfig(player).getPlayerInfo().getGold() >= quantity;
    }

}
