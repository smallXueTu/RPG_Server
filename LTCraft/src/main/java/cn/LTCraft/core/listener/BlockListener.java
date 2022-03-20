package cn.LTCraft.core.listener;

import cn.LTCraft.core.other.exceptions.BlockCeilingException;
import cn.LTCraft.core.Main;
import cn.LTCraft.core.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Directional;

import java.util.ArrayList;
import java.util.Map;

public class BlockListener implements Listener{
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){
        Player player = e.getPlayer();
        Block block = e.getBlock();
        if (player.hasPermission("LTCraft.vip") && player.isSneaking()) {
            if (Game.isMine(block)) {
                try {
                    ItemStack ItemStack = new ItemStack(278, 1, (short) 1);
                    Map<Enchantment, Integer> enchantments = player.getInventory().getItemInMainHand().getEnchantments();
                    ItemStack.addEnchantments(enchantments);
                    Game.breakMines(block, 0, new ArrayList<>(), ItemStack, 0);
                } catch (BlockCeilingException var7) {
                    //
                }
            }else if (Game.isWood(block)){
                try {
                    ItemStack ItemStack = new ItemStack(279, 1, (short) 1);
                    Map<Enchantment, Integer> enchantments = player.getInventory().getItemInMainHand().getEnchantments();
                    ItemStack.addEnchantments(enchantments);
                    Game.breakWoods(block, 0, new ArrayList<>(), ItemStack, 0);
                } catch (BlockCeilingException var7) {
                    //
                }
            }
        }
        World world = player.getWorld();
        if (Game.resourcesWorlds.contains(world.getName())) {
            Location pos = world.getSpawnLocation();
            Location blockPos = block.getLocation();
            blockPos.setY(pos.getY());
            if (pos.distance(blockPos) < 16 && !player.isOp()) {
                player.sendMessage("§c你没有权限破坏出生点！");
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlaceEvent(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        Block block = e.getBlock();
        World world = player.getWorld();
        if (Game.resourcesWorlds.contains(world.getName())) {
            Location pos = world.getSpawnLocation();
            Location blockPos = block.getLocation();
            blockPos.setY(pos.getY());
            if (pos.distance(blockPos) < 16 && !player.isOp()) {
                player.sendMessage("§c你没有权限破坏出生点！");
                e.setCancelled(true);
            }
        }
        if (Game.rpgWorlds.contains(world.getName())){
            Location location = player.getLocation();
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
                if (e.isCancelled())player.teleport(location);
            }, 1);
        }
    }

    @EventHandler
    public void onPlaceEvent(BlockFromToEvent e) {
        Block block = e.getBlock();
        World world = block.getWorld();
        if (Game.resourcesWorlds.contains(world.getName()) && (block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER || block.getType() == Material.LAVA || block.getType() == Material.STATIONARY_LAVA || block.getType() == Material.AIR)) {
            Location pos = world.getSpawnLocation();
            Location blockPos = block.getLocation();
            blockPos.setY(pos.getY());
            if (pos.distance(blockPos) < 16) {
                e.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void onBlockDispense(BlockDispenseEvent e) {
        Block b = e.getBlock();
        if (b.getType() == Material.DISPENSER) {
            BlockFace f = ((Directional)b.getState().getData()).getFacing();
            boolean y0FacingDown = b.getY() == 0 && f == BlockFace.DOWN;
            boolean yMaxFacingDown = b.getY() == b.getWorld().getMaxHeight() - 1 && f == BlockFace.UP;
            if ((y0FacingDown || yMaxFacingDown) && e.getItem().getType().name().toLowerCase().endsWith("shulker_box")) {
                e.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void onBlockRedstone(BlockRedstoneEvent event){

    }
    @EventHandler
    public void onPortalCreate(PortalCreateEvent event){
        if (event.getReason() == PortalCreateEvent.CreateReason.OBC_DESTINATION) {
            event.setCancelled(true);
        }
    }
}
