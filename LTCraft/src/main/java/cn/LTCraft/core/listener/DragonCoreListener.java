package cn.LTCraft.core.listener;

import cn.LTCraft.core.Main;
import cn.LTCraft.core.game.Game;
import cn.LTCraft.core.utils.DragonCoreUtil;
import cn.LTCraft.core.utils.PlayerUtils;
import cn.ltcraft.item.objs.PlayerAttribute;
import eos.moe.dragoncore.api.event.PlayerSlotUpdateEvent;
import eos.moe.dragoncore.api.gui.event.CustomPacketEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class DragonCoreListener implements Listener {

    @EventHandler
    public void onSlotUpdateEvent(PlayerSlotUpdateEvent event){
        Player player = event.getPlayer();
        if (event.getIdentifier() == null)return;
        switch (event.getIdentifier()){
            case "材料格子":
                if (event.getItemStack() == null || event.getItemStack().getType() == Material.AIR){
                    DragonCoreUtil.setItemStack(player, "结果格子", new ItemStack(Material.AIR), null, true);
                    return;
                }
                if (event.getItemStack().getAmount() > 1){
                    player.getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
                        ItemStack itemStack = event.getItemStack().clone();
                        ItemStack itemStack2 = event.getItemStack().clone();
                        itemStack.setAmount(1);
                        itemStack2.setAmount(itemStack2.getAmount() - 1);
                        DragonCoreUtil.setItemStack(player, "材料格子", itemStack, (ignore) -> PlayerUtils.securityAddItem(player, itemStack2));
                    }, 1);
                }
                DragonCoreUtil.getItemStack(player, "装备格子", itemStack -> {
                    ItemStack forgingResult = Game.getForgingResult(itemStack, event.getItemStack(), player);
                    if (forgingResult != null){
                        DragonCoreUtil.setItemStack(player, "结果格子", forgingResult, null);
                    }
                });
            break;
            case "装备格子":
                if (event.getItemStack() == null || event.getItemStack().getType() == Material.AIR){
                    DragonCoreUtil.setItemStack(player, "结果格子", new ItemStack(Material.AIR), null, true);
                    return;
                }
                if (event.getItemStack().getAmount() > 1){
                    player.getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
                        ItemStack itemStack = event.getItemStack().clone();
                        ItemStack itemStack2 = event.getItemStack().clone();
                        itemStack.setAmount(1);
                        itemStack2.setAmount(itemStack2.getAmount() - 1);
                        DragonCoreUtil.setItemStack(player, "装备格子", itemStack, (ignore) -> PlayerUtils.securityAddItem(player, itemStack2));
                    }, 1);
                }
                DragonCoreUtil.getItemStack(player, "材料格子", itemStack -> {
                    ItemStack forgingResult = Game.getForgingResult(event.getItemStack(), itemStack, player);
                    if (forgingResult != null){
                        DragonCoreUtil.setItemStack(player, "结果格子", forgingResult, null);
                    }
                });
            break;
            case "结果格子":
                if (event.getItemStack() == null || event.getItemStack().getType() == Material.AIR){
                    DragonCoreUtil.getItemStack(player, "装备格子", itemStack -> {
                        if (itemStack !=null && itemStack.getType() != Material.AIR) {
                            DragonCoreUtil.getItemStack(player, "材料格子", itemStack1 -> {
                                if (itemStack1 != null && itemStack1.getType() != Material.AIR) {
                                    Game.startForging(itemStack, itemStack1, player);
                                    DragonCoreUtil.setItemStack(player, "装备格子", new ItemStack(Material.AIR), null);
                                    DragonCoreUtil.setItemStack(player, "材料格子", new ItemStack(Material.AIR), null);
                                }
                            });
                        }
                    });
                }
            break;
        }
        if (event.getIdentifier().startsWith("饰品槽位")){
            PlayerAttribute.getPlayerAttribute(event.getPlayer()).onChangeOrnament(Integer.parseInt(event.getIdentifier().substring(4)));
        }
    }

    @EventHandler
    public void onPacket(CustomPacketEvent e) {
        if (e.getIdentifier().equalsIgnoreCase("autoGPS")) {
            Player player = e.getPlayer();
            if (e.getData().size() > 0){
                if (e.getData().get(0).equals("true")){
                    PlayerUtils.addBQTag(player, "default.自动导航");
                }else{
                    PlayerUtils.removeBQTag(player, "default.自动导航");
                }
            }
        }
    }
}
