package cn.LTCraft.core.hook.BQ.event;

import cn.LTCraft.core.Main;
import org.bukkit.Bukkit;
import pl.betoncraft.betonquest.*;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.config.ConfigPackage;

public class DelayEventEvent extends QuestEvent {
    private int delay;
    private String event;
    public DelayEventEvent(Instruction instruction) throws InstructionParseException {
        super(instruction);
        delay = Integer.parseInt(instruction.next());
        event = instruction.next();
    }

    @Override
    public void run(String playerID) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
            String[] events = event.split("\\.");
            String pak = events.length > 1 ?instruction.getPackage().getName():events[0];
            ConfigPackage configPackage = Config.getPackages().get(pak);
            String event = events.length > 1 ?events[1]:events[0];
            EventID eventID = null;
            try {
                eventID = new EventID(configPackage, event);
                BetonQuest.event(playerID, eventID);
            } catch (ObjectNotFoundException e) {
                e.printStackTrace();
            }
        }, delay);
    }
}
