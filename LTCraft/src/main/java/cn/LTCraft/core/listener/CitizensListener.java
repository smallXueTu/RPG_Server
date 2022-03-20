package cn.LTCraft.core.listener;

import cn.LTCraft.core.Main;
import cn.LTCraft.core.utils.Utils;
import net.citizensnpcs.api.event.NPCClickEvent;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.List;

public class CitizensListener implements org.bukkit.event.Listener {
    @EventHandler
    public void onNPCLeftClick(NPCLeftClickEvent event){
        onNPCClick(event);
    }
    @EventHandler
    public void onNPCRightClick(NPCRightClickEvent event) {
        onNPCClick(event);
    }
    @EventHandler
    public void onNPCClick(NPCClickEvent event){
        if (Main.getInstance().getConfigOBJ().getNPCClickYaml().contains(event.getNPC().getId() + "")){
            int id = event.getNPC().getId();
            Player player = event.getClicker();
            YamlConfiguration config = Main.getInstance().getConfigOBJ().getNPCClickYaml();
            if (config.contains(id + "." + "commands")){
                List<String> commands = config.getStringList(id + "." + "commands");
                for (String command : commands) {
                    command = command.replace("%player%", player.getName());
                    Bukkit.getServer().dispatchCommand(player, command);
                }
            }
            if (config.contains(id + "." + "sudoCommands")){
                List<String> commands = config.getStringList(id + "." + "sudoCommands");
                for (String command : commands) {
                    command = command.replace("%player%", player.getName());
                    boolean isOp = player.isOp();
                    player.setOp(true);
                    Bukkit.getServer().dispatchCommand(player, command);
                    player.setOp(isOp);
                }
            }
            if (config.contains(id + "." + "messages")){
                List<String> messages = config.getStringList(id + "." + "messages");
                messages.forEach(player::sendMessage);
            }
            if (config.contains(id + "." + "randMessages")){
                List<String> messages = config.getStringList(id + "." + "randMessages");
                player.sendMessage(messages.get(Utils.getRandom().nextInt(messages.size())));
            }
        }
    }
}
