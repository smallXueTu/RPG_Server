package cn.LTCraft.core.hook.BQ.event;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.classes.RPGClass;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.listener.AttributeListener;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.utils.PlayerConverter;


public class ChangeClassEvent extends QuestEvent {
    private String clazz = "";
    private String group = "";
    public ChangeClassEvent(Instruction instruction) throws InstructionParseException {
        super(instruction);
        group = instruction.next();
        clazz = instruction.next();
    }


    @Override
    public void run(String playerID) throws QuestRuntimeException {
        Player player =  PlayerConverter.getPlayer(playerID);
        PlayerClass data = SkillAPI.getPlayerData(player).getClass(group);
        if (data == null)throw new QuestRuntimeException("无法修改"+player.getName()+"职业，找不到群："+group);
        RPGClass target = SkillAPI.getClass(clazz);
        if (target == null)throw new QuestRuntimeException("无法修改"+player.getName()+"职业，找不到职业："+clazz);
        data.setClassData(target);
        SkillAPI.getPlayerData(player).updateScoreboard();
        AttributeListener.updatePlayer(SkillAPI.getPlayerData(player));
    }
}
