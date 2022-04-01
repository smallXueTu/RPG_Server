package cn.LTCraft.core.hook.BQ.event;

import cn.LTCraft.core.entityClass.PlayerConfig;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Created by Angel、 on 2022/4/1 13:14
 */
public class StealGoldEvent extends QuestEvent {
    private double quantity = 0;
    public StealGoldEvent(Instruction instruction) throws InstructionParseException {
        super(instruction);
        quantity = instruction.getDouble();
    }
    @Override
    public void run(String playerID) throws QuestRuntimeException {
        Player player = PlayerConverter.getPlayer(playerID);
        PlayerConfig.getPlayerConfig(player).getPlayerInfo().reduceGold(quantity);
    }
}
