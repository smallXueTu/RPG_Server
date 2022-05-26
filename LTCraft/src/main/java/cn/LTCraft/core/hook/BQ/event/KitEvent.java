package cn.LTCraft.core.hook.BQ.event;

import cn.LTCraft.core.Main;
import cn.LTCraft.core.utils.PlayerUtils;
import com.earth2me.essentials.Kit;
import com.earth2me.essentials.User;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Created by Angel、 on 2022/5/26 18:33
 */
public class KitEvent extends QuestEvent {
    private final String kit;
    public KitEvent(Instruction instruction) throws InstructionParseException {
        super(instruction);
        kit = instruction.next();
    }

    @Override
    public void run(String playerID) throws QuestRuntimeException {
        Player player = PlayerConverter.getPlayer(playerID);
        PlayerUtils.giveKits(player, kit);
    }
}
