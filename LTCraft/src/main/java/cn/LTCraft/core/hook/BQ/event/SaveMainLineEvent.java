package cn.LTCraft.core.hook.BQ.event;

import cn.ltcraft.teleport.Home;
import cn.ltcraft.teleport.Teleport;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.utils.LocationData;
import pl.betoncraft.betonquest.utils.PlayerConverter;


public class SaveMainLineEvent extends QuestEvent {
    private LocationData loc = null;
    public SaveMainLineEvent(Instruction instruction) throws InstructionParseException {
        super(instruction);
        String next = instruction.next();
        if (!next.equals("underfoot")) {
            this.loc = instruction.getLocation(next);
        }
    }

    @Override
    public void run(String playerID) throws QuestRuntimeException {
        Player player =  PlayerConverter.getPlayer(playerID);
        if (loc != null){
            Teleport.getInstance().getPlayerHomes().get(player.getName()).put("mainline", new Home(loc.getLocation(playerID)));
        }else{
            Teleport.getInstance().getPlayerHomes().get(player.getName()).put("mainline", new Home(player.getLocation().clone()));
        }
    }
}
