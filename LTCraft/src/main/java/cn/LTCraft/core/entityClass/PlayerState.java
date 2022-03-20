package cn.LTCraft.core.entityClass;

import cn.LTCraft.core.utils.Utils;
import org.bukkit.entity.Player;

public class PlayerState {
    private Cooling cooling;
    private Player player;
    private String messages;
    public PlayerState(Player player, String messages){
        this.player = player;
        this.messages = messages;
    }
    public PlayerState(Player player, String messages, Cooling cooling){
        this.player = player;
        this.messages = messages;
        this.cooling = cooling;
    }

    public Player getPlayer() {
        return player;
    }

    public String getMessages() {
        return messages.replace("%s%", Utils.formatNumber(cooling.getCooling()));
    }
    public boolean complete(){
        return cooling.getCooling() <= 0;
    }
    public interface Cooling{
        double getCooling();
    }
}
