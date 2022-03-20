package cn.LTCraft.core.commands;

import cn.LTCraft.core.Main;
import cn.LTCraft.core.entityClass.Cooling;
import cn.LTCraft.core.utils.Utils;
import cn.LTCraft.core.utils.WorldUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class RtpCommand implements CommandExecutor {
    private final Main plugin;
    private final static List<String> allowWorld = Arrays.asList("zy", "world_nether", "world_the_end");
    public RtpCommand(){
        plugin = Main.getInstance();
        plugin.getCommand("rtp").setExecutor(this);
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        final Player player;
        if (commandSender instanceof Player){
            player = (Player) commandSender;
        }else return false;
        if (!allowWorld.contains(player.getWorld().getName())){
            player.sendMessage("§c你所在的世界禁止这个命令！");
            return true;
        }
        if (Cooling.isCooling(player, "RTP")) {
            return true;
        }
        boolean xA = Utils.getRandom().nextBoolean(),zA = Utils.getRandom().nextBoolean();
        Location location = null;
        Block block = null;
        World world = player.getWorld();
        double x = Utils.getRandom().nextInt(10000) + (xA?0:-10000) + player.getLocation().getX(),z = Utils.getRandom().nextInt(10000) + (zA?0:-10000) + player.getLocation().getZ(),y;
        for (int i = 0; i < 30; i++) {
            if (((CraftWorld)world).getHandle().dimension == -1){//地狱
                block = WorldUtils.getDownBlock(world.getBlockAt((int) x, 80, (int) z));
            }else {
                block = world.getHighestBlockAt((int) x, (int) z);
            }
            if (
                    block != null &&
                    block.getType() == Material.AIR &&
                    world.getBlockAt(block.getLocation().add(0, 1, 0)).getType() == Material.AIR &&
                    world.getBlockAt(block.getLocation().add(0, -1, 0)).getType().isSolid()
            ){
                location = block.getLocation();
                break;
            }
            x += xA?Utils.getRandom().nextInt(100): -Utils.getRandom().nextInt(100);
            z += zA?Utils.getRandom().nextInt(100): -Utils.getRandom().nextInt(100);
        }
        if (location != null){
            Cooling.cooling(player, "RTP", 30, "随机传送剩余冷却时间：%s%S");
            player.teleport(location);
            player.sendTitle("§l§6传送成功！", "§l§aX：" + Utils.formatNumber(x, 1) + ", Y：" + Utils.formatNumber(location.getBlockY() * 1.0, 1) + ", Z：" + Utils.formatNumber(z, 1) + "!");
            player.getWorld().playSound(player.getLocation(), "entity.generic.explode", 1, 1);
        }else {
            player.sendMessage("§c随机失败，请重试！");
        }
        return true;
    }
}
