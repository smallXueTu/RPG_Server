package cn.LTCraft.core.commands;

import cn.LTCraft.core.Main;
import cn.LTCraft.core.game.Game;
import cn.LTCraft.core.task.PlayerClass;
import cn.LTCraft.core.entityClass.PlayerConfig;
import cn.LTCraft.core.utils.PlayerUtils;
import cn.LTCraft.core.utils.Utils;
import io.lumine.xikage.mythicmobs.io.MythicConfig;
import me.arasple.mc.trmenu.api.TrMenuAPI;
import me.arasple.mc.trmenu.api.event.MenuOpenEvent;
import me.arasple.mc.trmenu.module.display.Menu;
import net.minecraft.server.v1_12_R1.DamageSource;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class UseItemCommand implements CommandExecutor {
    private final Main plugin;
    public UseItemCommand(){
        plugin = Main.getInstance();
        plugin.getCommand("useitem").setExecutor(this);
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length <= 0){
            return false;
        }
        Player player = null;
        if (commandSender instanceof Player)
            player = (Player) commandSender;
        if (player == null)return true;
        switch (args[0]){
            case "非稳定钢质":
                if (!player.getWorld().getName().equals("t2"))return true;
                Location center = new Location(Bukkit.getWorld("t2"), 1000, 123, 3000);
                if (center.distance(player.getLocation()) < 1000){
                    player.teleport(new Location(Bukkit.getWorld("t2"), 1479, 93, 1965));
                }else {
                    player.teleport(center);
                }
                Player finalPlayer = player;
                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () ->
                        finalPlayer.getWorld().playSound(finalPlayer.getLocation(), "entity.generic.explode", 1, 1), 10);
            break;
            case "恶火之瓶":
                ((CraftPlayer)player).getHandle().setHealth(0);
                ((CraftPlayer)player).getHandle().getCombatTracker().trackDamage(DamageSource.BURN, 0f, 0f);
                ((CraftPlayer)player).getHandle().die(DamageSource.BURN);
            break;
            case "交易盘":
                World world = player.getWorld();
                switch (world.getName()){
                    case "t2":
                        if (new Location(world, 308, 24, -62).distance(player.getLocation()) < 100){
                            Menu menu = TrMenuAPI.INSTANCE.getMenuById("T2Exchange1");
                            if(menu != null){
                                menu.open(player, 0, MenuOpenEvent.Reason.PLAYER_COMMAND, menuSession -> {});
                            }
                        }else if(new Location(world, 476, 157, 60).distance(player.getLocation()) < 100){
                            Menu menu = TrMenuAPI.INSTANCE.getMenuById("T2Exchange2");
                            if(menu != null){
                                menu.open(player, 0, MenuOpenEvent.Reason.PLAYER_COMMAND, menuSession -> {});
                            }
                        }else if(new Location(world, 1000, 123, 3000).distance(player.getLocation()) < 100){
                            Menu menu = TrMenuAPI.INSTANCE.getMenuById("T2Exchange3");
                            if(menu != null){

                                menu.open(player, 0, MenuOpenEvent.Reason.PLAYER_COMMAND, menuSession -> {});
                            }
                        } else{
                            player.sendMessage("§c附近没有可交易资源。");
                        }
                    break;
                    case "t3":
                        if (new Location(world, 289, 9, 285).distance(player.getLocation()) < 100){
                            Menu menu = TrMenuAPI.INSTANCE.getMenuById("T3Exchange1");
                            if(menu != null){
                                menu.open(player, 0, MenuOpenEvent.Reason.PLAYER_COMMAND, menuSession -> {});
                            }
                        } else if (new Location(world, 289, 2, 454).distance(player.getLocation()) < 100){
                            Menu menu = TrMenuAPI.INSTANCE.getMenuById("T3Exchange2");
                            if(menu != null){
                                menu.open(player, 0, MenuOpenEvent.Reason.PLAYER_COMMAND, menuSession -> {});
                            }
                        } else{
                            player.sendMessage("§c附近没有可交易资源。");
                        }
                    break;
                    case "f1":
                        Menu menu = TrMenuAPI.INSTANCE.getMenuById("F1");
                        if(menu != null){
                            menu.open(player, 0, MenuOpenEvent.Reason.PLAYER_COMMAND, menuSession -> {});
                        }
                    break;
                    default:
                        player.sendMessage("§c附近没有可交易资源。");
                    break;
                }
            break;
            case "右键使用":
                ItemStack inHand = player.getItemInHand();
                int count = 0;
                String itemName = Utils.clearColor(inHand.getItemMeta().getDisplayName());
                PlayerClass playerClass = PlayerUtils.getClass(player);
                switch (itemName){
                    case "刺客知识精髓":
                    case "法师知识精髓":
                    case "战士知识精髓":
                    case "牧师知识精髓":
                        String itemClass = Utils.clearColor(inHand.getItemMeta().getDisplayName()).substring(0, 2);
                        if (playerClass.getName().equals(itemClass)){
                            MythicConfig classAtt = PlayerConfig.getPlayerConfig(player).getClassAttConfig();
                            Set<String> skills = classAtt.getKeys("skills");
                            if (skills == null)skills = new HashSet<>();
                            String skillName = Game.getClassSkill(playerClass);
                            if (skills.contains(skillName)){
                                int maxLevel = classAtt.getInteger("skills." + skillName + ".maxLevel");
                                if (maxLevel >= 5){
                                    player.sendMessage("§c你的§d" + skillName + "§c最大等级已达上限:5！");
                                    return true;
                                }
                                classAtt.set("skills." + skillName + ".maxLevel", classAtt.getInteger("skills." + skillName + ".maxLevel") + 1);
                            }else {
                                classAtt.set("skills." + skillName, new HashMap<String, Integer>(){
                                    {
                                        put("maxLevel", 1);
                                        put("level", 1);
                                        put("awakenLevel", 0);
                                    }
                                });
                            }
                            count = 1;
                            player.sendMessage("§a使用成功！");
                        }else {
                            player.sendMessage("§c你的职业与精髓类型不符合！");
                        }
                    break;
                    case "法杖知识卷轴":
                        MythicConfig classAtt = PlayerConfig.getPlayerConfig(player).getClassAttConfig();
                        Set<String> skills = classAtt.getKeys("skills");
                        String skillName = Game.getClassSkill(playerClass);
                        if (skills == null || !skills.contains(skillName)){
                            player.sendMessage("§c你还没学习职业基础技能！");
                            return true;
                        }
                        int level = classAtt.getInteger("skills." + skillName + ".level");
                        int maxLevel = classAtt.getInteger("skills." + skillName + ".maxLevel");
                        if (maxLevel <= level){
                            player.sendMessage("§c你的§d" + skillName + "§c已达最大等级，请使用§d" + playerClass.getName() + "知识精髓§c升级最大等级！");
                            return true;
                        }
                        count = level * 2;
                        if (inHand.getAmount() < count){
                            player.sendMessage("§c你需要" + count + "个§d" + itemName + "§c才能升级！");
                            return true;
                        }
                        classAtt.set("skills." + skillName + ".level", classAtt.getInteger("skills." + skillName + ".level") + 1);
                        player.sendMessage("§a使用成功！");
                    break;
                }
                inHand.setAmount(inHand.getAmount() - count);
                player.getInventory().setItemInMainHand(inHand);
            break;
        }
        return true;
    }
}
