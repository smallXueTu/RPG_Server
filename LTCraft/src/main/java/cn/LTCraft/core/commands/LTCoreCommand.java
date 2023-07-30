package cn.LTCraft.core.commands;

import cn.LTCraft.core.Main;
import cn.LTCraft.core.entityClass.ClutterItem;
import cn.LTCraft.core.game.Game;
import cn.LTCraft.core.task.ClientCheckTask;
import cn.LTCraft.core.task.LTCraftRestartRunnable;
import cn.LTCraft.core.utils.ClientUtils;
import cn.LTCraft.core.utils.FileUtil;
import cn.LTCraft.core.utils.PlayerUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftMetaBook;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.BookMeta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class LTCoreCommand implements CommandExecutor {
    private final Main plugin;
    protected LTCraftRestartRunnable restartRunnable = null;
    protected LTCoreCommand() {
        plugin = Main.getInstance();
        plugin.getCommand("lt").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String ss, String[] args) {
        Player player = null;
        if (commandSender instanceof Player){
            player = (Player) commandSender;
        }
        if (!commandSender.isOp())return true;
        if (args.length<1){
            commandSender.sendMessage("/lt String");
            return true;
        }
        switch (args[0]){
            case "clear":
                World world = player.getWorld();
                List<Entity> entities =  world.getEntities();
                for (Entity entity : entities) {
                    if (!(entity instanceof Player) && entity.getLocation().distance(player.getLocation()) < Integer.parseInt(args[1])) {
                        entity.remove();
                        player.sendMessage(entity.getName());
                    }
                }
                break;
            case "r":
            case "reload":
                plugin.getConfigOBJ().reload();
                commandSender.sendMessage("§a重载成功！");
                break;
            case "重启":
                restartRunnable = new LTCraftRestartRunnable(Integer.parseInt(args[1]));
                Bukkit.getServer().broadcastMessage("§c§lLTCraft将在" + args[1] + "秒后进行重启!");
                break;
            case "取消重启":
                if (restartRunnable != null){
                    restartRunnable.cancel();
                    restartRunnable = null;
                    Bukkit.getServer().broadcastMessage("§c§l重启被取消！");
                }
                break;
            case "reset":
                if (args.length<2){
                    commandSender.sendMessage("/lt reset worldName");
                    return true;
                }
                Game.resetWorld(args[1]);
                break;
            case "getitem":
                ClutterItem clutterItem = ClutterItem.spawnClutterItem(args[1]);
                if (clutterItem.getItemSource() != null){
                    PlayerUtils.securityAddItem(player, clutterItem.generate());
                }else {
                    commandSender.sendMessage("§c不存在物品：" + args[1]);
                }
            break;
            case "att":
                if (args.length<3){
                    commandSender.sendMessage("/lt att attName value");
                    return true;
                }
                switch (args[1]){
                    case "飞行速度":
                        CraftPlayer craftPlayer = (CraftPlayer)player;
                        craftPlayer.setFlySpeed(Float.parseFloat(args[2]));
                    break;
                }
                break;
            case "entities":
                Player target;
                if (args.length >= 2){
                    target = Bukkit.getPlayer(args[1]);
                }else {
                    target = player;
                }
                World targetWorld = target.getWorld();
                List<Entity> entities1 = targetWorld.getEntities();
                for (Entity entity : entities1) {
                    System.out.println(entity.getCustomName());
                }
                break;
            case "add":
                if (args.length<3){
                    commandSender.sendMessage("/lt add {玩家} screen/json/mod");
                    return true;
                }
                player = commandSender.getServer().getPlayerExact(args[1]);
                if (player==null){
                    commandSender.sendMessage("玩家不在线。");
                    return true;
                }
                String s;
                switch (args[2]){
                    case "screen":
                        s = (char)0 + "{\"id\":\"+"+ Main.id +"\",\"results\":\"ScreenCapture\"}";
                        player.sendPluginMessage(plugin ,"LTCraft",  s.getBytes());
                        Main.getInstance().addTack(Main.id++, new ClientCheckTask(Main.id-1, "screen", player));
                        commandSender.sendMessage("已发送screen请求。");
                        break;
                    case "json":
                        s = FileUtil.readToString(plugin.getDataFolder()+"/tmp.json");
                        s = (char)0 + s;
                        player.sendPluginMessage(plugin ,"LTCraft",  s.getBytes());
                        Main.getInstance().addTack(Main.id++, new ClientCheckTask(Main.id-1, "json", player));
                        commandSender.sendMessage("已发送json动作。");
                        break;
                    case "mod":
                        s = (char)0 + "{\"id\":\"+"+ Main.id +"\",\"results\":\"CheckMod\"}";
                        player.sendPluginMessage(Main.getInstance() ,"LTCraft",  s.getBytes());
                        Main.getInstance().addTack(Main.id++, new ClientCheckTask(Main.id-1, "checkMod", player));
                        commandSender.sendMessage("已发送mod动作。");
                        break;
                    case "action":
                        ClientUtils.sendActionMessage(player, args[3]);
                        commandSender.sendMessage("已发送action动作。");
                        break;
                }
                break;
        }
        return true;
    }
}
