package cn.LTCraft.core.other;

import cn.LTCraft.core.game.Game;
import cn.LTCraft.core.Main;
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
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class UseItemEffect {
    public static int onUse(Player player){
        return onUse(player, player.getItemInHand());
    }
    public static int onUse(Player player, ItemStack itemStack){
        String itemName = Utils.clearColor(itemStack.getItemMeta().getDisplayName());
        return onUse(player, itemName);
    }
    public static int onUse(Player player, String itemName){
        PlayerClass playerClass = PlayerUtils.getClass(player);
        ItemStack inHand = player.getItemInHand();
        int count = 0;
        switch (itemName){
            case "非稳定钢质":
                if (!player.getWorld().getName().equals("t2"))return count;
                Location center = new Location(Bukkit.getWorld("t2"), 1000, 123, 3000);
                if (center.distance(player.getLocation()) < 1000){
                    player.teleport(new Location(Bukkit.getWorld("t2"), 1479, 93, 1965));
                }else {
                    player.teleport(center);
                }
                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () ->
                        player.getWorld().playSound(player.getLocation(), "entity.generic.explode", 1, 1), 10);
                count++;
                break;
            case "恶火之瓶":
                ((CraftPlayer)player).getHandle().setHealth(0);
                ((CraftPlayer)player).getHandle().getCombatTracker().trackDamage(DamageSource.BURN, 0f, 0f);
                ((CraftPlayer)player).getHandle().die(DamageSource.BURN);
                count++;
                break;
            case "交易盘":
                World world = player.getWorld();
                String menuName = "";
                switch (world.getName()){
                    case "t2":
                        if (new Location(world, 308, 24, -62).distance(player.getLocation()) < 100){
                            menuName = "T2Exchange1";
                        }else if(new Location(world, 476, 157, 60).distance(player.getLocation()) < 100){
                            menuName = "T2Exchange2";
                        }else if(new Location(world, 1000, 123, 3000).distance(player.getLocation()) < 100){
                            menuName = "T2Exchange3";
                        } else{
                            player.sendMessage("§c附近没有可交易资源。");
                        }
                        break;
                    case "t3":
                        if (new Location(world, 289, 9, 285).distance(player.getLocation()) < 100){
                            menuName = "T3Exchange1";
                        } else if (new Location(world, 289, 2, 454).distance(player.getLocation()) < 100){
                            menuName = "T3Exchange2";
                        } else{
                            player.sendMessage("§c附近没有可交易资源。");
                        }
                        break;
                    case "f1":
                        menuName = "F1";
                        break;
                    case "f2":
                        menuName = "F2";
                        break;
                    default:
                        player.sendMessage("§c附近没有可交易资源。");
                        break;
                }
                if (!menuName.equals("")){
                    Menu menu = TrMenuAPI.INSTANCE.getMenuById(menuName);
                    if(menu != null){
                        menu.open(player, 0, MenuOpenEvent.Reason.PLAYER_COMMAND, menuSession -> {});
                    }
                }
                break;
            case "刺客知识精髓":
            case "法师知识精髓":
            case "战士知识精髓":
            case "牧师知识精髓":
                String itemClass = itemName.substring(0, 2);
                if (playerClass.getName().equals(itemClass)){
                    MythicConfig classAtt = PlayerConfig.getPlayerConfig(player).getClassAttConfig();
                    Set<String> skills = classAtt.getKeys("skills");
                    if (skills == null)skills = new HashSet<>();
                    String skillName = cn.LTCraft.core.game.Game.getClassSkill(playerClass);
                    if (skills.contains(skillName)){
                        int maxLevel = classAtt.getInteger("skills." + skillName + ".maxLevel");
                        if (maxLevel >= 5){
                            player.sendMessage("§c你的§d" + skillName + "§c最大等级已达上限:5！");
                            return count;
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
                    player.sendMessage("§a使用成功！");
                    count++;
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
                    return count;
                }
                int level = classAtt.getInteger("skills." + skillName + ".level");
                int maxLevel = classAtt.getInteger("skills." + skillName + ".maxLevel");
                if (maxLevel <= level){
                    player.sendMessage("§c你的§d" + skillName + "§c已达最大等级，请使用§d" + playerClass.getName() + "知识精髓§c升级最大等级！");
                    return count;
                }
                count = level * 2;
                if (inHand.getAmount() < count){
                    player.sendMessage("§c你需要" + count + "个§d" + itemName + "§c才能升级！");
                    return 0;
                }
                classAtt.set("skills." + skillName + ".level", classAtt.getInteger("skills." + skillName + ".level") + 1);
                player.sendMessage("§a使用成功！");
                inHand.setAmount(inHand.getAmount() - count);
                player.getInventory().setItemInMainHand(inHand);
                count = 0;
            break;
        }
        return count;
    }
}
