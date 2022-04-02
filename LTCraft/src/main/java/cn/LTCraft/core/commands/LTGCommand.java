package cn.LTCraft.core.commands;

import cn.LTCraft.core.Config;
import cn.LTCraft.core.Main;
import cn.LTCraft.core.entityClass.MobSpawn;
import cn.LTCraft.core.entityClass.TeleportGate;
import cn.LTCraft.core.game.SpawnManager;
import cn.LTCraft.core.game.TeleportGateManager;
import cn.LTCraft.core.utils.GameUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_12_R1.block.CraftBlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Angel、 on 2022/3/27 16:27
 */
public class LTGCommand implements CommandExecutor, Listener {
    private static final List<String> def = new ArrayList<String>();
    public static Map<Player, List<Location>> mapList = new HashMap();
    public static Map<Player, String> mapGate = new HashMap();

    public LTGCommand(){
        Main plugin = Main.getInstance();
        plugin.getCommand("ltg").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String cmd, String[] args) {
        if (args.length < 1)return false;
        Player player = null;
        if (sender instanceof Player) player = (Player)sender;
        switch (args[0]){
            case "create":
            case "c":
                if (args.length < 2)return false;
                Map<String, Object> map = new HashMap<>();
                Location location = player.getLocation();
                map.put("to", location.getBlockX() + ":" + location.getBlockY() + ":" + location.getBlockZ() + ":" + location.getWorld().getName());
                map.put("locations", def);
                Config.getInstance().getGateYaml().set(args[1] , map);
                Config.getInstance().save();
                Config.getInstance().reload();
                TeleportGateManager.getInstance().reload();
                sender.sendMessage("§a添加成功！");
                break;
            case "set":
            case "s":
                if (args.length < 2)return false;
                mapList.put(player, new ArrayList<Location>());
                mapGate.put(player, args[1]);
                sender.sendMessage("§a请放置任意方块来设置，放置完成后输入/lts done 90/119/209");
            break;
            case "d":
            case "done":
                if (args.length < 2){
                    sender.sendMessage("§a/lts done 90/119/209");
                    return true;
                }
                List<String> stringList = Config.getInstance().getGateYaml().getStringList(mapGate.get(player) + ".locations");
                List<Location> locations = mapList.get(player);
                int i = Integer.parseInt(args[1]);
                int d = args.length > 2 ?Integer.parseInt(args[2]):0;
                for (Location loc : locations) {
                    CraftBlock blockAt = (CraftBlock) loc.getWorld().getBlockAt(loc);
                    if (blockAt.getType() != Material.AIR) {
                        blockAt.setTypeId(i, false);
                        blockAt.setData((byte) d, false);
                        String string = GameUtils.spawnLocationString(loc);
                        if (!stringList.contains(string))stringList.add(string);
                    }
                }
                Config.getInstance().getGateYaml().set(mapGate.get(player) + ".locations", stringList);
                mapList.remove(player);
                mapGate.remove(player);
                Config.getInstance().save();
                Config.getInstance().reload();
                TeleportGateManager.getInstance().reload();
                sender.sendMessage("§a保存完成。");
            break;
            case "delete":
            case "remove":
            case "rem":
            case "rm":
            case "del":
                if (args.length < 2)return false;
                Config.getInstance().getGateYaml().set(args[1], null);
                Config.getInstance().save();
                TeleportGateManager.getInstance().getGates().removeIf(teleportGate -> teleportGate.getGateName().equals(args[1]));
                sender.sendMessage("§a删除成功！");
                break;
            case "reload":
            case "r":
                Config.getInstance().reload();
                TeleportGateManager.getInstance().reload();
                sender.sendMessage("§a重载完成！");
                break;
        }
        return true;
    }
}
